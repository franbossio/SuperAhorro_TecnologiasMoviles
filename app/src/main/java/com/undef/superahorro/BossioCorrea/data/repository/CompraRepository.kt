package com.undef.superahorro.BossioCorrea.data.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.undef.superahorro.BossioCorrea.domain.model.Compra
import com.undef.superahorro.BossioCorrea.domain.model.PrecioProducto
import com.undef.superahorro.BossioCorrea.domain.model.Producto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

/**
 * Compras en Firestore: colección "compras", un documento por compra
 * con sus productos embebidos como lista (equivale al JOIN compras-productos
 * que había en Room). Firestore cachea offline automáticamente.
 */
class CompraRepository {

    private val compras = FirebaseFirestore.getInstance().collection("compras")

    // ── Observar compras del usuario ──────────────────────────────────────────
    //
    // callbackFlow envuelve el listener en tiempo real de Firestore: cada vez
    // que cambia algo en la nube (o localmente offline) emite la lista nueva.

    fun getComprasFlow(usuarioId: String): Flow<List<Compra>> = callbackFlow {
        val registro = compras
            .whereEqualTo("usuarioId", usuarioId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val lista = snapshot?.documents
                    ?.mapNotNull { it.toCompra() }
                    ?.sortedByDescending { it.fecha.atTime(it.hora) }
                    ?: emptyList()
                trySend(lista)
            }
        awaitClose { registro.remove() }
    }

    // ── Guardar compra con productos ──────────────────────────────────────────

    suspend fun guardarCompra(
        usuarioId    : String,
        fecha        : String,
        hora         : String,
        supermercado : String,
        total        : Double,
        productos    : List<Producto>,
        ticketUri    : String? = null
    ): String {
        val doc = compras.add(
            mapOf(
                "usuarioId"      to usuarioId,
                "fecha"          to fecha,
                "hora"           to hora,
                "supermercado"   to supermercado,
                "total"          to total,
                "ticketImageUri" to ticketUri,
                "productos"      to productos.map { it.toMap() }
            )
        ).await()
        return doc.id
    }

    suspend fun agregarProducto(compraId: String, producto: Producto) {
        val conId = if (producto.id.isBlank())
            producto.copy(id = UUID.randomUUID().toString()) else producto
        val doc       = compras.document(compraId).get().await()
        val actuales  = doc.productosEmbebidos()
        compras.document(compraId)
            .update("productos", (actuales + conId).map { it.toMap() })
            .await()
    }

    suspend fun eliminarCompra(compraId: String) {
        compras.document(compraId).delete().await()
    }

    suspend fun eliminarProducto(compraId: String, productoId: String) {
        val doc      = compras.document(compraId).get().await()
        val restantes = doc.productosEmbebidos().filterNot { it.id == productoId }
        compras.document(compraId)
            .update("productos", restantes.map { it.toMap() })
            .await()
    }

    suspend fun getCompraById(compraId: String): Compra? =
        compras.document(compraId).get().await().toCompra()

    // ── Comparativa de precios ────────────────────────────────────────────────

    // Precios de cada producto comprado por el usuario durante [anioMes] ("yyyy-MM")
    suspend fun getProductosDelMes(usuarioId: String, anioMes: String): List<PrecioProducto> {
        val snapshot = compras.whereEqualTo("usuarioId", usuarioId).get().await()
        return snapshot.documents
            .mapNotNull { it.toCompra() }
            .filter { it.fecha.format(FMT_ANIO_MES) == anioMes }
            .flatMap { compra ->
                compra.productos.map { producto ->
                    PrecioProducto(
                        nombre       = producto.nombre,
                        supermercado = compra.supermercado,
                        precio       = producto.precio,
                        fecha        = compra.fecha
                    )
                }
            }
    }

    // Meses ("yyyy-MM") con compras del usuario, más reciente primero
    suspend fun getMesesConCompras(usuarioId: String): List<String> {
        val snapshot = compras.whereEqualTo("usuarioId", usuarioId).get().await()
        return snapshot.documents
            .mapNotNull { it.toCompra() }
            .map { it.fecha.format(FMT_ANIO_MES) }
            .distinct()
            .sortedDescending()
    }
}

// ── Mappers ───────────────────────────────────────────────────────────────────

private val FMT_HORA_LENIENTE = DateTimeFormatter.ofPattern("H:mm")
private val FMT_ANIO_MES      = DateTimeFormatter.ofPattern("yyyy-MM")

// Normaliza cualquier variante "yy-M-d" / "yyyy-M-d" → "yyyy-MM-dd" y parsea.
private fun parseFecha(raw: String): LocalDate {
    val partes = raw.trim().split("-")
    if (partes.size == 3) {
        val anio = partes[0].let { if (it.length == 2) "20$it" else it }
        val mes  = partes[1].padStart(2, '0')
        val dia  = partes[2].padStart(2, '0')
        return LocalDate.parse("$anio-$mes-$dia")
    }
    return LocalDate.parse(raw)   // último recurso: dejar que falle con mensaje claro
}

private fun DocumentSnapshot.toCompra(): Compra? {
    if (!exists()) return null
    return try {
        Compra(
            id             = id,
            fecha          = parseFecha(getString("fecha") ?: return null),
            hora           = (getString("hora") ?: "00:00").let { h ->
                                 try { LocalTime.parse(h) }
                                 catch (_: Exception) { LocalTime.parse(h, FMT_HORA_LENIENTE) }
                             },
            supermercado   = getString("supermercado") ?: "",
            total          = getDouble("total") ?: 0.0,
            productos      = productosEmbebidos(),
            ticketImageUri = getString("ticketImageUri")
        )
    } catch (_: Exception) {
        null   // documento corrupto: lo salteamos en vez de romper toda la lista
    }
}

@Suppress("UNCHECKED_CAST")
private fun DocumentSnapshot.productosEmbebidos(): List<Producto> {
    val crudos = get("productos") as? List<Map<String, Any?>> ?: return emptyList()
    return crudos.map { p ->
        Producto(
            id          = p["id"] as? String ?: UUID.randomUUID().toString(),
            codigo      = p["codigo"] as? String ?: "",
            nombre      = p["nombre"] as? String ?: "",
            descripcion = p["descripcion"] as? String ?: "",
            cantidad    = (p["cantidad"] as? Number)?.toInt() ?: 1,
            precio      = (p["precio"] as? Number)?.toDouble() ?: 0.0
        )
    }
}

private fun Producto.toMap(): Map<String, Any?> = mapOf(
    "id"          to id.ifBlank { UUID.randomUUID().toString() },
    "codigo"      to codigo,
    "nombre"      to nombre,
    "descripcion" to descripcion,
    "cantidad"    to cantidad,
    "precio"      to precio
)
