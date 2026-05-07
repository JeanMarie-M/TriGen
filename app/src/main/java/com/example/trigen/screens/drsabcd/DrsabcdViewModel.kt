package com.example.trigen.screens.drsabcd

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trigen.navigation.Routes
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
class DrsabcdViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(DrsabcdState())
    val state: StateFlow<DrsabcdState> = _state.asStateFlow()

    private var timerJob: Job? = null

    init {
        startTimer()
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _state.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
            }
        }
    }

    fun markSceneSafe(safe: Boolean) {
        _state.update { it.copy(isSceneSafe = safe) }
        if (safe) advanceTo(DrsabcdStep.RESPONSE)
    }

    fun markResponse(level: ResponseLevel) {
        _state.update { it.copy(responseLevel = level) }
        advanceTo(DrsabcdStep.SEND_HELP)
    }

    fun markHelpCalled() {
        _state.update { it.copy(helpCalled = true) }
        advanceTo(DrsabcdStep.AIRWAY)
    }

    fun markAirwayClear() {
        advanceTo(DrsabcdStep.BREATHING)
    }

    fun markBreathing(status: BreathingStatus) {
        _state.update { it.copy(breathingStatus = status) }
        when (status) {
            BreathingStatus.NORMAL -> advanceTo(DrsabcdStep.DEFIBRILLATION)
            BreathingStatus.NOT_BREATHING,
            BreathingStatus.ABNORMAL -> advanceTo(DrsabcdStep.CPR)
        }
    }

    fun markCprStarted() {
        advanceTo(DrsabcdStep.DEFIBRILLATION)
    }

    fun advanceTo(step: DrsabcdStep) {
        _state.update { it.copy(
            currentStep = step,
            stepStartSeconds = it.elapsedSeconds
        )}
    }

    fun goBack() {
        val prevStep = when (_state.value.currentStep) {
            DrsabcdStep.RESPONSE -> DrsabcdStep.DANGER
            DrsabcdStep.SEND_HELP -> DrsabcdStep.RESPONSE
            DrsabcdStep.AIRWAY -> DrsabcdStep.SEND_HELP
            DrsabcdStep.BREATHING -> DrsabcdStep.AIRWAY
            DrsabcdStep.CPR -> DrsabcdStep.BREATHING
            DrsabcdStep.DEFIBRILLATION -> DrsabcdStep.CPR
            else -> return
        }
        _state.update { it.copy(currentStep = prevStep) }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
