package com.undef.superahorro.BossioCorrea.ui.screens.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.undef.superahorro.BossioCorrea.R
import com.undef.superahorro.BossioCorrea.data.mock.comprasMock
import com.undef.superahorro.BossioCorrea.data.mock.usuarioMock
import com.undef.superahorro.BossioCorrea.ui.theme.SuperAhorroTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    onGuardarClick      : () -> Unit = {},
    onCerrarSesionClick : () -> Unit = {},
    onBackClick         : () -> Unit = {}
) {
    var nombre  by remember { mutableStateOf(usuarioMock.nombre) }
    var apellido by remember { mutableStateOf(usuarioMock.apellido) }
    var email   by remember { mutableStateOf(usuarioMock.email) }
    var telefono by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    val totalGastado   = comprasMock.sumOf { it.total }
    val cantidadCompras = comprasMock.size
    val superFavorito  = comprasMock
        .groupBy { it.supermercado }
        .maxByOrNull { it.value.size }?.key ?: "-"

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title   = { Text("Cerrar sesión") },
            text    = { Text("¿Estás seguro que querés cerrar sesión?") },
            confirmButton = {
                TextButton(onClick = { showDialog = false; onCerrarSesionClick() }) {
                    Text("Sí, salir", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(R.string.cancelar))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.perfil_titulo)) },
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Header verde con avatar ───────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Avatar con iniciales
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text       = "${nombre.firstOrNull() ?: ""}${apellido.firstOrNull() ?: ""}",
                            fontSize   = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color      = MaterialTheme.colorScheme.onPrimary,
                            textAlign  = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text       = "$nombre $apellido",
                        style      = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text  = email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    // Badge "Super Ahorrador"
                    Card(
                        shape  = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor   = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    ) {
                        Row(
                            modifier  = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(14.dp))
                            Text("Super Ahorrador", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Column(
                modifier            = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // ── Mis logros ────────────────────────────────────────────
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(16.dp),
                    colors    = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor   = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text       = "Mis Logros de Ahorro",
                            style      = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            LogroItem(valor = "$ %,.0f".format(totalGastado), label = "Total gastado")
                            VerticalDivider(modifier = Modifier.height(40.dp))
                            LogroItem(valor = "$cantidadCompras", label = "Compras")
                            VerticalDivider(modifier = Modifier.height(40.dp))
                            LogroItem(valor = superFavorito, label = "Favorito", small = true)
                        }
                    }
                }

                // ── Datos personales ──────────────────────────────────────
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(16.dp),
                    colors    = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor   = MaterialTheme.colorScheme.onSurface
                    ),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(
                        modifier            = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text       = "Datos Personales",
                            style      = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color      = MaterialTheme.colorScheme.primary
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value         = nombre,
                                onValueChange = { nombre = it },
                                label         = { Text("Nombre") },
                                leadingIcon   = { Icon(Icons.Default.Person, contentDescription = null) },
                                modifier      = Modifier.weight(1f),
                                singleLine    = true,
                                shape         = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value         = apellido,
                                onValueChange = { apellido = it },
                                label         = { Text("Apellido") },
                                modifier      = Modifier.weight(1f),
                                singleLine    = true,
                                shape         = RoundedCornerShape(12.dp)
                            )
                        }

                        OutlinedTextField(
                            value         = email,
                            onValueChange = { email = it },
                            label         = { Text(stringResource(R.string.register_email)) },
                            leadingIcon   = { Icon(Icons.Default.Email, contentDescription = null) },
                            modifier      = Modifier.fillMaxWidth(),
                            singleLine    = true,
                            shape         = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value         = telefono,
                            onValueChange = { telefono = it },
                            label         = { Text("Teléfono (opcional)") },
                            leadingIcon   = { Icon(Icons.Default.Phone, contentDescription = null) },
                            modifier      = Modifier.fillMaxWidth(),
                            singleLine    = true,
                            shape         = RoundedCornerShape(12.dp)
                        )
                    }
                }

                // ── Supermercados favoritos ───────────────────────────────
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(16.dp),
                    colors    = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor   = MaterialTheme.colorScheme.onSurface
                    ),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(
                        modifier            = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Text(
                                text       = "Supermercados Favoritos",
                                style      = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color      = MaterialTheme.colorScheme.primary
                            )
                            TextButton(onClick = {}) { Text("+ Agregar") }
                        }
                        comprasMock
                            .groupBy { it.supermercado }
                            .entries.take(3)
                            .forEach { (super_, compras) ->
                                Row(
                                    modifier              = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment     = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment     = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.ShoppingCart,
                                            contentDescription = null,
                                            tint     = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Column {
                                            Text(super_, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                            Text(
                                                "${compras.size} compra(s)",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    IconButton(onClick = {}) {
                                        Icon(
                                            Icons.Default.Star,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.tertiary
                                        )
                                    }
                                }
                                if (compras != comprasMock.groupBy { it.supermercado }.entries.take(3).last().value) {
                                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                                }
                            }
                    }
                }

                // ── Botones ───────────────────────────────────────────────
                Button(
                    onClick  = onGuardarClick,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape    = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.perfil_guardar), fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick  = { showDialog = true },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape    = RoundedCornerShape(12.dp),
                    colors   = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    border   = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.error)
                    )
                ) {
                    Text(stringResource(R.string.perfil_cerrar_sesion), fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun LogroItem(valor: String, label: String, small: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text       = valor,
            style      = if (small) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text  = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PerfilPreview() {
    SuperAhorroTheme { PerfilScreen() }
}
