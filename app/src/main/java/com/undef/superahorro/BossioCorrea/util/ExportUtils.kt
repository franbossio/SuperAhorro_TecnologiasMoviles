package com.undef.superahorro.BossioCorrea.util

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.undef.superahorro.BossioCorrea.domain.model.Compra
import java.io.File
import java.io.FileOutputStream
import java.time.format.DateTimeFormatter
import java.util.Locale

private val FMT_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy")
private val FMT_HORA  = DateTimeFormatter.ofPattern("HH:mm")

private fun exportsDir(context: Context): File =
    File(context.cacheDir, "exports").apply { mkdirs() }

private fun uriParaArchivo(context: Context, file: File): Uri =
    FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

private fun csvEscape(valor: String): String {
    val escapado = valor.replace("\"", "\"\"")
    return if (valor.any { it == ',' || it == '"' || it == '\n' }) "\"$escapado\"" else escapado
}

/** Genera un CSV con una fila por producto comprado y devuelve su Uri (vía FileProvider). */
fun exportarComprasCsv(context: Context, compras: List<Compra>): Uri {
    val sb = StringBuilder("Fecha,Hora,Supermercado,Producto,Cantidad,Precio Unitario,Subtotal,Total Compra\n")
    compras.forEach { compra ->
        val fecha = compra.fecha.format(FMT_FECHA)
        val hora  = compra.hora.format(FMT_HORA)
        val total = "%.2f".format(Locale.US, compra.total)
        val supermercado = csvEscape(compra.supermercado)
        if (compra.productos.isEmpty()) {
            sb.append("$fecha,$hora,$supermercado,,,,,$total\n")
        } else {
            compra.productos.forEach { producto ->
                sb.append(fecha).append(',')
                    .append(hora).append(',')
                    .append(supermercado).append(',')
                    .append(csvEscape(producto.nombre)).append(',')
                    .append(producto.cantidad).append(',')
                    .append("%.2f".format(Locale.US, producto.precio)).append(',')
                    .append("%.2f".format(Locale.US, producto.subtotal)).append(',')
                    .append(total).append('\n')
            }
        }
    }

    val file = File(exportsDir(context), "superahorro_compras.csv")
    file.writeText(sb.toString(), Charsets.UTF_8)
    return uriParaArchivo(context, file)
}

/** Genera un PDF con el detalle de cada compra y devuelve su Uri (vía FileProvider). */
fun exportarComprasPdf(context: Context, compras: List<Compra>): Uri {
    val pageWidth  = 595
    val pageHeight = 842
    val margin     = 40f

    val titlePaint  = Paint().apply { textSize = 18f; isFakeBoldText = true }
    val mutedPaint  = Paint().apply { textSize = 9f; color = Color.GRAY }
    val headerPaint = Paint().apply { textSize = 11f; isFakeBoldText = true }
    val textPaint   = Paint().apply { textSize = 10f }

    val document = PdfDocument()
    var pageNumero = 1
    var page   = document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumero).create())
    var canvas = page.canvas
    var y = margin

    fun nuevaPagina() {
        document.finishPage(page)
        pageNumero++
        page   = document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumero).create())
        canvas = page.canvas
        y = margin
    }

    fun asegurarEspacio(alto: Float) {
        if (y + alto > pageHeight - margin) nuevaPagina()
    }

    canvas.drawText("SuperAhorro — Historial de compras", margin, y, titlePaint)
    y += 26f
    canvas.drawText(
        "Total gastado: \$ %,.2f  ·  %d compras".format(compras.sumOf { it.total }, compras.size),
        margin, y, mutedPaint
    )
    y += 24f

    compras.forEach { compra ->
        asegurarEspacio(18f)
        canvas.drawText(
            "${compra.fecha.format(FMT_FECHA)}  ${compra.hora.format(FMT_HORA)}  ·  ${compra.supermercado}  ·  Total: \$ %,.2f".format(compra.total),
            margin, y, headerPaint
        )
        y += 16f
        compra.productos.forEach { producto ->
            asegurarEspacio(14f)
            canvas.drawText(
                "•  ${producto.nombre}   x${producto.cantidad}   \$ %,.2f".format(producto.subtotal),
                margin + 14f, y, textPaint
            )
            y += 14f
        }
        y += 10f
    }

    document.finishPage(page)

    val file = File(exportsDir(context), "superahorro_compras.pdf")
    FileOutputStream(file).use { document.writeTo(it) }
    document.close()
    return uriParaArchivo(context, file)
}
