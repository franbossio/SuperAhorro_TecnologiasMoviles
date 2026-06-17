package com.undef.superahorro.BossioCorrea.ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.undef.superahorro.BossioCorrea.R
import com.undef.superahorro.BossioCorrea.ui.components.LabelCaps
import com.undef.superahorro.BossioCorrea.ui.components.MainBottomBar
import com.undef.superahorro.BossioCorrea.ui.navigation.Routes
import com.undef.superahorro.BossioCorrea.ui.navigation.UiState
import com.undef.superahorro.BossioCorrea.ui.theme.*
import kotlinx.coroutines.launch

// Colores del gradiente verde — internal para que HomePerfilSheet los use en el mismo paquete
internal val HeroBgTop    = Color(0xFF006C49)
internal val HeroBgBottom = Color(0xFF00A066)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    vm                  : HomeViewModel = viewModel(),
    onNuevaCompraClick  : () -> Unit = {},
    onListadoClick      : () -> Unit = {},
    onEstadisticasClick : () -> Unit = {},
    onPromocionesClick  : () -> Unit = {},
    onPerfilClick       : () -> Unit = {},
    onSettingsClick     : () -> Unit = {},
    onChatClick         : () -> Unit = {},
    onCerrarSesionClick : () -> Unit = {},
    currentRoute        : String = Routes.HOME,
    onTabClick          : (String) -> Unit = {}
) {
    val uiState        by vm.uiState.collectAsStateWithLifecycle()
    val notificaciones by vm.notificaciones.collectAsStateWithLifecycle()
    val sheetState     = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope          = rememberCoroutineScope()

    var showSheet            by remember { mutableStateOf(false) }
    var showLogoutDialog     by remember { mutableStateOf(false) }
    var showNotificaciones   by remember { mutableStateOf(false) }
    var notificacionesVistas by remember { mutableStateOf(false) }
    var perfilNombre         by remember { mutableStateOf("") }
    var perfilApellido       by remember { mutableStateOf("") }
    var perfilEmail          by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { vm.cargar() }

    LaunchedEffect(uiState) {
        if (uiState is UiState.Success) {
            val d = (uiState as UiState.Success<HomeData>).data
            perfilNombre   = d.usuarioNombre
            perfilApellido = d.usuarioApellido
            perfilEmail    = d.usuarioEmail
        }
    }

    val pulse = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by pulse.animateFloat(
        initialValue  = 0.25f, targetValue = 0.45f,
        animationSpec = infiniteRepeatable(tween(1100, easing = EaseInOutSine), RepeatMode.Reverse),
        label         = "pulseAlpha"
    )

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(stringResource(R.string.perfil_cerrar_sesion), fontWeight = FontWeight.Bold) },
            text  = { Text(stringResource(R.string.perfil_cerrar_sesion_confirmacion), color = MaterialTheme.colorScheme.onSurfaceVariant) },
            confirmButton = {
                Button(
                    onClick  = { showLogoutDialog = false; onCerrarSesionClick() },
                    colors   = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape    = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) { Text(stringResource(R.string.perfil_cerrar_sesion), fontWeight = FontWeight.SemiBold) }
            },
            dismissButton = {
                OutlinedButton(
                    onClick  = { showLogoutDialog = false },
                    shape    = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) { Text(stringResource(R.string.cancelar)) }
            },
            shape          = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState       = sheetState,
            containerColor   = MaterialTheme.colorScheme.surfaceContainerLow,
            shape            = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            dragHandle = {
                Column(
                    modifier            = Modifier.fillMaxWidth().padding(top = 14.dp, bottom = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(Modifier.width(40.dp).height(4.dp).clip(CircleShape)
                        .background(MaterialTheme.colorScheme.outlineVariant))
                }
            }
        ) {
            PerfilBottomSheetContent(
                nombre              = perfilNombre,
                apellido            = perfilApellido,
                email               = perfilEmail,
                onVerPerfilClick    = { scope.launch { sheetState.hide() }.invokeOnCompletion { showSheet = false; onPerfilClick() } },
                onEstadisticasClick = { scope.launch { sheetState.hide() }.invokeOnCompletion { showSheet = false; onEstadisticasClick() } },
                onCerrarSesionClick = { scope.launch { sheetState.hide() }.invokeOnCompletion { showSheet = false; showLogoutDialog = true } },
                onCerrarClick       = { scope.launch { sheetState.hide() }.invokeOnCompletion { showSheet = false } }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(32.dp).clip(CircleShape).clickable { showSheet = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter            = painterResource(R.drawable.logo_sin_letra),
                                contentDescription = "Logo",
                                modifier           = Modifier.size(110.dp)
                            )
                        }
                        Spacer(Modifier.width(9.dp))
                        Text(
                            stringResource(R.string.app_name_mayuscula),
                            fontWeight = FontWeight.ExtraBold, fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.primary, letterSpacing = (-0.3).sp
                        )
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showNotificaciones = true; notificacionesVistas = true }) {
                            Icon(Icons.Default.Notifications, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (notificaciones.isNotEmpty() && !notificacionesVistas) {
                            Box(
                                modifier = Modifier.size(8.dp).align(Alignment.TopEnd)
                                    .offset(x = (-10).dp, y = 10.dp).clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.error)
                            )
                        }
                        DropdownMenu(
                            expanded         = showNotificaciones,
                            onDismissRequest = { showNotificaciones = false },
                            modifier         = Modifier.width(300.dp)
                        ) {
                            Text(
                                stringResource(R.string.notificaciones_titulo),
                                fontWeight = FontWeight.Bold, fontSize = 16.sp,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(.4f))
                            if (notificaciones.isEmpty()) {
                                Text(
                                    stringResource(R.string.notificaciones_vacio),
                                    color    = MaterialTheme.colorScheme.outline, fontSize = 13.sp,
                                    modifier = Modifier.padding(16.dp)
                                )
                            } else {
                                notificaciones.forEach { promo ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(stringResource(R.string.notificaciones_nueva_oferta),
                                                    fontSize = 11.sp, fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary)
                                                Text(promo.producto, fontWeight = FontWeight.SemiBold,
                                                    fontSize = 13.sp, maxLines = 2,
                                                    overflow = TextOverflow.Ellipsis,
                                                    color = MaterialTheme.colorScheme.onSurface)
                                                val descuento = promo.precioSinDescuento?.let { original ->
                                                    if (original > promo.precio && original > 0)
                                                        (((original - promo.precio) / original) * 100).toInt()
                                                    else null
                                                }
                                                Text(
                                                    listOfNotNull(promo.supermercado, descuento?.let { "-$it%" },
                                                        "$ %.2f".format(promo.precio)).joinToString(" · "),
                                                    fontSize = 12.sp, color = MaterialTheme.colorScheme.outline
                                                )
                                            }
                                        },
                                        leadingIcon = {
                                            Icon(Icons.Default.LocalOffer, null, tint = MaterialTheme.colorScheme.primary)
                                        },
                                        onClick = { showNotificaciones = false; onPromocionesClick() }
                                    )
                                }
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(.4f))
                            DropdownMenuItem(
                                text    = { Text(stringResource(R.string.notificaciones_ver_todas),
                                    fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary) },
                                onClick = { showNotificaciones = false; onPromocionesClick() }
                            )
                        }
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            )
        },
        floatingActionButton = {
            Box(contentAlignment = Alignment.Center) {
                Box(Modifier.size(62.dp).clip(CircleShape)
                    .background(HeroBgBottom.copy(alpha = pulseAlpha * 0.5f)))
                Image(
                    painter            = painterResource(R.drawable.ic_asistente_chat),
                    contentDescription = stringResource(R.string.home_asistente),
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier
                        .size(58.dp)
                        .shadow(10.dp, CircleShape, spotColor = HeroBgTop)
                        .clip(CircleShape)
                        .clickable { onChatClick() }
                )
                Box(
                    Modifier.align(Alignment.TopEnd).offset(x = (-4).dp, y = 4.dp)
                        .size(13.dp).clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerLow),
                    contentAlignment = Alignment.Center
                ) {
                    Box(Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF00E676)))
                }
            }
        },
        bottomBar     = { MainBottomBar(currentRoute = currentRoute, onTabClick = onTabClick) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        when (val state = uiState) {
            is UiState.Loading -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
            is UiState.Error -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                Text(stringResource(R.string.error_generico))
            }
            is UiState.Success -> {
                val data = state.data
                val currentLocale = java.util.Locale.getDefault()
                val dayLabels = remember(currentLocale) {
                    (1..7).map { dayNum ->
                        java.time.DayOfWeek.of(dayNum)
                            .getDisplayName(java.time.format.TextStyle.NARROW, currentLocale)
                    }
                }

                Column(
                    modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
                ) {
                    HomeHeroCard(data = data, dayLabels = dayLabels, pulseAlpha = pulseAlpha)

                    Spacer(Modifier.height(18.dp))

                    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                        Box(Modifier.fillMaxWidth(0.92f).height(56.dp).align(Alignment.BottomCenter)
                            .offset(y = 8.dp).blur(20.dp).clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = pulseAlpha)))
                        Button(
                            onClick   = onNuevaCompraClick,
                            modifier  = Modifier.fillMaxWidth().height(56.dp),
                            shape     = RoundedCornerShape(16.dp),
                            colors    = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor   = MaterialTheme.colorScheme.onPrimary
                            ),
                            elevation = ButtonDefaults.buttonElevation(4.dp)
                        ) {
                            Icon(Icons.Default.AddShoppingCart, null, modifier = Modifier.size(22.dp))
                            Spacer(Modifier.width(10.dp))
                            Text(stringResource(R.string.home_nueva_compra), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    HomeUltimaCompra(data = data, onListadoClick = onListadoClick)

                    Spacer(Modifier.height(18.dp))

                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        LabelCaps(stringResource(R.string.home_resumen_actividad))
                        Spacer(Modifier.height(10.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            KpiCard(Modifier.weight(1f), Icons.Default.ShoppingCart,
                                "${data.cantidadCompras}", stringResource(R.string.home_compras),
                                iconBg   = MaterialTheme.colorScheme.primaryContainer.copy(.35f),
                                iconTint = MaterialTheme.colorScheme.primary)
                            KpiCard(Modifier.weight(1f), Icons.Default.Payments,
                                "$ %,.0f".format(if (data.cantidadCompras > 0) data.gastoMes / data.cantidadCompras else 0.0),
                                stringResource(R.string.home_prom),
                                iconBg   = MaterialTheme.colorScheme.tertiaryContainer.copy(.35f),
                                iconTint = MaterialTheme.colorScheme.tertiary)
                            KpiCard(Modifier.weight(1f), Icons.Default.TrendingDown,
                                "15%", stringResource(R.string.home_ahorro),
                                iconBg   = MaterialTheme.colorScheme.errorContainer.copy(.25f),
                                iconTint = MaterialTheme.colorScheme.error)
                        }
                    }

                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}

// ── Hero card con gradiente verde y gráfico de barras semanal ─────────────────

@Composable
private fun HomeHeroCard(data: HomeData, dayLabels: List<String>, pulseAlpha: Float) {
    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(HeroBgTop, HeroBgBottom, Color(0xFF00C27A)),
                        start  = Offset(0f, 0f), end = Offset(900f, 550f)
                    )
                )
        ) {
            Box(Modifier.size(160.dp).align(Alignment.TopEnd)
                .offset(x = 40.dp, y = (-40).dp).clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f)))
            Box(Modifier.size(90.dp).align(Alignment.BottomStart)
                .offset(x = (-25).dp, y = 25.dp).clip(CircleShape)
                .background(Color.White.copy(alpha = 0.06f)))

            Column(modifier = Modifier.padding(22.dp)) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Column {
                        Text(stringResource(R.string.home_bienvenida, data.saludo.substringBefore(" ")),
                            fontSize = 15.sp, color = Color.White.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Medium)
                        Text(stringResource(R.string.resumen),
                            fontSize = 13.sp, color = Color.White.copy(alpha = 0.58f))
                    }
                    Box(modifier = Modifier.clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.18f))
                        .padding(horizontal = 12.dp, vertical = 5.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.ShoppingBag, null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Text(stringResource(R.string.home_cantidad_compras, data.cantidadCompras),
                                color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text("$ %,.2f".format(data.gastoMes),
                    fontSize = 36.sp, fontWeight = FontWeight.ExtraBold,
                    color = Color.White, letterSpacing = (-0.72).sp)
                Text(stringResource(R.string.home_gasto_mes),
                    fontSize = 13.sp, color = Color.White.copy(alpha = 0.62f))
                Spacer(Modifier.height(20.dp))

                val maxGasto = data.gastosPorDia.maxOrNull() ?: 0.0
                val barHeights = if (maxGasto > 0)
                    data.gastosPorDia.map { (it / maxGasto).toFloat().coerceAtLeast(0.05f) }
                else List(7) { 0.05f }
                val todayIndex = java.time.LocalDate.now().dayOfWeek.value - 1

                Row(Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment     = Alignment.Bottom) {
                    barHeights.forEachIndexed { i, h ->
                        val isToday = i == todayIndex
                        Column(Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom) {
                            Box(Modifier.fillMaxWidth().height((44 * h).dp)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(if (isToday) Color.White.copy(alpha = 0.95f) else Color.White.copy(alpha = 0.28f)))
                            Spacer(Modifier.height(4.dp))
                            Text(dayLabels[i], fontSize = 9.sp,
                                color      = if (isToday) Color.White else Color.White.copy(.50f),
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            }
        }
    }
}

// ── Tarjeta de la última compra ───────────────────────────────────────────────

@Composable
private fun HomeUltimaCompra(data: HomeData, onListadoClick: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            LabelCaps(stringResource(R.string.home_ultima_compra))
            TextButton(onClick = onListadoClick, contentPadding = PaddingValues(0.dp)) {
                Text(stringResource(R.string.home_ver),
                    color = MaterialTheme.colorScheme.primary, fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold)
            }
        }
        Spacer(Modifier.height(8.dp))
        Surface(
            modifier        = Modifier.fillMaxWidth().clickable { onListadoClick() },
            shape           = RoundedCornerShape(18.dp),
            color           = MaterialTheme.colorScheme.surfaceContainerLow,
            shadowElevation = 2.dp,
            border          = CardDefaults.outlinedCardBorder().copy(
                brush = SolidColor(MaterialTheme.colorScheme.outlineVariant.copy(.35f))
            )
        ) {
            Row(Modifier.fillMaxWidth().padding(16.dp),
                Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(52.dp).clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Storefront, null,
                            tint     = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(26.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(data.ultimoSuper, fontWeight = FontWeight.Bold,
                            fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text(data.ultimaFecha, fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.outline)
                        Spacer(Modifier.height(5.dp))
                        Surface(shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(.4f)) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(Modifier.size(6.dp).clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary))
                                Text(stringResource(R.string.home_completada), fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("$ %,.0f".format(data.ultimoTotal),
                        fontSize = 20.sp, fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary)
                    Icon(Icons.Default.ChevronRight, null,
                        tint     = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomePreview() { SuperAhorroTheme { HomeScreen() } }
