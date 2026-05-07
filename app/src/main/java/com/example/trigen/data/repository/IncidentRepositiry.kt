package com.example.trigen.data.repository

import com.example.trigen.data.local.dao.IncidentDao
import com.example.trigen.data.local.entity.IncidentEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IncidentRepository @Inject constructor(
    private val incidentDao: IncidentDao,
    private val firestore: FirebaseFirestore
) {
    fun getAllIncidents(): Flow<List<IncidentEntity>> =
        incidentDao.getAllIncidents()

    suspend fun saveIncident(incident: IncidentEntity) {
        incidentDao.insert(incident)
        syncToFirebase(incident)
    }

    suspend fun updateIncident(incident: IncidentEntity) {
        incidentDao.update(incident)
        syncToFirebase(incident)
    }

    suspend fun deleteIncident(incident: IncidentEntity) {
        incidentDao.delete(incident)
        try {
            firestore.collection("incidents")
                .document(incident.id)
                .delete()
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getIncidentById(id: String): IncidentEntity? =
        incidentDao.getIncidentById(id)

    private suspend fun syncToFirebase(incident: IncidentEntity) {
        try {
            val data = hashMapOf(
                "id" to incident.id,
                "date" to incident.date,
                "location" to incident.location,
                "injuryTypes" to incident.injuryTypes,
                "treatmentsGiven" to incident.treatmentsGiven,
                "handoverNotes" to incident.handoverNotes,
                "patientCondition" to incident.patientCondition,
                "calledEmergency" to incident.calledEmergency,
                "elapsedTimeSeconds" to incident.elapsedTimeSeconds,
                "responderNotes" to incident.responderNotes,
                "createdAt" to incident.createdAt
            )
            firestore.collection("incidents")
                .document(incident.id)
                .set(data)
                .await()
            incidentDao.markAsSynced(incident.id)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun createNewIncident(): IncidentEntity = IncidentEntity(
        id = UUID.randomUUID().toString(),
        date = System.currentTimeMillis(),
        location = "",
        injuryTypes = "",
        treatmentsGiven = "",
        handoverNotes = "",
        patientCondition = "",
        calledEmergency = false,
        elapsedTimeSeconds = 0,
        responderNotes = ""
    )
}