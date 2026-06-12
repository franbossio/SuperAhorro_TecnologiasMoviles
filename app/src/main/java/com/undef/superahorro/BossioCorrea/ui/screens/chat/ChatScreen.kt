package com.undef.superahorro.BossioCorrea.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.undef.superahorro.BossioCorrea.R
import com.undef.superahorro.BossioCorrea.ui.components.StitchTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    vm          : ChatViewModel = viewModel(),
    onBackClick : () -> Unit = {}
) {
    val mensajes    by vm.mensajes.collectAsStateWithLifecycle()
    val escribiendo by vm.escribiendo.collectAsStateWithLifecycle()
    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto-scroll al último mensaje
    LaunchedEffect(mensajes.size, escribiendo) {
        val total = mensajes.size + if (escribiendo) 1 else 0
        if (total > 0) listState.animateScrollToItem(total - 1)
    }

    Scaffold(
        topBar = { StitchTopBar(stringResource(R.string.chat_titulo), onBackClick) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // ── Mensajes ─────────────────────────────────────────────────────
            LazyColumn(
                state               = listState,
                modifier            = Modifier.weight(1f).fillMaxWidth(),
                contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (mensajes.isEmpty()) {
                    item { ChatBienvenida(onSugerenciaClick = { vm.enviar(it) }) }
                }
                items(mensajes) { mensaje -> BurbujaMensaje(mensaje) }
                if (escribiendo) {
                    item { IndicadorEscribiendo() }
                }
            }

            // ── Input ────────────────────────────────────────────────────────
            Surface(
                color           = MaterialTheme.colorScheme.surfaceContainerLow,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().navigationBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value         = input,
                        onValueChange = { input = it },
                        modifier      = Modifier.weight(1f),
                        placeholder   = { Text(stringResource(R.string.chat_placeholder)) },
                        shape         = RoundedCornerShape(24.dp),
                        maxLines      = 3
                    )
                    FilledIconButton(
                        onClick  = { vm.enviar(input); input = "" },
                        enabled  = input.isNotBlank() && !escribiendo,
                        modifier = Modifier.size(48.dp),
                        colors   = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor   = Color.White
                        )
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, null, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatBienvenida(onSugerenciaClick: (String) -> Unit) {
    val sugerencias = listOf(
        stringResource(R.string.chat_sugerencia_1),
        stringResource(R.string.chat_sugerencia_2),
        stringResource(R.string.chat_sugerencia_3)
    )

    Column(
        modifier            = Modifier.fillMaxWidth().padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(72.dp).clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.SmartToy, null,
                tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
        }
        Text(
            stringResource(R.string.chat_bienvenida),
            style     = MaterialTheme.typography.bodyMedium,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier  = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(Modifier.height(4.dp))
        sugerencias.forEach { sugerencia ->
            Surface(
                shape    = RoundedCornerShape(20.dp),
                color    = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                modifier = Modifier.clip(RoundedCornerShape(20.dp))
                    .clickable { onSugerenciaClick(sugerencia) }
            ) {
                Text(
                    sugerencia,
                    modifier   = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun BurbujaMensaje(mensaje: MensajeChat) {
    val esUsuario = mensaje.esUsuario
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = if (esUsuario) Arrangement.End else Arrangement.Start
    ) {
        if (!esUsuario) {
            Box(
                modifier = Modifier.size(30.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.SmartToy, null,
                    tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
            }
            Spacer(Modifier.width(8.dp))
        }
        Surface(
            shape = RoundedCornerShape(
                topStart     = 16.dp,
                topEnd       = 16.dp,
                bottomStart  = if (esUsuario) 16.dp else 4.dp,
                bottomEnd    = if (esUsuario) 4.dp else 16.dp
            ),
            color = when {
                mensaje.esError -> MaterialTheme.colorScheme.errorContainer
                esUsuario       -> MaterialTheme.colorScheme.primary
                else            -> MaterialTheme.colorScheme.surfaceContainerLow
            },
            shadowElevation = 1.dp,
            modifier        = Modifier.widthIn(max = 290.dp)
        ) {
            Text(
                mensaje.texto,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                fontSize = 14.sp,
                color    = when {
                    mensaje.esError -> MaterialTheme.colorScheme.onErrorContainer
                    esUsuario       -> Color.White
                    else            -> MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}

@Composable
private fun IndicadorEscribiendo() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(30.dp).clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.SmartToy, null,
                tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.width(8.dp))
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow
        ) {
            Row(
                modifier              = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier    = Modifier.size(14.dp),
                    strokeWidth = 2.dp,
                    color       = MaterialTheme.colorScheme.primary
                )
                Text(
                    stringResource(R.string.chat_escribiendo),
                    fontSize = 13.sp,
                    color    = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
