package com.undef.superahorro.BossioCorrea.ui.screens.compras.nueva

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.undef.superahorro.BossioCorrea.R
import com.undef.superahorro.BossioCorrea.data.mock.supermercadosMock
import com.undef.superahorro.BossioCorrea.domain.model.Producto
import com.undef.superahorro.BossioCorrea.ui.components.LabelCaps
import com.undef.superahorro.BossioCorrea.ui.components.StitchTopBar
import com.undef.superahorro.BossioCorrea.ui.navigation.UiState
import com.undef.superahorro.BossioCorrea.ui.theme.SuperAhorroTheme
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaCompraScreen(
    vm          : NuevaCompraViewModel = viewModel(),
    onGuardarClick : () -> Unit = {},
    onBackClick    : () -> Unit = {}
) {
    val uiState   by vm.uiState.collectAsStateWithLifecycle()
    val productos by vm.productos.collectAsStateWithLifecycle()
    val ticketUri by vm.ticketUri.collectAsStateWithLifecycle()
    val analizando by vm.analizando.collectAsStateWithLifecycle()
    val errorIA    by vm.errorIA.collectAsStateWithLifecycle()

    val context = LocalContext.current

    // ── Campos del formulario (la IA los puede rellenar) ──────────────────────
    var fecha        by remember { mutableStateOf("") }
    var hora         by remember { mutableStateOf("") }
    var supermercado by remember { mutableStateOf("") }
    var total        by remember { mutableStateOf("") }
    var superQuery   by remember { mutableStateOf("") }

    // Autocomplete supermercado
    var supermercados     by remember { mutableStateOf(supermercadosMock.toMutableList()) }
    var dropdownOpen      by remember { mutableStateOf(false) }
    var showAgregarDialog by remember { mutableStateOf(false) }
    var nuevoSuperNombre  by remember { mutableStateOf("") }

    val supersFiltrados = remember(superQuery, supermercados) {
        if (superQuery.isBlank()) supermercados
        else supermercados.filter { it.contains(superQuery.trim(), ignoreCase = true) }
    }

    // ── BottomSheet producto ──────────────────────────────────────────────────
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet  by remember { mutableStateOf(false) }

    // ── DatePicker ────────────────────────────────────────────────────────────
    var mostrarCalendario by remember { mutableStateOf(false) }
    val datePickerState   = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())

    // ── Foto del ticket ───────────────────────────────────────────────────────
    var showFotoDialog  by remember { mutableStateOf(false) }
    var cameraImageUri  by remember { mutableStateOf<Uri?>(null) }

    // Launcher de cámara
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) cameraImageUri?.let { vm.setTicketUri(it) }
    }

    // Launcher de galería
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { vm.setTicketUri(it) }
    }

    // Permiso de cámara
    val permisoCamara = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            val file = File(context.cacheDir, "ticket_${System.currentTimeMillis()}.jpg")
            val uri  = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            cameraImageUri = uri
            cameraLauncher.launch(uri)
        }
    }

    // DatePicker dialog
    if (mostrarCalendario) {
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
                    }
                    mostrarCalendario = false
                }) { Text(stringResource(R.string.aceptar), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) }
            },
            dismissButton = { TextButton(onClick = { mostrarCalendario = false }) { Text(stringResource(R.string.cancelar), color = MaterialTheme.colorScheme.outline) } },
            shape = RoundedCornerShape(20.dp),
            colors = DatePickerDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            DatePicker(state = datePickerState, colors = DatePickerDefaults.colors(
                selectedDayContainerColor = MaterialTheme.colorScheme.primary,
                selectedDayContentColor   = Color.White,
                todayContentColor         = MaterialTheme.colorScheme.primary,
                todayDateBorderColor      = MaterialTheme.colorScheme.primary))
        }
    }

    // Dialog agregar supermercado
    if (showAgregarDialog) {
        Dialog(onDismissRequest = { showAgregarDialog = false; nuevoSuperNombre = "" }) {
            Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surfaceContainerLow, shadowElevation = 4.dp) {
                Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(stringResource(R.string.dialog_agregar_super_titulo), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    OutlinedTextField(value = nuevoSuperNombre, onValueChange = { nuevoSuperNombre = it },
                        label = { Text(stringResource(R.string.dialog_agregar_super_campo)) }, singleLine = true,
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(onClick = { showAgregarDialog = false; nuevoSuperNombre = "" }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) { Text(stringResource(R.string.cancelar)) }
                        Button(onClick = {
                            val nuevo = nuevoSuperNombre.trim()
                            if (nuevo.isNotBlank()) { supermercados = (supermercados + nuevo).toMutableList(); supermercado = nuevo; superQuery = nuevo; dropdownOpen = false }
                            showAgregarDialog = false; nuevoSuperNombre = ""
                        }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), enabled = nuevoSuperNombre.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) { Text(stringResource(R.string.nueva_compra_btn_agregar), fontWeight = FontWeight.SemiBold) }
                    }
                }
            }
        }
    }

    // Dialog elegir fuente de foto
    if (showFotoDialog) {
        Dialog(onDismissRequest = { showFotoDialog = false }) {
            Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surfaceContainerLow, shadowElevation = 4.dp) {
                Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Foto del ticket", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    // Cámara
                    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .clickable {
                            showFotoDialog = false
                            permisoCamara.launch(Manifest.permission.CAMERA)
                        }
                        .background(MaterialTheme.colorScheme.primary.copy(0.08f))
                        .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.CameraAlt, null, tint = MaterialTheme.colorScheme.primary)
                        Text("Tomar foto", fontWeight = FontWeight.Medium)
                    }
                    // Galería
                    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .clickable {
                            showFotoDialog = false
                            galleryLauncher.launch("image/*")
                        }
                        .background(MaterialTheme.colorScheme.primary.copy(0.08f))
                        .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.Photo, null, tint = MaterialTheme.colorScheme.primary)
                        Text("Elegir de galería", fontWeight = FontWeight.Medium)
                    }
                    TextButton(onClick = { showFotoDialog = false }, modifier = Modifier.align(Alignment.End)) {
                        Text("Cancelar", color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }
    }

    // BottomSheet agregar producto
    if (showSheet) {
        ModalBottomSheet(onDismissRequest = { showSheet = false }, sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)) {
            AgregarProductoSheet(
                onGuardar  = { producto -> vm.agregarProducto(producto); showSheet = false },
                onCancelar = { showSheet = false }
            )
        }
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor    = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor  = MaterialTheme.colorScheme.outlineVariant,
        focusedContainerColor   = MaterialTheme.colorScheme.surfaceContainerLow,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
    )

    Scaffold(
        topBar = { StitchTopBar(stringResource(R.string.compra_nueva_titulo), onBackClick) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp).verticalScroll(rememberScrollState())) {
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.nueva_compra_titulo_seccion), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, letterSpacing = (-0.24).sp)
            Text(stringResource(R.string.nueva_compra_subtitulo), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
            Spacer(Modifier.height(16.dp))

            // ── SECCIÓN TICKET ────────────────────────────────────────────────
            Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow, shadowElevation = 1.dp,
                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(MaterialTheme.colorScheme.outlineVariant.copy(0.3f)))) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    LabelCaps(stringResource(R.string.nueva_compra_ticket_label))

                    // Previsualización de la imagen
                    if (ticketUri != null) {
                        AsyncImage(
                            model             = ticketUri,
                            contentDescription = stringResource(R.string.nueva_compra_ticket_label),
                            modifier          = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(12.dp)),
                            contentScale      = ContentScale.Crop
                        )
                    }

                    // Botones de foto y escanear con IA
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Botón foto
                        OutlinedButton(
                            onClick  = { showFotoDialog = true },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape    = RoundedCornerShape(12.dp),
                            colors   = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.CameraAlt, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(if (ticketUri == null) stringResource(R.string.nueva_compra_btn_foto) else stringResource(R.string.nueva_compra_btn_cambiar_foto), fontWeight = FontWeight.Medium)
                        }

                        // Botón escanear con IA
                        Button(
                            onClick  = {
                                vm.analizarTicketConIA { superIA, fechaIA, horaIA, totalIA, _ ->
                                    if (superIA.isNotBlank()) { supermercado = superIA; superQuery = superIA }
                                    if (fechaIA.isNotBlank()) fecha = fechaIA
                                    if (horaIA.isNotBlank())  hora  = horaIA
                                    if (totalIA.isNotBlank()) total = totalIA
                                }
                            },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape    = RoundedCornerShape(12.dp),
                            enabled  = ticketUri != null && !analizando,
                            colors   = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            if (analizando) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                                Spacer(Modifier.width(6.dp))
                                Text(stringResource(R.string.nueva_compra_analizando), fontWeight = FontWeight.SemiBold)
                            } else {
                                Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(stringResource(R.string.nueva_compra_btn_escanear_ia), fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    // Error de IA
                    if (errorIA != null) {
                        Text(errorIA!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }

                    // Banner éxito IA
                    AnimatedVisibility(visible = analizando.not() && errorIA == null && ticketUri != null && productos.isNotEmpty()) {
                        Surface(shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.primary.copy(0.10f)) {
                            Row(modifier = Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.CheckCircle, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                Text(stringResource(R.string.nueva_compra_ticket_analizado), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // ── FORM CARD ─────────────────────────────────────────────────────
            Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow, shadowElevation = 1.dp,
                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(MaterialTheme.colorScheme.outlineVariant.copy(0.3f)))) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

                    // Supermercado
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        LabelCaps(stringResource(R.string.compra_supermercado))
                        OutlinedTextField(value = superQuery,
                            onValueChange = { superQuery = it; supermercado = ""; dropdownOpen = it.isNotBlank() },
                            placeholder = { Text(stringResource(R.string.nueva_compra_super_placeholder), color = MaterialTheme.colorScheme.outline) },
                            modifier = Modifier.fillMaxWidth(), singleLine = true,
                            shape = RoundedCornerShape(12.dp), colors = fieldColors,
                            leadingIcon = { Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.primary) },
                            trailingIcon = { IconButton(onClick = { dropdownOpen = !dropdownOpen }) { ExposedDropdownMenuDefaults.TrailingIcon(dropdownOpen) } })
                        if (supermercado.isNotBlank()) {
                            Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)) {
                                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.Store, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                                    Text(supermercado, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                        if (dropdownOpen) {
                            Surface(modifier = Modifier.fillMaxWidth().heightIn(max = 220.dp), shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerLow, shadowElevation = 4.dp) {
                                LazyColumn {
                                    items(supersFiltrados) { s ->
                                        Row(modifier = Modifier.fillMaxWidth().clickable { supermercado = s; superQuery = s; dropdownOpen = false }.padding(horizontal = 16.dp, vertical = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Icon(Icons.Default.Store, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                            Text(s, style = MaterialTheme.typography.bodyMedium)
                                        }
                                        if (s != supersFiltrados.last()) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(0.2f))
                                    }
                                    if (supersFiltrados.isEmpty() && superQuery.isNotBlank()) {
                                        item {
                                            Row(modifier = Modifier.fillMaxWidth()
                                                .clickable { nuevoSuperNombre = superQuery.trim(); dropdownOpen = false; showAgregarDialog = true }
                                                .padding(horizontal = 16.dp, vertical = 12.dp),
                                                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                                Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                                Text(stringResource(R.string.nueva_compra_super_agregar, superQuery.trim()), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Fecha / Hora
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            LabelCaps(stringResource(R.string.compra_fecha))
                            OutlinedTextField(value = fecha, onValueChange = {}, readOnly = true,
                                placeholder = { Text(stringResource(R.string.nueva_compra_fecha_placeholder), color = MaterialTheme.colorScheme.outline) },
                                trailingIcon = { IconButton(onClick = { mostrarCalendario = true }) { Icon(Icons.Default.DateRange, null, tint = MaterialTheme.colorScheme.primary) } },
                                modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors)
                        }
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            LabelCaps(stringResource(R.string.compra_hora))
                            OutlinedTextField(value = hora, onValueChange = { hora = it },
                                placeholder = { Text(stringResource(R.string.nueva_compra_hora_placeholder), color = MaterialTheme.colorScheme.outline) },
                                modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors)
                        }
                    }

                    // Total
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        LabelCaps(stringResource(R.string.compra_total_label))
                        OutlinedTextField(value = total, onValueChange = { total = it },
                            placeholder = { Text(stringResource(R.string.nueva_compra_total_placeholder), color = MaterialTheme.colorScheme.outline) },
                            modifier = Modifier.fillMaxWidth(), singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            leadingIcon = { Text("$", color = MaterialTheme.colorScheme.outline) },
                            shape = RoundedCornerShape(12.dp), colors = fieldColors)
                    }
                }
            }

            // Error guardar
            if (uiState is UiState.Error) {
                Spacer(Modifier.height(8.dp))
                Text((uiState as UiState.Error).mensaje, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(Modifier.height(20.dp))

            // ── PRODUCTOS ─────────────────────────────────────────────────────
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text(stringResource(R.string.nueva_compra_productos_header, productos.size), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                TextButton(onClick = { showSheet = true }) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.nueva_compra_btn_agregar), fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(Modifier.height(8.dp))

            if (productos.isEmpty()) {
                Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)) {
                    Box(Modifier.padding(16.dp)) { Text(stringResource(R.string.producto_sin_productos), color = MaterialTheme.colorScheme.outline) }
                }
            } else {
                productos.forEach { p ->
                    ProductoItemRow(producto = p, onEliminar = { vm.eliminarProducto(p.id) })
                    Spacer(Modifier.height(8.dp))
                }
            }

            Spacer(Modifier.height(20.dp))

            when (uiState) {
                is UiState.Loading -> Box(Modifier.fillMaxWidth(), Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
                else -> Button(
                    onClick = { vm.guardar(supermercado, fecha, hora, total, onGuardarClick) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    elevation = ButtonDefaults.buttonElevation(4.dp)
                ) { Text(stringResource(R.string.compra_guardar), fontWeight = FontWeight.SemiBold, fontSize = 16.sp) }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ── Fila producto ─────────────────────────────────────────────────────────────

@Composable
private fun ProductoItemRow(producto: Producto, onEliminar: () -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow, shadowElevation = 1.dp,
        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(MaterialTheme.colorScheme.outlineVariant.copy(0.25f)))) {
        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(producto.nombre, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Text("x${producto.cantidad}  ·  $ %,.2f c/u".format(producto.precio), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("$ %,.2f".format(producto.subtotal), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                IconButton(onClick = onEliminar, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Close, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

// ── BottomSheet agregar producto ──────────────────────────────────────────────

@Composable
private fun AgregarProductoSheet(onGuardar: (Producto) -> Unit, onCancelar: () -> Unit) {
    var nombre      by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var cantidad    by remember { mutableStateOf("1") }
    var precio      by remember { mutableStateOf("") }
    val subtotal = (cantidad.toDoubleOrNull() ?: 0.0) * (precio.toDoubleOrNull() ?: 0.0)

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow)

    Column(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 20.dp).padding(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Box(modifier = Modifier.width(40.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(MaterialTheme.colorScheme.outlineVariant.copy(0.5f)).align(Alignment.CenterHorizontally))
        Text(stringResource(R.string.compra_agregar_producto), fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)

        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text(stringResource(R.string.producto_nombre)) },
            modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors)
        OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text(stringResource(R.string.producto_descripcion_opcional)) },
            modifier = Modifier.fillMaxWidth(), minLines = 2, maxLines = 3, shape = RoundedCornerShape(12.dp), colors = fieldColors)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = cantidad, onValueChange = { cantidad = it }, label = { Text(stringResource(R.string.producto_cantidad)) },
                modifier = Modifier.weight(1f), singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp), colors = fieldColors)
            OutlinedTextField(value = precio, onValueChange = { precio = it }, label = { Text(stringResource(R.string.nueva_compra_precio_label)) },
                modifier = Modifier.weight(1f), singleLine = true,
                leadingIcon = { Text("$", color = MaterialTheme.colorScheme.outline, fontWeight = FontWeight.Bold) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(12.dp), colors = fieldColors)
        }

        if (subtotal > 0) {
            Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.primary.copy(0.08f)) {
                Row(modifier = Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(stringResource(R.string.nueva_compra_subtotal_label, cantidad), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                    Text("$ %,.2f".format(subtotal), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onCancelar, modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(14.dp)) { Text(stringResource(R.string.cancelar)) }
            Button(
                onClick = {
                    onGuardar(Producto(id = System.currentTimeMillis().toInt(), codigo = "", nombre = nombre,
                        descripcion = descripcion, cantidad = cantidad.toIntOrNull() ?: 1, precio = precio.toDoubleOrNull() ?: 0.0))
                },
                modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                enabled = nombre.isNotBlank() && precio.isNotBlank()
            ) { Text(stringResource(R.string.guardar), fontWeight = FontWeight.SemiBold) }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun NuevaCompraPreview() { SuperAhorroTheme { NuevaCompraScreen() } }