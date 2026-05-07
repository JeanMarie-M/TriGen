package com.example.trigen.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trigen.data.preferences.UserPreferences
import com.example.trigen.data.repository.AuthRepository
import com.example.trigen.data.repository.AcademyRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: FirebaseUser? = null,
    val displayName: String? = null,
    val isDarkMode: Boolean? = null,
    val earnedBadges: Int = 0
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val academyRepository: AcademyRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                authRepository.authStateFlow(),
                userPreferences.darkModeFlow,
                academyRepository.getEarnedBadgeCountFlow()
            ) { user, isDarkMode, badgeCount ->
                ProfileUiState(
                    user = user,
                    displayName = user?.displayName,
                    isDarkMode = isDarkMode,
                    earnedBadges = badgeCount
                )
            }.collect {
                _uiState.value = it
            }
        }
    }

    fun toggleDarkMode(isDark: Boolean?) {
        viewModelScope.launch {
            userPreferences.setDarkMode(isDark)
        }
    }

    fun logout() {
        authRepository.logout()
    }

    fun updateDisplayName(newName: String) {
        viewModelScope.launch {
            authRepository.updateDisplayName(newName)
        }
    }

    fun deleteAccount(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = authRepository.deleteAccount()
            if (result.isSuccess) {
                onSuccess()
            }
        }
    }
}
