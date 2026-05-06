package com.dan.walletlogin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.graphics.Brush

@Composable
fun LoginScreen(viewModel: AuthViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    when (val state = uiState) {
        is AuthState.Success -> {
            WelcomeScreen(
                email = state.email,
                provider = state.provider
            ) {
                viewModel.logout(context)
            }
        }
        else -> {
            AuthContent(
                uiState = uiState,
                onLoginEmail = { email, pass -> viewModel.loginWithEmail(email, pass) },
                onRegisterEmail = { email, pass -> viewModel.registerWithEmail(email, pass) },
                onLoginGoogle = { clientId -> viewModel.loginWithGoogle(context, clientId) }
            )
        }
    }
}

@Composable
fun AuthContent(
    uiState: AuthState,
    onLoginEmail: (String, String) -> Unit,
    onRegisterEmail: (String, String) -> Unit,
    onLoginGoogle: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegister by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }
    
    val webClientId = stringResource(R.string.web_client)

    val isPasswordValid = remember(password) {
        (password.length >= 8) && 
        password.any { it.isDigit() } && 
        password.any { !it.isLetterOrDigit() }
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(Color.White, Color(0xFF003366))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradient)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.drawable.logoaerolinea),
            contentDescription = "Logo Aerolineas TAE",
            modifier = Modifier.size(180.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // titulo
        Text(
            text = if (isRegister) "Crear Cuenta" else "Bienvenido a Aerolineas TAE",
            fontSize = 32.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF003366),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // campo email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico", color = Color(0xFF000000)) },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // campo password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña", color=Color(0xFF000000)) },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black
            ),
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null
                    )

                }

            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            isError = !isPasswordValid && password.isNotEmpty(),
            supportingText = {
                if (!isPasswordValid && password.isNotEmpty()) {
                    Text("Min 8 caracteres, 1 número y 1 especial")
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // boton principal
        Button(
            onClick = {
                if (isPasswordValid) {
                    if (isRegister) onRegisterEmail(email, password)
                    else onLoginEmail(email, password)
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = email.isNotEmpty() && isPasswordValid && uiState !is AuthState.Loading
        ) {
            if (uiState is AuthState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text(if (isRegister) "Registrarse" else "Iniciar Sesión", color = Color(0xFF313131))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // boton google
        OutlinedButton(
            onClick = { onLoginGoogle(webClientId) },
            border = BorderStroke(2.dp, Color(0xFF000000)),
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = uiState !is AuthState.Loading
        ) {
            Text(
                text = "Continuar con Google",
                color = Color(0xFF000000)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // toggle registro
        TextButton(onClick = { isRegister = !isRegister }) {
            Text(if (isRegister) "¿Ya tienes cuenta? Entra" else "¿No tienes cuenta? Regístrate", color = Color(0xFF000000))
        }

        if (uiState is AuthState.Error) {
            Text(
                text = uiState.message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun WelcomeScreen(email: String, provider: String, onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "¡ Bienvenido !",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Has iniciado sesión con:",
            fontSize = 16.sp,
            color = Color.Gray
        )
        
        Text(
            text = email,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = "Proveedor: $provider",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Cerrar Sesión")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuthContentPreview() {
    MaterialTheme {
        AuthContent(
            uiState = AuthState.Idle,
            onLoginEmail = { _, _ -> },
            onRegisterEmail = { _, _ -> },
            onLoginGoogle = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    MaterialTheme {
        WelcomeScreen(
            email = "usuario@example.com",
            provider = "Google",
            onLogout = { }
        )
    }
}