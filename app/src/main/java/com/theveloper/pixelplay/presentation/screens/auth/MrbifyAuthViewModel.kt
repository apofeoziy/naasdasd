package com.theveloper.pixelplay.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theveloper.pixelplay.data.preferences.MrbifyAuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MrbifyAuthUiState(
    val emailInput: String = "",
    val passwordInput: String = "",
    val isLoggingIn: Boolean = false,
    val loginError: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class MrbifyAuthViewModel @Inject constructor(
    private val mrbifyAuthManager: MrbifyAuthManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MrbifyAuthUiState())
    val uiState: StateFlow<MrbifyAuthUiState> = _uiState.asStateFlow()

    fun updateEmail(email: String) {
        _uiState.update { it.copy(emailInput = email, loginError = null) }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(passwordInput = password, loginError = null) }
    }

    fun login() {
        val email = _uiState.value.emailInput.trim()
        val password = _uiState.value.passwordInput

        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(loginError = "Email and password must not be empty.") }
            return
        }

        _uiState.update { it.copy(isLoggingIn = true, loginError = null) }

        viewModelScope.launch {
            val request = com.theveloper.pixelplay.data.network.mrbify.MrbifyLoginRequest(email, password)
            val result = mrbifyAuthManager.login(request)
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoggingIn = false, isSuccess = true) }
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Unknown login error"
                _uiState.update { it.copy(isLoggingIn = false, loginError = errorMsg) }
            }
        }
    }
}
