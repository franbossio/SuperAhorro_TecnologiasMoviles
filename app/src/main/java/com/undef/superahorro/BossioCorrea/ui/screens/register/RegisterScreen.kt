package com.undef.superahorro.BossioCorrea.ui.screens.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.undef.superahorro.BossioCorrea.R
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

    var nombre          by remember { mutableStateOf("") }
    var apellido        by remember { mutableStateOf("") }
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var confirmar       by remember { mutableStateOf("") }
    var verPassword     by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.register_titulo)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.volver))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text       = stringResource(R.string.register_titulo),
                style      = MaterialTheme.typography.headlineMedium,
                color      = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier            = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value         = nombre,
                    onValueChange = { nombre = it },
                    label         = { Text(stringResource(R.string.register_nombre)) },
                    modifier      = Modifier.weight(1f),
                    singleLine    = true,
                    shape         = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value         = apellido,
                    onValueChange = { apellido = it },
                    label         = { Text(stringResource(R.string.register_apellido)) },
                    modifier      = Modifier.weight(1f),
                    singleLine    = true,
                    shape         = RoundedCornerShape(12.dp)
                )
            }

            OutlinedTextField(
                value         = email,
                onValueChange = { email = it },
                label         = { Text(stringResource(R.string.register_email)) },
                modifier      = Modifier.fillMaxWidth(),
                singleLine    = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape         = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value         = password,
                onValueChange = { password = it },
                label         = { Text(stringResource(R.string.register_password)) },
                modifier      = Modifier.fillMaxWidth(),
                singleLine    = true,
                visualTransformation = if (verPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape         = RoundedCornerShape(12.dp),
                trailingIcon  = {
                    IconButton(onClick = { verPassword = !verPassword }) {
                        Icon(
                            if (verPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null
                        )
                    }
                }
            )

            OutlinedTextField(
                value         = confirmar,
                onValueChange = { confirmar = it },
                label         = { Text(stringResource(R.string.register_confirmar_password)) },
                modifier      = Modifier.fillMaxWidth(),
                singleLine    = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape         = RoundedCornerShape(12.dp)
            )

            if (uiState is UiState.Error) {
                Text(
                    text  = (uiState as UiState.Error).msg,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            when (uiState) {
                is UiState.Loading -> CircularProgressIndicator()
                else -> {
                    Button(
                        onClick  = { vm.registrar(nombre, email, password, confirmar, onRegistroExitoso) },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape    = RoundedCornerShape(12.dp)
                    ) {
                        Text(stringResource(R.string.register_btn_crear), fontWeight = FontWeight.Bold)
                    }
                }
            }

            TextButton(onClick = onLoginClick) {
                Text(
                    text  = stringResource(R.string.register_ya_cuenta),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RegisterPreview() {
    SuperAhorroTheme { RegisterScreen() }
}