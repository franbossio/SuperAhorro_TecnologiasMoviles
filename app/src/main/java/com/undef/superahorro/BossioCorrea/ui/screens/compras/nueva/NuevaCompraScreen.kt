package com.undef.superahorro.BossioCorrea.ui.screens.compras.nueva

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.undef.superahorro.BossioCorrea.R
import com.undef.superahorro.BossioCorrea.data.mock.supermercadosMock
import com.undef.superahorro.BossioCorrea.ui.theme.SuperAhorroTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaCompraScreen(
    onGuardarClick         : () -> Unit = {},
    onAgregarProductoClick : () -> Unit = {},
    onBackClick            : () -> Unit = {}
) {
    var fecha                 by remember { mutableStateOf("") }
    var hora                  by remember { mutableStateOf("") }
    var supermercado          by remember { mutableStateOf("") }
    var total                 by remember { mutableStateOf("") }
    var dropdownExpandido     by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.compra_nueva_titulo)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.volver))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor         = MaterialTheme.colorScheme.primary,
                    titleContentColor      = MaterialTheme.colorScheme.onPrimary,
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

            // ─── Sección datos ────────────────────────────────────────────
            Text(
                text       = "Datos de la compra",
                style      = MaterialTheme.typography.titleMedium,
                color      = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value         = fecha,
                    onValueChange = { fecha = it },
                    label         = { Text(stringResource(R.string.compra_fecha)) },
                    placeholder   = { Text("dd/mm/aaaa") },
                    modifier      = Modifier.weight(1f),
                    singleLine    = true,
                    shape         = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value         = hora,
                    onValueChange = { hora = it },
                    label         = { Text(stringResource(R.string.compra_hora)) },
                    placeholder   = { Text("hh:mm") },
                    modifier      = Modifier.weight(1f),
                    singleLine    = true,
                    shape         = RoundedCornerShape(12.dp)
                )
            }

            // ─── Dropdown supermercado ────────────────────────────────────
            ExposedDropdownMenuBox(
                expanded          = dropdownExpandido,
                onExpandedChange  = { dropdownExpandido = !dropdownExpandido }
            ) {
                OutlinedTextField(
                    value         = supermercado,
                    onValueChange = {},
                    readOnly      = true,
                    label         = { Text(stringResource(R.string.compra_supermercado)) },
                    trailingIcon  = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpandido)
                    },
                    modifier      = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape         = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded          = dropdownExpandido,
                    onDismissRequest  = { dropdownExpandido = false }
                ) {
                    supermercadosMock.forEach { s ->
                        DropdownMenuItem(
                            text    = { Text(s) },
                            onClick = { supermercado = s; dropdownExpandido = false }
                        )
                    }
                }
            }

            OutlinedTextField(
                value         = total,
                onValueChange = { total = it },
                label         = { Text(stringResource(R.string.compra_total)) },
                modifier      = Modifier.fillMaxWidth(),
                singleLine    = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon   = { Text("$") },
                shape         = RoundedCornerShape(12.dp)
            )

            // ─── Ticket ───────────────────────────────────────────────────
            OutlinedButton(
                onClick  = {},
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Default.CameraAlt,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.compra_adjuntar_ticket))
            }

            Divider()

            // ─── Productos ────────────────────────────────────────────────
            Text(
                text       = "Productos",
                style      = MaterialTheme.typography.titleMedium,
                color      = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(12.dp),
                colors    = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor   = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.producto_sin_productos))
                }
            }

            OutlinedButton(
                onClick  = onAgregarProductoClick,
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(12.dp)
            ) {
                Text("➕  ${stringResource(R.string.compra_agregar_producto)}")
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick  = onGuardarClick,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape    = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.compra_guardar), fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun NuevaCompraPreview() {
    SuperAhorroTheme { NuevaCompraScreen() }
}