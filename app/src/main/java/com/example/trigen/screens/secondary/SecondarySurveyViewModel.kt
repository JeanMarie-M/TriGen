package com.example.trigen.screens.secondary

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SecondarySurveyViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(SecondarySurveyState())
    val state: StateFlow<SecondarySurveyState> = _state.asStateFlow()

    fun toggleSymptom(symptom: String) {
        _state.update { state ->
            val current = state.samData.symptoms.toMutableList()
            if (current.contains(symptom)) current.remove(symptom)
            else current.add(symptom)
            state.copy(samData = state.samData.copy(symptoms = current))
        }
    }

    fun toggleAllergy(allergy: String) {
        _state.update { state ->
            val current = state.samData.allergies.toMutableList()
            if (current.contains(allergy)) current.remove(allergy)
            else current.add(allergy)
            state.copy(samData = state.samData.copy(allergies = current))
        }
    }

    fun toggleCondition(condition: String) {
        _state.update { state ->
            val current = state.samData.medicalConditions.toMutableList()
            if (current.contains(condition)) current.remove(condition)
            else current.add(condition)
            state.copy(samData = state.samData.copy(medicalConditions = current))
        }
    }

    fun toggleInjury(area: String) {
        _state.update { state ->
            val current = state.injuriesFound.toMutableList()
            if (current.contains(area)) current.remove(area)
            else current.add(area)
            state.copy(injuriesFound = current)
        }
    }

    fun updateCustomSymptom(value: String) {
        _state.update { it.copy(customSymptom = value) }
    }

    fun addCustomSymptom() {
        val symptom = _state.value.customSymptom.trim()
        if (symptom.isBlank()) return
        _state.update { state ->
            state.copy(
                samData = state.samData.copy(
                    symptoms = state.samData.symptoms + symptom
                ),
                customSymptom = ""
            )
        }
    }

    fun updateCustomAllergy(value: String) {
        _state.update { it.copy(customAllergy = value) }
    }

    fun addCustomAllergy() {
        val allergy = _state.value.customAllergy.trim()
        if (allergy.isBlank()) return
        _state.update { state ->
            state.copy(
                samData = state.samData.copy(
                    allergies = state.samData.allergies + allergy
                ),
                customAllergy = ""
            )
        }
    }

    fun updateCustomMedication(value: String) {
        _state.update { it.copy(customMedication = value) }
    }

    fun addCustomMedication() {
        val medication = _state.value.customMedication.trim()
        if (medication.isBlank()) return
        _state.update { state ->
            state.copy(
                samData = state.samData.copy(
                    medications = state.samData.medications + medication
                ),
                customMedication = ""
            )
        }
    }

    fun updatePulseRate(value: String) {
        _state.update { it.copy(vitalSigns = it.vitalSigns.copy(pulseRate = value)) }
    }

    fun updateBreathingRate(value: String) {
        _state.update { it.copy(vitalSigns = it.vitalSigns.copy(breathingRate = value)) }
    }

    fun updateConsciousness(level: ConsciousnessLevel) {
        _state.update { it.copy(vitalSigns = it.vitalSigns.copy(consciousnessLevel = level)) }
    }

    fun setRecoveryPosition(needed: Boolean) {
        _state.update { it.copy(recoveryPositionNeeded = needed) }
    }

    fun navigateTo(section: SurveySection) {
        _state.update { it.copy(currentSection = section) }
    }

    fun goBack() {
        val prev = when (_state.value.currentSection) {
            SurveySection.HEAD_TO_TOE -> SurveySection.SAM
            SurveySection.VITALS -> SurveySection.HEAD_TO_TOE
            SurveySection.RECOVERY_POSITION -> SurveySection.VITALS
            else -> return
        }
        _state.update { it.copy(currentSection = prev) }
    }
}