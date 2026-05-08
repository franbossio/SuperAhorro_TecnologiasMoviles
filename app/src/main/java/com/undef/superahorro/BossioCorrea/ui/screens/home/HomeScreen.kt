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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.undef.superahorro.BossioCorrea.R
import com.undef.superahorro.BossioCorrea.data.mock.usuarioMock
import com.undef.superahorro.BossioCorrea.ui.components.LabelCaps
import com.undef.superahorro.BossioCorrea.ui.navigation.UiState
import com.undef.superahorro.BossioCorrea.ui.theme.*
import kotlinx.coroutines.launch

// ─── Colores locales del Home ──────────────────────────────────────────────────
private val CardBg       = Color(0xFFFFFFFF)
private val CardBorder   = Color(0xFFBBCABF).copy(alpha = 0.35f)
private val HeroBgTop    = Color(0xFF006C49)
private val HeroBgBottom = Color(0xFF00A066)
private val BadgeBg      = Color(0xFFE8F7F1)
private val BadgeText    = Color(0xFF005236)

// ─── Datos del mini chart ──────────────────────────────────────────────────────
private val barHeights = listOf(0.38f, 0.55f, 0.45f, 0.72f, 0.60f, 0.88f, 0.70f)
private val barDays    = listOf("L", "M", "X", "J", "V", "S", "D")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    vm                  : HomeViewModel = viewModel(),
    onNuevaCompraClick  : () -> Unit = {},
    onListadoClick      : () -> Unit = {},
    onHistorialClick    : () -> Unit = {},
    onEstadisticasClick : () -> Unit = {},
    onPerfilClick       : () -> Unit = {},
    onSettingsClick     : () -> Unit = {}
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    // ── Bottom Sheet state (estilo Mercado Pago) ───────────────────────────
    val sheetState    = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope         = rememberCoroutineScope()
    var showSheet     by remember { mutableStateOf(false) }

    // ── Animación pulsante del botón principal ─────────────────────────────
    val pulse = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by pulse.animateFloat(
        initialValue  = 0.25f,
        targetValue   = 0.45f,
        animationSpec = infiniteRepeatable(tween(1100, easing = EaseInOutSine), RepeatMode.Reverse),
        label         = "pulseAlpha"
    )

    // ── Bottom Sheet de Perfil ─────────────────────────────────────────────
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest  = { showSheet = false },
            sheetState        = sheetState,
            containerColor    = Color.White,
            shape             = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            dragHandle        = {
                // Handle visual igual que Mercado Pago
                Column(
                    modifier            = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp, bottom = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFDDE1E0))
                    )
                }
            }
        ) {
            PerfilBottomSheetContent(
                onVerPerfilClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        showSheet = false
                        onPerfilClick()
                    }
                },
                onCerrarClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        showSheet = false
                    }
                }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .clickable { showSheet = true },   // ← AQUÍ abre el sheet
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter            = painterResource(R.drawable.logo_sin_letra),
                                contentDescription = "Logo Super Ahorro",
                                modifier           = Modifier.size(110.dp)
                            )
                        }
                        Spacer(Modifier.width(9.dp))
                        Text(
                            stringResource(R.string.app_name_mayuscula),
                            fontWeight    = FontWeight.ExtraBold,
                            fontSize      = 17.sp,
                            color         = StitchPrimary,
                            letterSpacing = (-0.3).sp
                        )
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.Notifications, null, tint = StitchOutline)
                        }
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = (-10).dp, y = 10.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE53935))
                        )
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, null, tint = StitchOutline)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF8FAFC))
            )
        },
        containerColor = StitchBackground
    ) { padding ->

        when (val state = uiState) {
            is UiState.Loading -> Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding), Alignment.Center
            ) {
                CircularProgressIndicator(color = StitchPrimary)
            }

            is UiState.Error -> Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding), Alignment.Center
            ) {
                Text(stringResource(R.string.error_generico))
            }

            is UiState.Success -> {
                val data = state.data

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                ) {

                    // ══════════════════════════════════════════════════════
                    //  HERO CARD — gradiente verde
                    // ══════════════════════════════════════════════════════
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(24.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(HeroBgTop, HeroBgBottom, Color(0xFF00C27A)),
                                        start = Offset(0f, 0f),
                                        end = Offset(900f, 550f)
                                    )
                                )
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(160.dp)
                                    .align(Alignment.TopEnd)
                                    .offset(x = 40.dp, y = (-40).dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.08f))
                            )
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .align(Alignment.BottomStart)
                                    .offset(x = (-25).dp, y = 25.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.06f))
                            )

                            Column(modifier = Modifier.padding(22.dp)) {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    Arrangement.SpaceBetween, Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            "Hola, ${data.saludo.substringBefore(" ")} 👋",
                                            fontSize   = 15.sp,
                                            color      = Color.White.copy(alpha = 0.85f),
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            stringResource(R.string.resumen),
                                            fontSize = 13.sp,
                                            color    = Color.White.copy(alpha = 0.58f)
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(Color.White.copy(alpha = 0.18f))
                                            .padding(horizontal = 12.dp, vertical = 5.dp)
                                    ) {
                                        Row(
                                            verticalAlignment     = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.ShoppingBag, null,
                                                tint     = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                "${data.cantidadCompras} compras",
                                                color      = Color.White, fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }

                                Spacer(Modifier.height(16.dp))

                                Text(
                                    "$ %,.2f".format(data.gastoMes),
                                    fontSize      = 36.sp,
                                    fontWeight    = FontWeight.ExtraBold,
                                    color         = Color.White,
                                    letterSpacing = (-0.72).sp
                                )
                                Text(
                                    stringResource(R.string.home_gasto_mes),
                                    fontSize = 13.sp,
                                    color    = Color.White.copy(alpha = 0.62f)
                                )

                                Spacer(Modifier.height(20.dp))

                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                                    verticalAlignment     = Alignment.Bottom
                                ) {
                                    barHeights.forEachIndexed { i, h ->
                                        val isLast = i == barHeights.lastIndex
                                        Column(
                                            modifier            = Modifier.weight(1f),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Bottom
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height((44 * h).dp)
                                                    .clip(
                                                        RoundedCornerShape(
                                                            topStart = 4.dp,
                                                            topEnd = 4.dp
                                                        )
                                                    )
                                                    .background(
                                                        if (isLast) Color.White.copy(alpha = 0.95f)
                                                        else Color.White.copy(alpha = 0.28f)
                                                    )
                                            )
                                            Spacer(Modifier.height(4.dp))
                                            Text(
                                                barDays[i],
                                                fontSize   = 9.sp,
                                                color      = if (isLast) Color.White else Color.White.copy(.50f),
                                                fontWeight = if (isLast) FontWeight.Bold else FontWeight.Normal
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ══════════════════════════════════════════════════════
                    //  ACCIONES RÁPIDAS
                    // ══════════════════════════════════════════════════════
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        LabelCaps(stringResource(R.string.home_acciones_rapidas))
                        Spacer(Modifier.height(10.dp))
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            QuickActionCard(
                                modifier    = Modifier.weight(1f),
                                icon        = Icons.Default.ListAlt,
                                label       = stringResource(R.string.home_mis_compras),
                                accentColor = Color(0xFF1565C0),
                                bgColor     = Color(0xFFE8EEF9),
                                onClick     = onListadoClick
                            )
                            QuickActionCard(
                                modifier    = Modifier.weight(1f),
                                icon        = Icons.Default.CalendarMonth,
                                label       = stringResource(R.string.home_historial),
                                accentColor = Color(0xFF6A1B9A),
                                bgColor     = Color(0xFFF3E5F5),
                                onClick     = onHistorialClick
                            )
                            QuickActionCard(
                                modifier    = Modifier.weight(1f),
                                icon        = Icons.Default.BarChart,
                                label       = stringResource(R.string.home_estadisticas),
                                accentColor = Color(0xFFE65100),
                                bgColor     = Color(0xFFFFF3E0),
                                onClick     = onEstadisticasClick
                            )
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    // ══════════════════════════════════════════════════════
                    //  BOTÓN NUEVA COMPRA con glow pulsante
                    // ══════════════════════════════════════════════════════
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.92f)
                                .height(56.dp)
                                .align(Alignment.BottomCenter)
                                .offset(y = 8.dp)
                                .blur(20.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(StitchPrimary.copy(alpha = pulseAlpha))
                        )
                        Button(
                            onClick   = onNuevaCompraClick,
                            modifier  = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape     = RoundedCornerShape(16.dp),
                            colors    = ButtonDefaults.buttonColors(
                                containerColor = StitchPrimary,
                                contentColor   = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(4.dp)
                        ) {
                            Icon(Icons.Default.AddShoppingCart, null, modifier = Modifier.size(22.dp))
                            Spacer(Modifier.width(10.dp))
                            Text(
                                stringResource(R.string.home_nueva_compra),
                                fontWeight = FontWeight.Bold, fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    // ══════════════════════════════════════════════════════
                    //  ÚLTIMA COMPRA
                    // ══════════════════════════════════════════════════════
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            Arrangement.SpaceBetween, Alignment.CenterVertically
                        ) {
                            LabelCaps(stringResource(R.string.home_ultima_compra))
                            TextButton(onClick = onListadoClick, contentPadding = PaddingValues(0.dp)) {
                                Text(
                                    "Ver todas →", color = StitchPrimary,
                                    fontSize = 12.sp, fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(
                                    4.dp,
                                    RoundedCornerShape(18.dp),
                                    ambientColor = StitchPrimary.copy(.08f)
                                )
                                .clip(RoundedCornerShape(18.dp))
                                .background(CardBg)
                                .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
                                .clickable { onListadoClick() }
                                .padding(16.dp)
                        ) {
                            Row(
                                Modifier.fillMaxWidth(),
                                Arrangement.SpaceBetween, Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(
                                                        StitchPrimary.copy(.12f),
                                                        StitchPrimary.copy(.05f)
                                                    )
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Storefront, null,
                                            tint = StitchPrimary, modifier = Modifier.size(26.dp)
                                        )
                                    }
                                    Spacer(Modifier.width(14.dp))
                                    Column {
                                        Text(
                                            data.ultimoSuper,
                                            fontWeight = FontWeight.Bold,
                                            fontSize   = 16.sp,
                                            color      = StitchOnBackground
                                        )
                                        Text(data.ultimaFecha, fontSize = 13.sp, color = StitchOutline)
                                        Spacer(Modifier.height(5.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(20.dp))
                                                .background(BadgeBg)
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Row(
                                                verticalAlignment     = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Box(
                                                    Modifier
                                                        .size(6.dp)
                                                        .clip(CircleShape)
                                                        .background(BadgeText)
                                                )
                                                Text(
                                                    "Completada", fontSize = 10.sp,
                                                    color = BadgeText, fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        }
                                    }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        "$ %,.0f".format(data.ultimoTotal),
                                        fontSize   = 20.sp, fontWeight = FontWeight.ExtraBold,
                                        color      = StitchPrimary
                                    )
                                    Icon(
                                        Icons.Default.ChevronRight, null,
                                        tint = StitchOutline, modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    // ══════════════════════════════════════════════════════
                    //  KPIs
                    // ══════════════════════════════════════════════════════
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        LabelCaps("Resumen de actividad")
                        Spacer(Modifier.height(10.dp))
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            KpiCard(
                                modifier = Modifier.weight(1f),
                                icon     = Icons.Default.ShoppingCart,
                                valor    = "${data.cantidadCompras}",
                                label    = "Compras",
                                iconBg   = Color(0xFFE8F7F1),
                                iconTint = StitchPrimary
                            )
                            KpiCard(
                                modifier = Modifier.weight(1f),
                                icon     = Icons.Default.Payments,
                                valor    = "$ %,.0f".format(
                                    if (data.cantidadCompras > 0) data.gastoMes / data.cantidadCompras else 0.0
                                ),
                                label    = "Promedio",
                                iconBg   = Color(0xFFE8EEF9),
                                iconTint = Color(0xFF1565C0)
                            )
                            KpiCard(
                                modifier = Modifier.weight(1f),
                                icon     = Icons.Default.TrendingDown,
                                valor    = "15%",
                                label    = "Ahorro",
                                iconBg   = Color(0xFFFFF3E0),
                                iconTint = Color(0xFFE65100)
                            )
                        }
                    }

                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}

// ─── Bottom Sheet Contenido (estilo Mercado Pago) ─────────────────────────────
@Composable
private fun PerfilBottomSheetContent(
    onVerPerfilClick : () -> Unit,
    onCerrarClick    : () -> Unit
) {
    val usuario = usuarioMock
    val iniciales = "${usuario.nombre.firstOrNull() ?: ""}${usuario.apellido.firstOrNull() ?: ""}"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        // ── Cabecera: avatar + datos ───────────────────────────────────────
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar con iniciales
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(HeroBgTop, HeroBgBottom))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = iniciales,
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = Color.White
                )
            }

            // Nombre + email
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = "${usuario.nombre} ${usuario.apellido}",
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color(0xFF1A1C1A)
                )
                Text(
                    text     = usuario.email,
                    fontSize = 13.sp,
                    color    = Color(0xFF637067)
                )
                Spacer(Modifier.height(5.dp))
                // Badge "Super Ahorrador"
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(BadgeBg)
                        .padding(horizontal = 9.dp, vertical = 3.dp)
                ) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.Star, null,
                            tint     = BadgeText,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            "Super Ahorrador",
                            fontSize   = 11.sp,
                            color      = BadgeText,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = Color(0xFFF0F1EE), thickness = 1.dp)

        // ── Opciones del sheet ─────────────────────────────────────────────
        SheetMenuItem(
            icon    = Icons.Default.Person,
            title   = "Mi Perfil",
            subtitle = "Ver y editar mis datos",
            onClick  = onVerPerfilClick
        )
        SheetMenuItem(
            icon    = Icons.Default.BarChart,
            title   = "Mis estadísticas",
            subtitle = "Resumen de ahorro y gastos",
            onClick  = {}
        )
        SheetMenuItem(
            icon     = Icons.Default.Share,
            title    = "Compartir app",
            subtitle = "Invitá a un amigo",
            onClick  = {}
        )

        HorizontalDivider(color = Color(0xFFF0F1EE), thickness = 1.dp)

        // ── Cerrar ─────────────────────────────────────────────────────────
        TextButton(
            onClick  = onCerrarClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .height(48.dp)
        ) {
            Text(
                text       = "Cerrar",
                fontSize   = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color      = Color(0xFF637067)
            )
        }

        Spacer(Modifier.height(8.dp))
    }
}

