package com.example.trigen.triage.classifier

enum class InjuryLabel(val displayName: String, val description: String) {
    BURN("Burn", "Thermal, chemical or electrical burn injury"),
    FRACTURE("Fracture", "Suspected bone fracture or break"),
    LACERATION("Laceration", "Deep cut or tear in skin"),
    BITE("Bite", "Animal or insect bite"),
    UNKNOWN("Unknown", "Injury type could not be determined")
}
