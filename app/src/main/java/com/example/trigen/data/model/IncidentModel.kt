package com.example.trigen.data.model

data class IncidentModel(
    val id: String = "",
    val date: Long,
    val location: String = "",
    val injuryTypes: String  = "",
    val treatmentsGiven: String = "",
    val handoverNotes: String = "",
    val patientCondition: String = "",
    val calledEmergency: Boolean,
    val elapsedTimeSeconds: Int,
    val responderNotes: String = "",
    val isSynced: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
