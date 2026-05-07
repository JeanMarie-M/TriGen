package com.example.trigen.screens.protocol

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trigen.data.repository.ProtocolRepository
import com.example.trigen.data.seeder.ProtocolSeeder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProtocolUiState {
    object Loading : ProtocolUiState()
    data class Success(val protocol: InjuryProtocol) : ProtocolUiState()
    data class Error(val message: String) : ProtocolUiState()
}

@HiltViewModel
class ProtocolViewModel @Inject constructor(
    private val repository: ProtocolRepository,
    private val seeder: ProtocolSeeder
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProtocolUiState>(ProtocolUiState.Loading)
    val uiState: StateFlow<ProtocolUiState> = _uiState.asStateFlow()

    fun loadProtocol(injuryType: String) {
        viewModelScope.launch {
            _uiState.value = ProtocolUiState.Loading
            try {
                seeder.seedIfNeeded()
                val protocol = repository.getProtocol(injuryType)
                _uiState.value = if (protocol != null) {
                    ProtocolUiState.Success(protocol)
                } else {
                    ProtocolUiState.Error("Protocol not found for: $injuryType")
                }
            } catch (e: Exception) {
                _uiState.value = ProtocolUiState.Error(e.message ?: "Failed to load protocol")
            }
        }
    }
}