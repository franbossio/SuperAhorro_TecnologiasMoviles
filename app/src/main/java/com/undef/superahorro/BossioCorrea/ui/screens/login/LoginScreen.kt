package com.undef.superahorro.BossioCorrea.ui.screens.login

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.undef.superahorro.BossioCorrea.R
import com.undef.superahorro.BossioCorrea.ui.components.LabelCaps
import com.undef.superahorro.BossioCorrea.ui.components.StitchTopBar
import com.undef.superahorro.BossioCorrea.ui.navigation.UiState
import com.undef.superahorro.BossioCorrea.ui.theme.SuperAhorroTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    vm                    : LoginViewModel = viewModel(),
    onLoginExitoso        : () -> Unit = {},
    onRegistrarseClick    : () -> Unit = {},
    onBackClick           : () -> Unit = {},
    onOlvidoPasswordClick : () -> Unit = {}
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    var email       by remember { mutableStateOf("") }
    var password    by remember { mutableStateOf("") }
    var verPassword by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val puedeUsarBiometria = remember {
        BiometricManager.from(context).canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
            BiometricManager.Authenticators.BIOMETRIC_WEAK
        ) == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun lanzarBiometria() {
        val activity = context as FragmentActivity
        val prompt = BiometricPrompt(
            activity,
            ContextCompat.getMainExecutor(context),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    vm.loginConBiometria(onLoginExitoso)
                }
            }
        )
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(context.getString(R.string.login_biometria_titulo))
            .setSubtitle(context.getString(R.string.login_biometria_subtitulo))
            .setNegativeButtonText(context.getString(R.string.cancelar))
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.BIOMETRIC_WEAK
            )
            .build()
        prompt.authenticate(promptInfo)
    }

    Scaffold(
        topBar = { StitchTopBar(title = stringResource(R.string.app_name), onBackClick = onBackClick) },
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

            // ── Heading ───────────────────────────────────────────────────
            Text(
                text      = stringResource(R.string.bienvenido),
                fontSize  = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color     = MaterialTheme.colorScheme.onSurface,
                letterSpacing = (-0.72).sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text  = stringResource(R.string.introduccion),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center,
                modifier  = Modifier.widthIn(max = 280.dp)
            )

            Spacer(Modifier.height(32.dp))

            // ── Form Card ─────────────────────────────────────────────────
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
                    // Email
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        LabelCaps("EMAIL ")
                        OutlinedTextField(
                            value         = email,
                            onValueChange = { email = it },
                            modifier      = Modifier.fillMaxWidth(),
                            placeholder   = { Text("nombre@email.com", color = MaterialTheme.colorScheme.outline) },
                            singleLine    = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            shape         = RoundedCornerShape(12.dp),
                            isError       = uiState is UiState.Error,
                            colors        = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor   = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            )
                        )
                    }

                    // Password
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        LabelCaps(stringResource(R.string.login_password).uppercase())
                        OutlinedTextField(
                            value         = password,
                            onValueChange = { password = it },
                            modifier      = Modifier.fillMaxWidth(),
                            singleLine    = true,
                            visualTransformation = if (verPassword) VisualTransformation.None
                            else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            shape         = RoundedCornerShape(12.dp),
                            isError       = uiState is UiState.Error,
                            trailingIcon  = {
                                IconButton(onClick = { verPassword = !verPassword }) {
                                    Icon(
                                        if (verPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        null, tint = MaterialTheme.colorScheme.outline
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor   = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            )
                        )
                    }

                    if (uiState is UiState.Error) {
                        Text(
                            (uiState as UiState.Error).mensaje,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    TextButton(
                        onClick  = onOlvidoPasswordClick,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            stringResource(R.string.login_olvide_password),
                            color    = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp
                        )
                    }

                    // Botón ingresar
                    when (uiState) {
                        is UiState.Loading -> Box(Modifier.fillMaxWidth(), Alignment.Center) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                        else -> Button(
                            onClick   = { vm.login(email, password, onLoginExitoso) },
                            modifier  = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape     = RoundedCornerShape(12.dp),
                            colors    = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            Text(
                                stringResource(R.string.login_btn_ingresar),
                                fontWeight = FontWeight.SemiBold,
                                fontSize   = 16.sp
                            )
                        }
                    }

                    // Botón biométrico (solo si el dispositivo lo soporta)
                    if (puedeUsarBiometria) {
                        Row(
                            modifier            = Modifier.fillMaxWidth(),
                            verticalAlignment   = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            HorizontalDivider(modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.outlineVariant)
                            Text(stringResource(R.string.login_o),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline)
                            HorizontalDivider(modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.outlineVariant)
                        }

                        OutlinedButton(
                            onClick  = { lanzarBiometria() },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape    = RoundedCornerShape(12.dp),
                            colors   = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(Icons.Default.Fingerprint, null, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.login_biometria_btn),
                                fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            TextButton(onClick = onRegistrarseClick) {
                Text(
                    stringResource(R.string.login_sin_cuenta),
                    color      = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginPreview() { SuperAhorroTheme { LoginScreen() } }