package com.undef.superahorro.BossioCorrea.ui.screens.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.undef.superahorro.BossioCorrea.R
import com.undef.superahorro.BossioCorrea.ui.components.LabelCaps
import com.undef.superahorro.BossioCorrea.ui.components.StitchTopBar
import com.undef.superahorro.BossioCorrea.ui.navigation.UiState
import com.undef.superahorro.BossioCorrea.ui.theme.SuperAhorroTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    vm                : RegisterViewModel = viewModel(),
    onRegistroExitoso : () -> Unit = {},
    onLoginClick      : () -> Unit = {},
    onBackClick       : () -> Unit = {}
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    var nombre      by remember { mutableStateOf("") }
    var apellido    by remember { mutableStateOf("") }
    var email       by remember { mutableStateOf("") }
    var password    by remember { mutableStateOf("") }
    var confirmar   by remember { mutableStateOf("") }
    var verPassword  by remember { mutableStateOf(false) }
    var verConfirmar by remember { mutableStateOf(false) }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor   = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
    )

    Scaffold(
        topBar = { StitchTopBar(stringResource(R.string.app_name), onBackClick) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(32.dp))

            Text(stringResource(R.string.register_btn_crear), fontSize = 36.sp, fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface, letterSpacing = (-0.72).sp)
            Spacer(Modifier.height(4.dp))
            Text(stringResource(R.string.register_empeza),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline)

            Spacer(Modifier.height(28.dp))

            // ── Form card ─────────────────────────────────────────────────
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(16.dp),
                color    = MaterialTheme.colorScheme.surfaceContainerLow,
                shadowElevation = 1.dp,
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )
                )
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {

                    // Nombre / Apellido
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            LabelCaps(stringResource(R.string.register_nombre))
                            OutlinedTextField(value = nombre, onValueChange = { nombre = it },
                                modifier = Modifier.fillMaxWidth(), singleLine = true,
                                shape = RoundedCornerShape(12.dp), colors = fieldColors)
                        }
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            LabelCaps(stringResource(R.string.register_apellido))
                            OutlinedTextField(value = apellido, onValueChange = { apellido = it },
                                modifier = Modifier.fillMaxWidth(), singleLine = true,
                                shape = RoundedCornerShape(12.dp), colors = fieldColors)
                        }
                    }

                    // Email
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        LabelCaps(stringResource(R.string.register_email))
                        OutlinedTextField(value = email, onValueChange = { email = it },
                            modifier = Modifier.fillMaxWidth(), singleLine = true,
                            placeholder = { Text("nombre@email.com", color = MaterialTheme.colorScheme.outline) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            shape = RoundedCornerShape(12.dp), colors = fieldColors)
                    }

                    // Password
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        LabelCaps(stringResource(R.string.register_password))
                        OutlinedTextField(
                            value = password, onValueChange = { password = it },
                            modifier = Modifier.fillMaxWidth(), singleLine = true,
                            visualTransformation = if (verPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            shape = RoundedCornerShape(12.dp), colors = fieldColors,
                            trailingIcon = {
                                IconButton(onClick = { verPassword = !verPassword }) {
                                    Icon(if (verPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        null, tint = MaterialTheme.colorScheme.outline)
                                }
                            }
                        )
                    }

                    // Confirmar
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        LabelCaps(stringResource(R.string.register_confirmar_password))
                        OutlinedTextField(
                            value = confirmar, onValueChange = { confirmar = it },
                            modifier = Modifier.fillMaxWidth(), singleLine = true,
                            visualTransformation = if (verConfirmar) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            shape = RoundedCornerShape(12.dp), colors = fieldColors,
                            trailingIcon = {
                                IconButton(onClick = { verConfirmar = !verConfirmar }) {
                                    Icon(
                                        if (verConfirmar) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = if (verConfirmar) "Ocultar contraseña" else "Ver contraseña",
                                        tint = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        )
                    }

                    if (uiState is UiState.Error) {
                        Text(stringResource((uiState as UiState.Error).msg),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium)
                    }

                    Spacer(Modifier.height(4.dp))

                    when (uiState) {
                        is UiState.Loading -> Box(Modifier.fillMaxWidth(), Alignment.Center) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                        else -> Button(
                            onClick   = { vm.registrar(nombre, email, password, confirmar, onRegistroExitoso) },
                            modifier  = Modifier.fillMaxWidth().height(52.dp),
                            shape     = RoundedCornerShape(12.dp),
                            colors    = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            elevation = ButtonDefaults.buttonElevation(2.dp)
                        ) {
                            Text(stringResource(R.string.register_btn_crear), fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            TextButton(onClick = onLoginClick) {
                Text(stringResource(R.string.register_ya_cuenta),
                    color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RegisterPreview() { SuperAhorroTheme { RegisterScreen() } }