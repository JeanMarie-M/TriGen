package com.example.trigen.screens.secondary

enum class ConsciousnessLevel {
    ALERT,
    VOICE,
    PAIN,
    UNRESPONSIVE
}

data class VitalSigns(
    val pulseRate: String = "",
    val breathingRate: String = "",
    val consciousnessLevel: ConsciousnessLevel? = null
)

data class SamData(
    val symptoms: List<String> = emptyList(),
    val allergies: List<String> = emptyList(),
    val medications: List<String> = emptyList(),
    val medicalConditions: List<String> = emptyList()
)

data class SecondarySurveyState(
    val currentSection: SurveySection = SurveySection.SAM,
    val samData: SamData = SamData(),
    val vitalSigns: VitalSigns = VitalSigns(),
    val injuriesFound: List<String> = emptyList(),
    val recoveryPositionNeeded: Boolean = false,
    val customSymptom: String = "",
    val customAllergy: String = "",
    val customMedication: String = ""
)

enum class SurveySection {
    SAM,
    HEAD_TO_TOE,
    VITALS,
    RECOVERY_POSITION
}

val COMMON_SYMPTOMS = listOf(
    "Chest pain", "Difficulty breathing", "Headache",
    "Dizziness", "Nausea", "Bleeding", "Swelling",
    "Numbness", "Confusion", "Fever", "Abdominal pain"
)

val COMMON_ALLERGIES = listOf(
    "Penicillin", "Aspirin", "Ibuprofen", "Latex",
    "Peanuts", "Shellfish", "Bee stings", "None known"
)

val COMMON_CONDITIONS = listOf(
    "Diabetes", "Hypertension", "Asthma", "Heart disease",
    "Epilepsy", "Blood clotting disorder", "Pregnancy", "None known"
)

val BODY_AREAS = listOf(
    "Head / Skull", "Face", "Neck / Spine",
    "Chest", "Abdomen", "Pelvis",
    "Left Arm", "Right Arm",
    "Left Leg", "Right Leg"
)