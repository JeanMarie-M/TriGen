package com.example.trigen.screens.cpr

data class CprUiState(
    val isRunning: Boolean = false,
    val bpm: Int = 110,
    val compressionCount: Int = 0,
    val cycleCount: Int = 0,
    val elapsedTime: String = "0:00"
)