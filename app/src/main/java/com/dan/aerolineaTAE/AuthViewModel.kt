package com.dan.aerolineaTAE

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dan.aerolineaTAE.data.Usuario
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    
    private val _uiState = MutableStateFlow<AuthState>(AuthState.Idle)
    val uiState = _uiState.asStateFlow()

    init {
        auth.currentUser?.let { user ->
            checkUserOnboarding(user)
        }
    }

    private fun checkUserOnboarding(user: FirebaseUser) {
        _uiState.value = AuthState.Loading
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(user.uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val usuario = document.toObject(Usuario::class.java)
                    if (usuario?.mfaEnabled == true) {
                        _uiState.value = AuthState.RequiresMfa(user.email ?: "", usuario.mfaType, usuario.pin)
                    } else {
                        _uiState.value = AuthState.Success(user.email ?: "", "Login")
                    }
                } else {
                    _uiState.value = AuthState.RequiresOnboarding(user.email ?: "")
                }
            }
            .addOnFailureListener { e ->
                _uiState.value = AuthState.Error("Error de conexión: ${e.message}")
            }
    }

    fun loginWithEmail(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.value = AuthState.Loading
            try {
                val user = auth.signInWithEmailAndPassword(email, pass).await().user
                if (user != null) checkUserOnboarding(user)
            } catch (e: Exception) {
                _uiState.value = AuthState.Error(e.message ?: "error login")
            }
        }
    }

    fun registerWithEmail(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.value = AuthState.Loading
            try {
                val user = auth.createUserWithEmailAndPassword(email, pass).await().user
                if (user != null) _uiState.value = AuthState.RequiresOnboarding(user.email ?: "")
            } catch (e: Exception) {
                _uiState.value = AuthState.Error(e.message ?: "error registro")
            }
        }
    }

    fun loginWithGoogle(context: Context, webClientId: String) {
        viewModelScope.launch {
            _uiState.value = AuthState.Loading
            val credentialManager = CredentialManager.create(context)
            
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts = false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(autoSelectEnabled = true)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            try {
                val result = credentialManager.getCredential(context, request)
                handleGoogleCredential(result)
            } catch (e: Exception) {
                _uiState.value = AuthState.Error(e.message ?: "error google")
            }
        }
    }

    private suspend fun handleGoogleCredential(result: GetCredentialResponse) {
        val credential = result.credential
        if (credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
            try {
                val user = auth.signInWithCredential(firebaseCredential).await().user
                if (user != null) checkUserOnboarding(user)
            } catch (e: Exception) {
                _uiState.value = AuthState.Error(e.message ?: "error google")
            }
        } else {
            _uiState.value = AuthState.Error("Tipo: ${credential.type}")
        }
    }

    fun verifyMfa(success: Boolean, email: String) {
        if (success) {
            _uiState.value = AuthState.Success(email, "MfaVerified")
        } else {
            _uiState.value = AuthState.Error("Autenticación fallida")
        }
    }

    fun completeOnboarding(email: String) {
        _uiState.value = AuthState.Success(email, "OnboardingComplete")
    }

    fun logout(context: Context) {
        auth.signOut()
        viewModelScope.launch {
            val credentialManager = CredentialManager.create(context)
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            _uiState.value = AuthState.Idle
        }
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val email: String, val provider: String) : AuthState()
    data class RequiresOnboarding(val email: String) : AuthState()
    data class RequiresMfa(val email: String, val type: String, val pinCorrecto: String) : AuthState()
    data class Error(val message: String) : AuthState()
}
