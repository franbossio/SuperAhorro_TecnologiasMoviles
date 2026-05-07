package com.undef.superahorro.BossioCorrea.ui.screens.compras.nueva

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    var dropdownExpanded  by remember { mutableStateOf(false) }

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

                    // Supermercado dropdown
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        LabelCaps("SUPERMERCADO")
                        ExposedDropdownMenuBox(expanded = dropdownExpanded,
                            onExpandedChange = { dropdownExpanded = !dropdownExpanded }) {
                            OutlinedTextField(
                                value         = supermercado,
                                onValueChange = {},
                                readOnly      = true,
                                placeholder   = { Text("Buscar establecimiento...",
                                    color = MaterialTheme.colorScheme.outline) },
                                trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(dropdownExpanded) },
                                modifier      = Modifier.fillMaxWidth().menuAnchor(),
                                shape         = RoundedCornerShape(12.dp),
                                colors        = fieldColors
                            )
                            ExposedDropdownMenu(expanded = dropdownExpanded,
                                onDismissRequest = { dropdownExpanded = false }) {
                                supermercadosMock.forEach { s ->
                                    DropdownMenuItem(
                                        text    = { Text(s) },
                                        onClick = { supermercado = s; dropdownExpanded = false }
                                    )
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