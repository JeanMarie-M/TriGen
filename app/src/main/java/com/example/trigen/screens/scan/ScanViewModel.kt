package com.example.trigen.screens.scan

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trigen.triage.classifier.InjuryClassifier
import com.example.trigen.triage.classifier.InjuryLabel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val classifier: InjuryClassifier
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    private var isProcessing = false
    private var lastDetectedLabel: InjuryLabel? = null
    private var consecutiveDetections = 0

    companion object {
        private const val REQUIRED_CONSECUTIVE = 3
    }

    fun onPermissionGranted() {
        _uiState.value = ScanUiState.Scanning
    }

    fun onPermissionDenied() {
        _uiState.value = ScanUiState.PermissionDenied
    }

    fun analyzeFrame(bitmap: Bitmap, rotation: Int) {
        if (isProcessing) return
        val current = _uiState.value
        if (current is ScanUiState.Result) return

        isProcessing = true
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val result = classifier.classify(bitmap, rotation)
                
                // Temporal smoothing: Require same label for multiple frames
                if (result.isReliable) {
                    if (result.label == lastDetectedLabel) {
                        consecutiveDetections++
                    } else {
                        lastDetectedLabel = result.label
                        consecutiveDetections = 1
                    }

                    if (consecutiveDetections >= REQUIRED_CONSECUTIVE) {
                        _uiState.value = ScanUiState.Result(result)
                    }
                } else {
                    consecutiveDetections = 0
                }
            } catch (e: Exception) {
                _uiState.value = ScanUiState.Error(e.message ?: "Classification failed")
            } finally {
                isProcessing = false
            }
        }
    }

    fun resetScan() {
        consecutiveDetections = 0
        lastDetectedLabel = null
        _uiState.value = ScanUiState.Scanning
    }

    override fun onCleared() {
        super.onCleared()
        classifier.close()
    }
}