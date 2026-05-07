package com.example.trigen.screens.scan

import com.example.trigen.triage.classifier.ClassificationResult

sealed class ScanUiState {
    object Idle : ScanUiState()
    object RequestingPermission : ScanUiState()
    object PermissionDenied : ScanUiState()
    object Scanning : ScanUiState()
    data class Result(val result: ClassificationResult) : ScanUiState()
    data class Error(val message: String) : ScanUiState()
}