package com.example.gyme.feature.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gyme.data.repository.AuthRepositoryImpl
import com.example.gyme.domain.repository.AuthRepository
import com.example.gyme.util.ApiResult
import com.example.gyme.util.SessionManager
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var isPasswordVisible by mutableStateOf(false)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun onEmailChange(value: String) {
        email = value
        errorMessage = null
    }

    fun onPasswordChange(value: String) {
        password = value
        errorMessage = null
    }

    fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
    }

    fun signIn(onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Please enter your email and password."
            return
        }
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            when (val result = authRepository.signIn(email.trim(), password)) {
                is ApiResult.Success -> {
                    SessionManager.currentUser = result.data
                    onSuccess()
                }
                is ApiResult.Error -> {
                    errorMessage = when {
                        result.code == 400 -> "Invalid email or password."
                        result.code == 422 -> "Email or password format is invalid."
                        else               -> "Sign in failed: ${result.message}"
                    }
                }
            }
            isLoading = false
        }
    }
}
