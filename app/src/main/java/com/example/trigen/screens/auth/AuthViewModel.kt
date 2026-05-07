package com.example.trigen.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trigen.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val user: FirebaseUser? = null,
    val displayName: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.authStateFlow().collect { user ->
                _uiState.update { it.copy(
                    user = user,
                    displayName = user?.displayName
                ) }
            }
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, isSuccess = false) }
            val result = repository.login(email, pass)
            result.fold(
                onSuccess = { user ->
                    _uiState.update { it.copy(user = user, isLoading = false, isSuccess = true) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(error = e.localizedMessage, isLoading = false) }
                }
            )
        }
    }

    fun signUp(email: String, pass: String, displayName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, isSuccess = false) }
            val result = repository.signUp(email, pass, displayName)
            result.fold(
                onSuccess = { user ->
                    _uiState.update { it.copy(user = user, isLoading = false, isSuccess = true) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(error = e.localizedMessage, isLoading = false) }
                }
            )
        }
    }

    fun logout() {
        repository.logout()
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
