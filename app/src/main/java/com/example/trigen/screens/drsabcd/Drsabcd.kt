package com.example.trigen.screens.drsabcd

enum class DrsabcdStep {
    DANGER,
    RESPONSE,
    SEND_HELP,
    AIRWAY,
    BREATHING,
    CPR,
    DEFIBRILLATION
}

enum class ResponseLevel {
    RESPONSIVE,
    UNRESPONSIVE
}

enum class BreathingStatus {
    NORMAL,
    NOT_BREATHING,
    ABNORMAL
}

data class DrsabcdState(
    val currentStep: DrsabcdStep = DrsabcdStep.DANGER,
    val isSceneSafe: Boolean? = null,
    val responseLevel: ResponseLevel? = null,
    val breathingStatus: BreathingStatus? = null,
    val helpCalled: Boolean = false,
    val elapsedSeconds: Int = 0,
    val stepStartSeconds: Int = 0
)