// ─── Item de menú del sheet ────────────────────────────────────────────────────
@Composable
private fun SheetMenuItem(
    icon     : ImageVector,
    title    : String,
    subtitle : String,
    onClick  : () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Ícono con fondo suave
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE8F7F1)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = HeroBgTop, modifier = Modifier.size(22.dp))
        }
        // Texto
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = title,
                fontSize   = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color      = Color(0xFF1A1C1A)
            )
            Text(text = subtitle, fontSize = 12.sp, color = Color(0xFF637067))
        }
        Icon(
            Icons.Default.ChevronRight, null,
            tint     = Color(0xFFBBCABF),
            modifier = Modifier.size(20.dp)
        )
    }
}

// ─── Quick Action Card ─────────────────────────────────────────────────────────
@Composable
private fun QuickActionCard(
    modifier    : Modifier,
    icon        : ImageVector,
    label       : String,
    accentColor : Color,
    bgColor     : Color,
    onClick     : () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(3.dp, RoundedCornerShape(16.dp), ambientColor = accentColor.copy(.10f))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, accentColor.copy(.12f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(vertical = 14.dp, horizontal = 8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier            = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = accentColor, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.height(7.dp))
            Text(
                label,
                fontSize   = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color      = StitchOnSurface,
                textAlign  = TextAlign.Center,
                maxLines   = 2,
                lineHeight = 14.sp
            )
        }
    }
}

// ─── KPI Card ─────────────────────────────────────────────────────────────────
@Composable
private fun KpiCard(
    modifier : Modifier,
    icon     : ImageVector,
    valor    : String,
    label    : String,
    iconBg   : Color,
    iconTint : Color
) {
    Box(
        modifier = modifier
            .shadow(2.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconTint, modifier = Modifier.size(17.dp))
            }
            Text(valor, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = StitchOnSurface)
            Text(label, fontSize = 11.sp, color = StitchOutline, fontWeight = FontWeight.Medium)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomePreview() { SuperAhorroTheme { HomeScreen() } }