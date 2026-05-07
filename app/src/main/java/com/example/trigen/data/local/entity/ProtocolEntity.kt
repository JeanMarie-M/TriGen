package com.example.trigen.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "protocols")
data class ProtocolEntity(
    @PrimaryKey
    val injuryType: String,
    val displayName: String,
    val severity: String,
    val source: String,
    val callEmergency: Boolean,
    val stepsJson: String,      // JSON array of steps
    val doNotJson: String       // JSON array of do-not items
)