package com.example.trigen.triage.classifier

data class ClassificationResult(
    val label: InjuryLabel,
    val confidence: Float,
    val isReliable: Boolean = confidence >= CONFIDENCE_THRESHOLD
) {
    companion object {
        const val CONFIDENCE_THRESHOLD = 0.80f
    }
}
