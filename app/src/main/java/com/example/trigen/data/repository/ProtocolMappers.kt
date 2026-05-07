package com.example.trigen.data.repository

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

fun getSeverityColor(severity: String): Color = when (severity.lowercase()) {
    "serious"  -> Color(0xFFE63946)
    "moderate" -> Color(0xFFEF9F27)
    "mild"     -> Color(0xFF457B9D)
    "variable" -> Color(0xFF1D9E75)
    else       -> Color(0xFF64748B)
}

fun getIconForInjury(injuryType: String): ImageVector = when (injuryType.uppercase()) {
    "BURN"            -> Icons.Default.LocalFireDepartment
    "FRACTURE"        -> Icons.Default.MedicalServices
    "LACERATION"      -> Icons.Default.Healing
    "BITE"            -> Icons.Default.BugReport
    "SWELLING"        -> Icons.Default.Accessibility
    "ABRASION"        -> Icons.Default.Healing
    "BRUISE"          -> Icons.Default.Healing
    "PRESSURE_WOUND"  -> Icons.Default.MedicalServices
    "DIABETIC_WOUND"  -> Icons.Default.MedicalServices
    "SURGICAL_WOUND"  -> Icons.Default.MedicalServices
    "INFECTION"       -> Icons.Default.Warning
    "RASH"            -> Icons.Default.Warning
    "FUNGAL"          -> Icons.Default.BugReport
    else              -> Icons.Default.MedicalServices
}