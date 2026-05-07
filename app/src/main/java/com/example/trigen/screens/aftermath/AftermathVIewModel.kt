package com.example.trigen.screens.aftermath

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.trigen.data.local.entity.IncidentEntity
import com.example.trigen.data.repository.IncidentRepository
import com.example.trigen.navigation.Routes
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class AftermathUiState(
    val incident: IncidentEntity? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
    val isEditing: Boolean = true
)

@HiltViewModel
class AftermathViewModel @Inject constructor(
    private val repository: IncidentRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AftermathUiState())
    val state: StateFlow<AftermathUiState> = _state.asStateFlow()

    init {
        _state.update { it.copy(incident = repository.createNewIncident()) }
    }

    fun loadIncident(id: String) {
        if (id == "new") {
            _state.update { it.copy(incident = repository.createNewIncident(), isEditing = true) }
            return
        }
        viewModelScope.launch {
            val incident = repository.getIncidentById(id)
            _state.update { it.copy(incident = incident, isEditing = false) }
        }
    }

    fun updateLocation(value: String) {
        _state.update { it.copy(incident = it.incident?.copy(location = value)) }
    }

    fun updateInjuryTypes(value: String) {
        _state.update { it.copy(incident = it.incident?.copy(injuryTypes = value)) }
    }

    fun updateTreatments(value: String) {
        _state.update { it.copy(incident = it.incident?.copy(treatmentsGiven = value)) }
    }

    fun updateHandoverNotes(value: String) {
        _state.update { it.copy(incident = it.incident?.copy(handoverNotes = value)) }
    }

    fun updatePatientCondition(value: String) {
        _state.update { it.copy(incident = it.incident?.copy(patientCondition = value)) }
    }

    fun updateResponderNotes(value: String) {
        _state.update { it.copy(incident = it.incident?.copy(responderNotes = value)) }
    }

    fun updateCalledEmergency(value: Boolean) {
        _state.update { it.copy(incident = it.incident?.copy(calledEmergency = value)) }
    }

    fun updateElapsedTime(seconds: Int) {
        _state.update { it.copy(incident = it.incident?.copy(elapsedTimeSeconds = seconds)) }
    }

    fun saveIncident() {
        val incident = _state.value.incident ?: return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            try {
                repository.saveIncident(incident)
                _state.update { it.copy(isSaving = false, isSaved = true, isEditing = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }

    fun uploadIncident(
        date: Long,
        location: String,
        injuryTypes: String,
        treatmentsGiven: String,
        handoverNotes: String,
        patientCondition: String,
        calledEmergency: Boolean,
        elapsedTimeSeconds: Int,
        responderNotes: String,
        isSynced: Boolean = false,
        createdAt: Long = System.currentTimeMillis()
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val ref = FirebaseDatabase.getInstance().getReference("Incidents").push()
                val incidentData = mapOf(
                    "id" to ref.key,
                    "date" to date,
                    "location" to location,
                    "injuryTypes" to injuryTypes,
                    "treatmentsGiven" to treatmentsGiven,
                    "handoverNotes" to handoverNotes,
                    "patientCondition" to patientCondition,
                    "calledEmergency" to calledEmergency,
                    "elapsedTimeSeconds" to elapsedTimeSeconds,
                    "responderNotes" to responderNotes,
                    "isSynced" to isSynced,
                    "createdAt" to createdAt
                )
                ref.setValue(incidentData).await()
                withContext(Dispatchers.Main) {
                    _state.update { it.copy(isSaving = false, isSaved = true, isEditing = false) }
                    onNavigateToHome()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _state.update { it.copy(isSaving = false, error = e.message) }
                }
            }
        }
    }

    private fun onNavigateToHome() {
        Routes.HOME
    }

    fun deleteIncident() {
        val incident = _state.value.incident ?: return
        viewModelScope.launch {
            try {
                repository.deleteIncident(incident)
                _state.update { it.copy(incident = null) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun startEditing() {
        _state.update { it.copy(isEditing = true) }
    }

    fun dismissError() {
        _state.update { it.copy(error = null) }
    }
}