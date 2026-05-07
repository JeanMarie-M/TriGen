package com.example.trigen.screens.academy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trigen.data.local.entity.BadgeEntity
import com.example.trigen.data.repository.AcademyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BadgeUiState(
    val badge: BadgeEntity? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class BadgeViewModel @Inject constructor(
    private val repository: AcademyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BadgeUiState())
    val uiState: StateFlow<BadgeUiState> = _uiState.asStateFlow()

    fun loadBadge(moduleId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val badge = repository.getBadgeForModule(moduleId)
            _uiState.update { it.copy(badge = badge, isLoading = false) }
        }
    }
}
