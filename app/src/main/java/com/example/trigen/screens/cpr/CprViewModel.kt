package com.example.trigen.screens.cpr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trigen.triage.cpr.MetronomeEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CprViewModel @Inject constructor(
    private val metronome: MetronomeEngine
) : ViewModel() {

    private val _uiState = MutableStateFlow(CprUiState())
    val uiState: StateFlow<CprUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var elapsedSeconds = 0

    fun start() {
        _uiState.update { it.copy(isRunning = true) }
        startTimer()
        metronome.start(_uiState.value.bpm) {
            _uiState.update { state ->
                val newCount = state.compressionCount + 1
                val newCycle = if (newCount % 30 == 0) state.cycleCount + 1
                else state.cycleCount
                state.copy(
                    compressionCount = newCount,
                    cycleCount = newCycle
                )
            }
        }
    }

    fun stop() {
        metronome.stop()
        timerJob?.cancel()
        _uiState.update { it.copy(isRunning = false) }
    }

    fun setBpm(bpm: Int) {
        _uiState.update { it.copy(bpm = bpm) }
        if (_uiState.value.isRunning) {
            metronome.updateBpm(bpm) {
                _uiState.update { state ->
                    val newCount = state.compressionCount + 1
                    val newCycle = if (newCount % 30 == 0) state.cycleCount + 1
                    else state.cycleCount
                    state.copy(
                        compressionCount = newCount,
                        cycleCount = newCycle
                    )
                }
            }
        }
    }

    private fun startTimer() {
        elapsedSeconds = 0
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                elapsedSeconds++
                val minutes = elapsedSeconds / 60
                val seconds = elapsedSeconds % 60
                _uiState.update { it.copy(elapsedTime = "$minutes:${seconds.toString().padStart(2, '0')}") }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        metronome.stop()
        timerJob?.cancel()
    }
}