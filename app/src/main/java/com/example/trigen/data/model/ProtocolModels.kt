package com.example.trigen.data.model

data class ProtocolJson(
    val injuryType: String,
    val displayName: String,
    val severity: String,
    val source: String,
    val callEmergency: Boolean,
    val steps: List<StepJson>,
    val doNot: List<String>
)

data class StepJson(
    val stepNumber: Int,
    val title: String,
    val instruction: String,
    val warning: String?
)