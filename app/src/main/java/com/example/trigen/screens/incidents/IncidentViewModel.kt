package com.example.trigen.screens.incidents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class IncidentEntity(
    val id: String = "",
    val date: Long = System.currentTimeMillis(),
    val location: String = "",
    val injuryTypes: String = "",
    val treatmentsGiven: String = "",
    val handoverNotes: String = "",
    val patientCondition: String = "",
    val calledEmergency: Boolean = false,
    val elapsedTimeSeconds: Int = 0,
    val responderNotes: String = "",
    val isSynced: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
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
    }
}

data class IncidentUiState(
    val incidents: List<IncidentEntity> = emptyList(),
    val filteredIncidents: List<IncidentEntity> = emptyList(),
    val selectedIncident: IncidentEntity? = null,
    val selectedIncidentId: String? = null,
    val currentEditIncident: IncidentEntity = IncidentEntity(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val searchQuery: String = "",
    val error: String? = null,
    val saveSuccess: Boolean = false,
    val deleteSuccess: Boolean = false
)

@HiltViewModel
class IncidentViewModel @Inject constructor() : ViewModel() {

    private val dbRef = FirebaseDatabase.getInstance().getReference("Incidents")
    
    private val _state = MutableStateFlow(IncidentUiState())
    val state: StateFlow<IncidentUiState> = _state.asStateFlow()

    init {
        observeIncidents()
    }

    private fun observeIncidents() {
        _state.update { it.copy(isLoading = true) }
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<IncidentEntity>()
                snapshot.children.forEach { child ->
                    child.getValue(IncidentEntity::class.java)?.let { incident ->
                        list.add(incident.copy(id = child.key ?: ""))
                    }
                }
                val sortedList = list.sortedByDescending { it.createdAt }
                _state.update { currentState ->
                    val selected = sortedList.find { it.id == currentState.selectedIncidentId }
                    currentState.copy(
                        incidents = sortedList,
                        filteredIncidents = if (currentState.searchQuery.isEmpty()) sortedList 
                                           else filterList(sortedList, currentState.searchQuery),
                        selectedIncident = selected,
                        // Update currentEditIncident if we are in an edit flow
                        currentEditIncident = if (currentState.selectedIncidentId != null && selected != null) selected 
                                              else currentState.currentEditIncident,
                        isLoading = false 
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _state.update { it.copy(isLoading = false, error = error.message) }
            }
        })
    }

    fun updateSearchQuery(query: String) {
        _state.update { 
            it.copy(
                searchQuery = query,
                filteredIncidents = filterList(it.incidents, query)
            )
        }
    }

    private fun filterList(list: List<IncidentEntity>, query: String): List<IncidentEntity> {
        if (query.isEmpty()) return list
        return list.filter { 
            it.location.contains(query, ignoreCase = true) || 
            it.injuryTypes.contains(query, ignoreCase = true) 
        }
    }

    fun selectIncident(id: String) {
        _state.update { currentState ->
            val incident = currentState.incidents.find { it.id == id }
            currentState.copy(
                selectedIncidentId = id,
                selectedIncident = incident,
                currentEditIncident = incident ?: currentState.currentEditIncident
            )
        }
    }

    fun startNewIncident(elapsedSeconds: Int = 0) {
        _state.update { 
            it.copy(
                selectedIncidentId = null,
                selectedIncident = null,
                currentEditIncident = IncidentEntity(elapsedTimeSeconds = elapsedSeconds),
                saveSuccess = false
            ) 
        }
    }

    fun editExistingIncident(incident: IncidentEntity) {
        _state.update { 
            it.copy(
                currentEditIncident = incident,
                saveSuccess = false
            ) 
        }
    }

    fun updateEditState(update: (IncidentEntity) -> IncidentEntity) {
        _state.update { it.copy(currentEditIncident = update(it.currentEditIncident)) }
    }

    fun saveIncident() {
        val incident = _state.value.currentEditIncident
        _state.update { it.copy(isSaving = true, error = null) }

        val targetRef = if (incident.id.isEmpty()) {
            dbRef.push()
        } else {
            dbRef.child(incident.id)
        }

        val finalIncident = if (incident.id.isEmpty()) {
            incident.copy(id = targetRef.key ?: "")
        } else {
            incident
        }

        targetRef.setValue(finalIncident.toMap())
            .addOnSuccessListener {
                _state.update { it.copy(isSaving = false, saveSuccess = true) }
            }
            .addOnFailureListener { e ->
                _state.update { it.copy(isSaving = false, error = e.message) }
            }
    }

    fun deleteIncident(id: String) {
        dbRef.child(id).removeValue()
            .addOnSuccessListener {
                _state.update { it.copy(deleteSuccess = true) }
            }
            .addOnFailureListener { e ->
                _state.update { it.copy(error = e.message) }
            }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
    
    fun resetSaveSuccess() {
        _state.update { it.copy(saveSuccess = false) }
    }

    fun resetDeleteSuccess() {
        _state.update { it.copy(deleteSuccess = false) }
    }
}
