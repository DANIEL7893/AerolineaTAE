package com.dan.walletlogin

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    
    private val _uiState = MutableStateFlow<AuthState>(AuthState.Idle)
    val uiState = _uiState.asStateFlow()

    // login correo
    fun loginWithEmail(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.value = AuthState.Loading
            try {
                val user = auth.signInWithEmailAndPassword(email, pass).await().user
                _uiState.value = AuthState.Success(user?.email ?: "", "Email")
            } catch (e: Exception) {
                _uiState.value = AuthState.Error(e.message ?: "error login")
            }
        }
    }

    // registro correo
    fun registerWithEmail(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.value = AuthState.Loading
            try {
                val user = auth.createUserWithEmailAndPassword(email, pass).await().user
                _uiState.value = AuthState.Success(user?.email ?: "", "Email")
            } catch (e: Exception) {
                _uiState.value = AuthState.Error(e.message ?: "error registro")
            }
        }
    }

    // login google
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
            } catch (e: GetCredentialException) {
                _uiState.value = AuthState.Error(e.message ?: "error google")
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
            val user = auth.signInWithCredential(firebaseCredential).await().user
            _uiState.value = AuthState.Success(user?.email ?: "", "Google")
        } else {
            _uiState.value = AuthState.Error("Tipo: ${credential.type}")
        }
    }

    // cerrar sesion
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
    data class Error(val message: String) : AuthState()
}