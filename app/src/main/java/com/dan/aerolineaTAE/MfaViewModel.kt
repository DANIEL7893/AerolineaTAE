package com.dan.aerolineaTAE

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.MultiFactorResolver
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MfaViewModel : ViewModel() {
    
    // Estado de la interfaz de usuario para MFA
    private val _uiState = MutableStateFlow<MfaState>(MfaState.Idle)
    val uiState = _uiState.asStateFlow()

    // ID de verificación recibido tras enviar el SMS
    private val _verificationId = MutableStateFlow<String?>(null)
    val verificationId = _verificationId.asStateFlow()

    // Token para reenvío de SMS si es necesario
    private val _resendToken = MutableStateFlow<PhoneAuthProvider.ForceResendingToken?>(null)
    val resendToken = _resendToken.asStateFlow()

    // Resólver de Firebase para manejar el flujo de segundo factor durante el login
    private val _resolver = MutableStateFlow<MultiFactorResolver?>(null)
    val resolver = _resolver.asStateFlow()

    fun setResolver(resolver: MultiFactorResolver) {
        _resolver.value = resolver
    }

    fun setVerificationInfo(id: String, token: PhoneAuthProvider.ForceResendingToken?) {
        _verificationId.value = id
        _resendToken.value = token
    }

    fun setLoading() {
        _uiState.value = MfaState.Loading
    }

    fun setError(message: String) {
        _uiState.value = MfaState.Error(message)
    }

    fun setSuccess() {
        _uiState.value = MfaState.Success
    }

    fun reset() {
        _uiState.value = MfaState.Idle
        _verificationId.value = null
        _resendToken.value = null
        _resolver.value = null
    }
}

sealed class MfaState {
    object Idle : MfaState()
    object Loading : MfaState()
    object Success : MfaState()
    data class Error(val message: String) : MfaState()
}
