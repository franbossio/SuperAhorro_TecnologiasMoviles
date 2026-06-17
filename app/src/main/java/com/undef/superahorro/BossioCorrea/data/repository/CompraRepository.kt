package com.undef.superahorro.BossioCorrea.data.repository

import android.content.Context
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.undef.superahorro.BossioCorrea.data.local.AppDatabase
import com.undef.superahorro.BossioCorrea.data.local.CompraConProductos
import com.undef.superahorro.BossioCorrea.data.local.CompraDao
import com.undef.superahorro.BossioCorrea.data.local.CompraEntity
import com.undef.superahorro.BossioCorrea.data.local.ProductoEntity
import com.undef.superahorro.BossioCorrea.domain.model.Compra
import com.undef.superahorro.BossioCorrea.domain.model.PrecioProducto
import com.undef.superahorro.BossioCorrea.domain.model.Producto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class CompraRepository(private val compraDao: CompraDao) {

    private val firestoreCompras = FirebaseFirestore.getInstance().collection("compras")

    companion object {
        fun create(context: Context) = CompraRepository(
            AppDatabase.getInstance(context).compraDao()
        )
    }

    // ── UI observa Room (offline-first) ──────────────────────────────────────
    fun getComprasFlow(usuarioId: String): Flow<List<Compra>> =
        compraDao.observarCompras(usuarioId)
            .map { lista -> lista.map { it.toDomain() } }

    // ── Sincroniza Firestore → Room ───────────────────────────────────────────
    // Llamar una vez al iniciar la app con sesión activa (HomeViewModel.cargar)
    suspend fun sincronizarDesdeFirestore(usuarioId: String) {
        try {
            val snapshot = firestoreCompras
                .whereEqualTo("usuarioId", usuarioId)
                .get()
                .await()
            val compras = snapshot.documents.mapNotNull { it.toCompra() }
            // Limpiar solo si la consulta fue exitosa para no perder datos offline
            compraDao.limpiarComprasDeUsuario(usuarioId)
            compras.forEach { compra ->
                compraDao.insertarCompra(compra.toEntity(usuarioId))
                compraDao.insertarProductos(compra.productos.map { it.toEntity(compra.id) })
            }
        } catch (_: Exception) { /* sin internet: la UI muestra los datos locales de Room */ }
    }

    // ── Guardar compra ────────────────────────────────────────────────────────
    suspend fun guardarCompra(
        usuarioId    : String,
        fecha        : String,
        hora         : String,
        supermercado : String,
        total        : Double,
        productos    : List<Producto>,
        ticketUri    : String? = null
    ): String {
        val id = UUID.randomUUID().toString()
        // 1. Room primero — disponible offline de inmediato
        compraDao.insertarCompra(CompraEntity(id, usuarioId, fecha, hora, supermercado, total, ticketUri))
        compraDao.insertarProductos(productos.map { it.toEntity(id) })
        // 2. Firestore — best-effort, falla en silencio sin internet
        try {
            firestoreCompras.document(id).set(
                mapOf(
                    "usuarioId"      to usuarioId,
                    "fecha"          to fecha,
                    "hora"           to hora,
                    "supermercado"   to supermercado,
                    "total"          to total,
                    "ticketImageUri" to ticketUri,
                    "productos"      to productos.map { it.toFirestoreMap() }
                )
            ).await()
        } catch (_: Exception) { }
        return id
    }

    suspend fun agregarProducto(compraId: String, producto: Producto) {
        val conId = if (producto.id.isBlank())
            producto.copy(id = UUID.randomUUID().toString()) else producto
        compraDao.insertarProductos(listOf(conId.toEntity(compraId)))
        try {
            val doc = firestoreCompras.document(compraId).get().await()
            val actuales = doc.productosEmbebidos()
            firestoreCompras.document(compraId)
                .update("productos", (actuales + conId).map { it.toFirestoreMap() })
                .await()
        } catch (_: Exception) { }
    }

    suspend fun eliminarCompra(compraId: String) {
        // CASCADE en Room elimina los productos automáticamente
        compraDao.eliminarCompraPorId(compraId)
        try {
            firestoreCompras.document(compraId).delete().await()
        } catch (_: Exception) { }
    }

    suspend fun eliminarProducto(compraId: String, productoId: String) {
        compraDao.eliminarProducto(compraId, productoId)
        try {
            val doc = firestoreCompras.document(compraId).get().await()
            val restantes = doc.productosEmbebidos().filterNot { it.id == productoId }
            firestoreCompras.document(compraId)
                .update("productos", restantes.map { it.toFirestoreMap() })
                .await()
        } catch (_: Exception) { }
    }

    suspend fun getCompraById(compraId: String): Compra? =
        compraDao.getCompraConProductos(compraId)?.toDomain()

    // ── Comparativa de precios ────────────────────────────────────────────────

    suspend fun getProductosDelMes(usuarioId: String, anioMes: String): List<PrecioProducto> {
        return compraDao.getComprasSnapshot(usuarioId)
            .map { it.toDomain() }
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

    suspend fun getMesesConCompras(usuarioId: String): List<String> {
        return compraDao.getComprasSnapshot(usuarioId)
            .map { it.toDomain() }
            .map { it.fecha.format(FMT_ANIO_MES) }
            .distinct()
            .sortedDescending()
    }
}

// ── Formato compartido ────────────────────────────────────────────────────────

private val FMT_HORA_LENIENTE = DateTimeFormatter.ofPattern("H:mm")
private val FMT_ANIO_MES      = DateTimeFormatter.ofPattern("yyyy-MM")

// Normaliza "yy-M-d" / "yyyy-M-d" → LocalDate
private fun parseFecha(raw: String): LocalDate {
    val partes = raw.trim().split("-")
    if (partes.size == 3) {
        val anio = partes[0].let { if (it.length == 2) "20$it" else it }
        val mes  = partes[1].padStart(2, '0')
        val dia  = partes[2].padStart(2, '0')
        return LocalDate.parse("$anio-$mes-$dia")
    }
    return LocalDate.parse(raw)
}

// ── Room mappers ──────────────────────────────────────────────────────────────

private fun CompraConProductos.toDomain(): Compra = Compra(
    id             = compra.id,
    fecha          = parseFecha(compra.fecha),
    hora           = try { LocalTime.parse(compra.hora) }
                     catch (_: Exception) { LocalTime.parse(compra.hora, FMT_HORA_LENIENTE) },
    supermercado   = compra.supermercado,
    total          = compra.total,
    productos      = productos.map { it.toDomain() },
    ticketImageUri = compra.ticketImageUri
)

private fun ProductoEntity.toDomain(): Producto = Producto(
    id          = id,
    codigo      = codigo,
    nombre      = nombre,
    descripcion = descripcion,
    cantidad    = cantidad,
    precio      = precio
)

private fun Compra.toEntity(usuarioId: String): CompraEntity = CompraEntity(
    id             = id,
    usuarioId      = usuarioId,
    fecha          = fecha.toString(),
    hora           = hora.toString(),
    supermercado   = supermercado,
    total          = total,
    ticketImageUri = ticketImageUri
)

private fun Producto.toEntity(compraId: String): ProductoEntity = ProductoEntity(
    id          = id.ifBlank { UUID.randomUUID().toString() },
    compraId    = compraId,
    codigo      = codigo,
    nombre      = nombre,
    descripcion = descripcion,
    cantidad    = cantidad,
    precio      = precio
)

// ── Firestore mappers ─────────────────────────────────────────────────────────

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
    } catch (_: Exception) { null }
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

private fun Producto.toFirestoreMap(): Map<String, Any?> = mapOf(
    "id"          to id.ifBlank { UUID.randomUUID().toString() },
    "codigo"      to codigo,
    "nombre"      to nombre,
    "descripcion" to descripcion,
    "cantidad"    to cantidad,
    "precio"      to precio
)
