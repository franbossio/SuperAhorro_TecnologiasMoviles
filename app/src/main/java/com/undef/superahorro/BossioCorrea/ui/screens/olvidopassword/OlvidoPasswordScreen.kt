package com.undef.superahorro.BossioCorrea.ui.screens.olvidopassword

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.undef.superahorro.BossioCorrea.R
import com.undef.superahorro.BossioCorrea.ui.components.LabelCaps
import com.undef.superahorro.BossioCorrea.ui.components.StitchTopBar
import com.undef.superahorro.BossioCorrea.ui.theme.SuperAhorroTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OlvidoPasswordScreen(
    onBackClick   : () -> Unit = {},
    onVolverLogin : () -> Unit = {}
) {
    var email      by remember { mutableStateOf("") }
    var enviado    by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            StitchTopBar(
                title       = stringResource(R.string.app_name),
                onBackClick = onBackClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(40.dp))

            // ── Heading animado ───────────────────────────────────────────
            AnimatedContent(
                targetState = enviado,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "heading_anim"
            ) { yaEnviado ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (yaEnviado) {
                        Icon(
                            imageVector        = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint               = MaterialTheme.colorScheme.primary,
                            modifier           = Modifier.size(56.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text          = stringResource(R.string.olvide_password_enviado_titulo),
                            fontSize      = 30.sp,
                            fontWeight    = FontWeight.ExtraBold,
                            color         = MaterialTheme.colorScheme.onSurface,
                            letterSpacing = (-0.6).sp,
                            textAlign     = TextAlign.Center
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text      = stringResource(R.string.olvide_password_enviado_desc, email),
                            style     = MaterialTheme.typography.bodyMedium,
                            color     = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.Center,
                            modifier  = Modifier.widthIn(max = 280.dp)
                        )
                    } else {
                        Text(
                            text          = stringResource(R.string.olvide_password_titulo),
                            fontSize      = 30.sp,
                            fontWeight    = FontWeight.ExtraBold,
                            color         = MaterialTheme.colorScheme.onSurface,
                            letterSpacing = (-0.6).sp,
                            textAlign     = TextAlign.Center
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text      = stringResource(R.string.olvide_password_desc),
                            style     = MaterialTheme.typography.bodyMedium,
                            color     = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.Center,
                            modifier  = Modifier.widthIn(max = 280.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // ── Card animada ──────────────────────────────────────────────
            AnimatedContent(
                targetState = enviado,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "card_anim"
            ) { yaEnviado ->
                Surface(
                    modifier        = Modifier.fillMaxWidth(),
                    shape           = RoundedCornerShape(16.dp),
                    color           = MaterialTheme.colorScheme.surfaceContainerLow,
                    tonalElevation  = 0.dp,
                    shadowElevation = 1.dp,
                    border          = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                        )
                    )
                ) {
                    Column(
                        modifier            = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (!yaEnviado) {

                            // ── Campo Email ───────────────────────────────
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                LabelCaps(stringResource(R.string.olvide_password_label_email))
                                OutlinedTextField(
                                    value         = email,
                                    onValueChange = { email = it; emailError = false },
                                    modifier      = Modifier.fillMaxWidth(),
                                    placeholder   = {
                                        Text(
                                            "nombre@email.com",
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Email,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.outline
                                        )
                                    },
                                    singleLine      = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    shape           = RoundedCornerShape(12.dp),
                                    isError         = emailError,
                                    supportingText  = if (emailError) {
                                        { Text("Ingresá un email válido", color = MaterialTheme.colorScheme.error) }
                                    } else null,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor   = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                    )
                                )
                            }

                            // ── Info tip ──────────────────────────────────
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.07f)
                            ) {
                                Text(
                                    text     = stringResource(R.string.olvide_password_info),
                                    style    = MaterialTheme.typography.bodySmall,
                                    color    = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }

                            // ── Botón enviar ──────────────────────────────
                            Button(
                                onClick = {
                                    if (email.isBlank() || !email.contains("@")) {
                                        emailError = true
                                    } else {
                                        enviado = true
                                    }
                                },
                                modifier  = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                shape     = RoundedCornerShape(12.dp),
                                colors    = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                            ) {
                                Text(
                                    text       = stringResource(R.string.olvide_password_btn_enviar),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize   = 16.sp
                                )
                            }

                        } else {

                            // ── Estado: email enviado ─────────────────────
                            Text(
                                text      = stringResource(R.string.olvide_password_revisar_spam),
                                style     = MaterialTheme.typography.bodyMedium,
                                color     = MaterialTheme.colorScheme.outline,
                                textAlign = TextAlign.Center,
                                modifier  = Modifier.fillMaxWidth()
                            )

                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                            )

                            // Reenviar (mockeado)
                            TextButton(
                                onClick  = { /* mock: reenviar */ },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text       = stringResource(R.string.olvide_password_reenviar),
                                    color      = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium,
                                    fontSize   = 14.sp
                                )
                            }

                            // Volver al login
                            Button(
                                onClick   = onVolverLogin,
                                modifier  = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                shape     = RoundedCornerShape(12.dp),
                                colors    = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                            ) {
                                Text(
                                    text       = stringResource(R.string.olvide_password_volver_login),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize   = 16.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun OlvidoPasswordPreview() {
    SuperAhorroTheme { OlvidoPasswordScreen() }
}