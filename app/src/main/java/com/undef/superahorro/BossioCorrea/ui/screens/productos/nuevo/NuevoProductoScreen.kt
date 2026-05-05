package com.undef.superahorro.BossioCorrea.ui.screens.productos.nuevo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.undef.superahorro.BossioCorrea.R
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.producto_nuevo_titulo)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.volver))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor            = MaterialTheme.colorScheme.primary,
                    titleContentColor         = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text       = "Datos del producto",
                style      = MaterialTheme.typography.titleMedium,
                color      = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            // Código con botón de scanner
            OutlinedTextField(
                value         = codigo,
                onValueChange = { codigo = it },
                label         = { Text(stringResource(R.string.producto_codigo)) },
                modifier      = Modifier.fillMaxWidth(),
                singleLine    = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                trailingIcon  = {
                    IconButton(onClick = { /* TODO: Intent para scanner */ }) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "Escanear código", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value         = nombre,
                onValueChange = { nombre = it },
                label         = { Text(stringResource(R.string.producto_nombre)) },
                modifier      = Modifier.fillMaxWidth(),
                singleLine    = true,
                shape         = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value         = descripcion,
                onValueChange = { descripcion = it },
                label         = { Text(stringResource(R.string.producto_descripcion)) },
                modifier      = Modifier.fillMaxWidth(),
                minLines      = 2,
                maxLines      = 3,
                shape         = RoundedCornerShape(12.dp)
            )

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value         = cantidad,
                    onValueChange = { cantidad = it },
                    label         = { Text(stringResource(R.string.producto_cantidad)) },
                    modifier      = Modifier.weight(1f),
                    singleLine    = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape         = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value         = precio,
                    onValueChange = { precio = it },
                    label         = { Text(stringResource(R.string.producto_precio)) },
                    modifier      = Modifier.weight(1f),
                    singleLine    = true,
                    leadingIcon   = { Text("$") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape         = RoundedCornerShape(12.dp)
                )
            }

            // Subtotal calculado
            val subtotalCalc = (cantidad.toDoubleOrNull() ?: 0.0) * (precio.toDoubleOrNull() ?: 0.0)
            if (subtotalCalc > 0) {
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(12.dp),
                    colors    = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor   = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Row(
                        modifier              = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text       = "$ %,.2f".format(subtotalCalc),
                            style      = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick  = onGuardarClick,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape    = RoundedCornerShape(12.dp),
                enabled  = nombre.isNotBlank() && precio.isNotBlank()
            ) {
                Text(stringResource(R.string.producto_guardar), fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun NuevoProductoPreview() {
    SuperAhorroTheme { NuevoProductoScreen() }
}