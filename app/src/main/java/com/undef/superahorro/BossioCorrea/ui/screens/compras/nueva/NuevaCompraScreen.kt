package com.undef.superahorro.BossioCorrea.ui.screens.compras.nueva

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.undef.superahorro.BossioCorrea.R
import com.undef.superahorro.BossioCorrea.data.mock.supermercadosMock
import com.undef.superahorro.BossioCorrea.ui.components.LabelCaps
import com.undef.superahorro.BossioCorrea.ui.components.StitchTopBar
import com.undef.superahorro.BossioCorrea.ui.theme.SuperAhorroTheme
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaCompraScreen(
    onGuardarClick         : () -> Unit = {},
    onAgregarProductoClick : () -> Unit = {},
    onBackClick            : () -> Unit = {}
) {
    var fecha             by remember { mutableStateOf("") }
    var hora              by remember { mutableStateOf("") }
    var supermercado      by remember { mutableStateOf("") }
    var total             by remember { mutableStateOf("") }

    // ── Estado autocomplete supermercado ──────────────────────────────────────
    var supermercados     by remember { mutableStateOf(supermercadosMock.toMutableList()) }
    var superQuery        by remember { mutableStateOf("") }
    var dropdownOpen      by remember { mutableStateOf(false) }
    var showAgregarDialog by remember { mutableStateOf(false) }
    var nuevoSuperNombre  by remember { mutableStateOf("") }

    val supersFiltrados = remember(superQuery, supermercados) {
        if (superQuery.isBlank()) supermercados
        else supermercados.filter { it.contains(superQuery.trim(), ignoreCase = true) }
    }

    // ── Estado del DatePicker ─────────────────────────────────────────────────
    var mostrarCalendario by remember { mutableStateOf(false) }
    val datePickerState   = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    // Cuando el diálogo se confirma, formateamos la fecha seleccionada
    if (mostrarCalendario) {
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            fecha = sdf.format(Date(millis))
                        }
                        mostrarCalendario = false
                    }
                ) {
                    Text(
                        stringResource(R.string.confirmar_fecha),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCalendario = false }) {
                    Text(
                        stringResource(R.string.cancelar),
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = Color(0xFFFFFFFF)
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            DatePicker(
                state  = datePickerState,
                colors = DatePickerDefaults.colors(
                    todayContentColor          = MaterialTheme.colorScheme.primary,
                    todayDateBorderColor       = MaterialTheme.colorScheme.primary,
                    selectedDayContainerColor  = MaterialTheme.colorScheme.primary,
                    selectedDayContentColor    = Color.White,
                    selectedYearContainerColor = MaterialTheme.colorScheme.primary,
                    selectedYearContentColor   = Color.White
                )
            )
        }
    }
    // ─────────────────────────────────────────────────────────────────────────

    // ── Dialog: agregar nuevo supermercado ────────────────────────────────────
    if (showAgregarDialog) {
        Dialog(onDismissRequest = { showAgregarDialog = false; nuevoSuperNombre = "" }) {
            Surface(
                shape           = RoundedCornerShape(20.dp),
                color           = Color(0xFFFFFFFF),
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier            = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Agregar supermercado",
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "\"${superQuery.trim()}\" no está en la lista. Confirmá el nombre para agregarlo.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    OutlinedTextField(
                        value         = nuevoSuperNombre,
                        onValueChange = { nuevoSuperNombre = it },
                        label         = { Text("Nombre del supermercado") },
                        singleLine    = true,
                        modifier      = Modifier.fillMaxWidth(),
                        shape         = RoundedCornerShape(12.dp),
                        colors        = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor      = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor    = MaterialTheme.colorScheme.outlineVariant,
                            focusedContainerColor   = Color(0xFFFFFFFF),
                            unfocusedContainerColor = Color(0xFFF2F4F6)
                        ),
                        leadingIcon = {
                            Icon(Icons.Default.Store, null, tint = MaterialTheme.colorScheme.primary)
                        }
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(
                            onClick  = { showAgregarDialog = false; nuevoSuperNombre = "" },
                            modifier = Modifier.weight(1f),
                            shape    = RoundedCornerShape(12.dp)
                        ) { Text("Cancelar") }
                        Button(
                            onClick  = {
                                val nuevo = nuevoSuperNombre.trim()
                                if (nuevo.isNotBlank()) {
                                    supermercados = (supermercados + nuevo).toMutableList()
                                    supermercado  = nuevo
                                    superQuery    = nuevo
                                    dropdownOpen  = false
                                }
                                showAgregarDialog = false
                                nuevoSuperNombre  = ""
                            },
                            modifier = Modifier.weight(1f),
                            shape    = RoundedCornerShape(12.dp),
                            enabled  = nuevoSuperNombre.isNotBlank(),
                            colors   = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Agregar", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
    // ─────────────────────────────────────────────────────────────────────────

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor    = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor  = MaterialTheme.colorScheme.outlineVariant,
        focusedContainerColor = Color(0xFFFFFFFF),
        unfocusedContainerColor = Color(0xFFF2F4F6)
    )

    Scaffold(
        topBar = { StitchTopBar(stringResource(R.string.compra_nueva_titulo), onBackClick) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            // ── Section header ────────────────────────────────────────────
            Text("Detalles del ticket", fontSize = 24.sp, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface, letterSpacing = (-0.24).sp)
            Text("Ingresá la información básica de tu compra.",
                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)

            Spacer(Modifier.height(20.dp))

            // ── Form card ─────────────────────────────────────────────────
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(16.dp),
                color    = Color(0xFFFFFFFF),
                shadowElevation = 1.dp,
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFBBCABF).copy(alpha = 0.3f))
                )
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

                    // Supermercado — autocomplete con búsqueda y agregar nuevo
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        LabelCaps("SUPERMERCADO")

                        // Campo de búsqueda editable
                        OutlinedTextField(
                            value         = superQuery,
                            onValueChange = {
                                superQuery   = it
                                supermercado = ""
                                dropdownOpen = it.isNotBlank()
                            },
                            placeholder   = { Text("Buscá o escribí un supermercado", color = MaterialTheme.colorScheme.outline) },
                            modifier      = Modifier.fillMaxWidth(),
                            singleLine    = true,
                            shape         = RoundedCornerShape(12.dp),
                            colors        = fieldColors,
                            leadingIcon   = {
                                Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.primary)
                            },
                            trailingIcon  = {
                                IconButton(onClick = { dropdownOpen = !dropdownOpen; if (dropdownOpen) superQuery = "" }) {
                                    ExposedDropdownMenuDefaults.TrailingIcon(dropdownOpen)
                                }
                            }
                        )

                        // Chip de confirmación cuando hay uno seleccionado
                        if (supermercado.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
                            ) {
                                Row(
                                    modifier              = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment     = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.Store, null,
                                        tint     = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(14.dp))
                                    Text(
                                        supermercado,
                                        style      = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color      = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        // Dropdown con resultados filtrados
                        if (dropdownOpen) {
                            Surface(
                                modifier        = Modifier.fillMaxWidth().heightIn(max = 220.dp),
                                shape           = RoundedCornerShape(12.dp),
                                color           = Color(0xFFFFFFFF),
                                shadowElevation = 4.dp,
                                border          = CardDefaults.outlinedCardBorder().copy(
                                    brush = SolidColor(Color(0xFFBBCABF).copy(alpha = 0.4f))
                                )
                            ) {
                                LazyColumn {
                                    items(supersFiltrados) { s ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    supermercado = s
                                                    superQuery   = s
                                                    dropdownOpen = false
                                                }
                                                .padding(horizontal = 16.dp, vertical = 12.dp),
                                            verticalAlignment     = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Icon(Icons.Default.Store, null,
                                                tint     = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp))
                                            Text(s, style = MaterialTheme.typography.bodyMedium)
                                        }
                                        if (s != supersFiltrados.last())
                                            HorizontalDivider(color = Color(0xFFBBCABF).copy(alpha = 0.2f))
                                    }

                                    // Sin resultados → opción de agregar
                                    if (supersFiltrados.isEmpty() && superQuery.isNotBlank()) {
                                        item {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        nuevoSuperNombre  = superQuery.trim()
                                                        dropdownOpen      = false
                                                        showAgregarDialog = true
                                                    }
                                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                                verticalAlignment     = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                Icon(Icons.Default.Add, null,
                                                    tint     = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(18.dp))
                                                Text(
                                                    "Agregar \"${superQuery.trim()}\"",
                                                    style      = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color      = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Fecha / Hora
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                        // ── FECHA con ícono de calendario ─────────────────
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            LabelCaps("FECHA")
                            OutlinedTextField(
                                value         = fecha,
                                onValueChange = {},           // solo lectura; se edita via picker
                                readOnly      = true,
                                placeholder   = { Text("dd/mm/aaaa", color = MaterialTheme.colorScheme.outline) },
                                trailingIcon  = {
                                    IconButton(onClick = { mostrarCalendario = true }) {
                                        Icon(
                                            imageVector        = Icons.Default.DateRange,
                                            contentDescription = stringResource(R.string.abrir_calendario),
                                            tint               = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape  = RoundedCornerShape(12.dp),
                                colors = fieldColors
                            )
                        }

                        // ── HORA (texto libre) ────────────────────────────
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            LabelCaps("HORA")
                            OutlinedTextField(value = hora, onValueChange = { hora = it },
                                placeholder = { Text("hh:mm", color = MaterialTheme.colorScheme.outline) },
                                modifier = Modifier.fillMaxWidth(), singleLine = true,
                                shape = RoundedCornerShape(12.dp), colors = fieldColors)
                        }
                    }

                    // Total
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        LabelCaps("TOTAL")
                        OutlinedTextField(value = total, onValueChange = { total = it },
                            placeholder = { Text("0.00", color = MaterialTheme.colorScheme.outline) },
                            modifier = Modifier.fillMaxWidth(), singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            leadingIcon = { Text("$", color = MaterialTheme.colorScheme.outline) },
                            shape = RoundedCornerShape(12.dp), colors = fieldColors)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Ticket ────────────────────────────────────────────────────
            Text("Ticket de compra", fontSize = 18.sp, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick  = {},
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                border   = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(
                        MaterialTheme.colorScheme.outlineVariant))
            ) {
                Icon(Icons.Default.CameraAlt, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.compra_adjuntar_ticket), fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(24.dp))

            // ── Productos ─────────────────────────────────────────────────
            Text("Productos", fontSize = 18.sp, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(8.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(12.dp),
                color    = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Box(Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.producto_sin_productos),
                        color = MaterialTheme.colorScheme.outline)
                }
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick  = onAgregarProductoClick,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("➕  ${stringResource(R.string.compra_agregar_producto)}", fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick   = onGuardarClick,
                modifier  = Modifier.fillMaxWidth().height(56.dp),
                shape     = RoundedCornerShape(14.dp),
                colors    = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                elevation = ButtonDefaults.buttonElevation(4.dp)
            ) {
                Text(stringResource(R.string.compra_guardar), fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun NuevaCompraPreview() { SuperAhorroTheme { NuevaCompraScreen() } }