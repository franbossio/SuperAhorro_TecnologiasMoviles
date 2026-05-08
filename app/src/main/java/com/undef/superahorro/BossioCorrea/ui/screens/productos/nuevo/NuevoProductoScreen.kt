package com.undef.superahorro.BossioCorrea.ui.screens.productos.nuevo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
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
import com.undef.superahorro.BossioCorrea.ui.components.LabelCaps
import com.undef.superahorro.BossioCorrea.ui.components.StitchTopBar
import com.undef.superahorro.BossioCorrea.ui.theme.SuperAhorroTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoProductoScreen(
    onGuardarClick : () -> Unit = {},
    onBackClick    : () -> Unit = {}
) {
    var codigo      by remember { mutableStateOf("") }
    var nombre      by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var cantidad    by remember { mutableStateOf("1") }
    var precio      by remember { mutableStateOf("") }

    val subtotal = (cantidad.toDoubleOrNull() ?: 0.0) * (precio.toDoubleOrNull() ?: 0.0)

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor    = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor  = MaterialTheme.colorScheme.outlineVariant,
        focusedContainerColor   = MaterialTheme.colorScheme.surfaceContainerLow,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
    )

    Scaffold(
        topBar = { StitchTopBar(stringResource(R.string.producto_nuevo_titulo), onBackClick) },
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

            Text("Agregar Productos", fontSize = 24.sp, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface, letterSpacing = (-0.24).sp)
            Text("Ingresá los detalles de tu compra.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline)

            Spacer(Modifier.height(20.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(16.dp),
                color    = MaterialTheme.colorScheme.surfaceContainerLow,
                shadowElevation = 1.dp,
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFBBCABF).copy(alpha = 0.3f))
                )
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

                    // Nombre
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        LabelCaps("NOMBRE DEL PRODUCTO")
                        OutlinedTextField(value = nombre, onValueChange = { nombre = it },
                            placeholder = { Text("Ej. Leche Entera", color = MaterialTheme.colorScheme.outline) },
                            modifier = Modifier.fillMaxWidth(), singleLine = true,
                            shape = RoundedCornerShape(12.dp), colors = fieldColors)
                    }

                    // Descripción
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        LabelCaps("DESCRIPCIÓN")
                        OutlinedTextField(value = descripcion, onValueChange = { descripcion = it },
                            modifier = Modifier.fillMaxWidth(), minLines = 2, maxLines = 3,
                            shape = RoundedCornerShape(12.dp), colors = fieldColors)
                    }

                    // Código de barras
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        LabelCaps("CÓDIGO DE BARRAS")
                        OutlinedTextField(
                            value         = codigo,
                            onValueChange = { codigo = it },
                            placeholder   = { Text("Ej. 7790040012345", color = MaterialTheme.colorScheme.outline) },
                            modifier      = Modifier.fillMaxWidth(), singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            trailingIcon  = {
                                IconButton(onClick = {}) {
                                    Icon(Icons.Default.QrCodeScanner, null,
                                        tint = MaterialTheme.colorScheme.primary)
                                }
                            },
                            shape = RoundedCornerShape(12.dp), colors = fieldColors
                        )
                    }

                    // Cantidad / Precio
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            LabelCaps("CANTIDAD")
                            OutlinedTextField(value = cantidad, onValueChange = { cantidad = it },
                                modifier = Modifier.fillMaxWidth(), singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(12.dp), colors = fieldColors)
                        }
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            LabelCaps("PRECIO UNITARIO")
                            OutlinedTextField(value = precio, onValueChange = { precio = it },
                                modifier = Modifier.fillMaxWidth(), singleLine = true,
                                leadingIcon = { Text("$", color = MaterialTheme.colorScheme.outline) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                shape = RoundedCornerShape(12.dp), colors = fieldColors)
                        }
                    }

                    // Subtotal calculado
                    if (subtotal > 0) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape    = RoundedCornerShape(12.dp),
                            color    = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Subtotal", style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary)
                                Text("$ %,.2f".format(subtotal), style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick   = onGuardarClick,
                modifier  = Modifier.fillMaxWidth().height(56.dp),
                shape     = RoundedCornerShape(14.dp),
                colors    = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                elevation = ButtonDefaults.buttonElevation(4.dp),
                enabled   = nombre.isNotBlank() && precio.isNotBlank()
            ) {
                Text(stringResource(R.string.producto_guardar), fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun NuevoProductoPreview() { SuperAhorroTheme { NuevoProductoScreen() } }