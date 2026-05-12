package com.example.trigen.screens.protocol

import android.app.Application
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.trigen.data.preferences.UserPreferences
import com.example.trigen.data.repository.ProtocolRepository
import com.example.trigen.data.seeder.ProtocolSeeder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

sealed class ProtocolUiState {
    object Loading : ProtocolUiState()
    data class Success(
        val protocol: InjuryProtocol,
        val currentStepIndex: Int = 0,
        val isTtsActive: Boolean = false,
        val isVoiceCommandActive: Boolean = false
    ) : ProtocolUiState()
    data class Error(val message: String) : ProtocolUiState()
}

@HiltViewModel
class ProtocolViewModel @Inject constructor(
    application: Application,
    private val repository: ProtocolRepository,
    private val seeder: ProtocolSeeder,
    private val userPreferences: UserPreferences
) : AndroidViewModel(application), TextToSpeech.OnInitListener {

    private val _uiState = MutableStateFlow<ProtocolUiState>(ProtocolUiState.Loading)
    val uiState: StateFlow<ProtocolUiState> = _uiState.asStateFlow()

    private var tts: TextToSpeech? = null
    private var speechRecognizer: SpeechRecognizer? = null
    private var currentVoiceType: String = "Default"

    init {
        tts = TextToSpeech(application, this)
        setupSpeechRecognizer(application)
        observeVoicePreference()
    }

    private fun observeVoicePreference() {
        viewModelScope.launch {
            userPreferences.voiceFlow.collect { voice ->
                currentVoiceType = voice
                applyVoice()
            }
        }
    }

    private fun applyVoice() {
        val availableVoices = tts?.voices ?: return
        val voice = when (currentVoiceType) {
            "Male" -> availableVoices.firstOrNull { it.name.lowercase().contains("male") && !it.isNetworkConnectionRequired }
            "Female" -> availableVoices.firstOrNull { it.name.lowercase().contains("female") && !it.isNetworkConnectionRequired }
            "British" -> availableVoices.firstOrNull { it.locale.country == "GB" }
            "Australian" -> availableVoices.firstOrNull { it.locale.country == "AU" }
            else -> tts?.defaultVoice
        }
        voice?.let { tts?.voice = it }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.getDefault()
            applyVoice()
        }
    }

    private fun setupSpeechRecognizer(application: Application) {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(application)
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                matches?.firstOrNull()?.let { command ->
                    handleVoiceCommand(command.lowercase())
                }
                // Restart listening if voice commands are still active
                if ((_uiState.value as? ProtocolUiState.Success)?.isVoiceCommandActive == true) {
                    startListening()
                }
            }
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {
                if ((_uiState.value as? ProtocolUiState.Success)?.isVoiceCommandActive == true) {
                    startListening()
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    private fun handleVoiceCommand(command: String) {
        when {
            command.contains("next") || command.contains("continue") -> nextStep()
            command.contains("previous") || command.contains("back") -> prevStep()
            command.contains("repeat") -> speakCurrentStep()
            command.contains("stop") -> toggleTts(false)
        }
    }

    fun loadProtocol(injuryType: String) {
        viewModelScope.launch {
            _uiState.value = ProtocolUiState.Loading
            try {
                seeder.seedIfNeeded()
                val protocol = repository.getProtocol(injuryType)
                _uiState.value = if (protocol != null) {
                    ProtocolUiState.Success(protocol)
                } else {
                    ProtocolUiState.Error("Protocol not found for: $injuryType")
                }
            } catch (e: Exception) {
                _uiState.value = ProtocolUiState.Error(e.message ?: "Failed to load protocol")
            }
        }
    }

    fun nextStep() {
        val currentState = _uiState.value as? ProtocolUiState.Success ?: return
        if (currentState.currentStepIndex < currentState.protocol.steps.size - 1) {
            val newIndex = currentState.currentStepIndex + 1
            _uiState.update { currentState.copy(currentStepIndex = newIndex) }
            if (currentState.isTtsActive) speakCurrentStep()
        }
    }

    fun prevStep() {
        val currentState = _uiState.value as? ProtocolUiState.Success ?: return
        if (currentState.currentStepIndex > 0) {
            val newIndex = currentState.currentStepIndex - 1
            _uiState.update { currentState.copy(currentStepIndex = newIndex) }
            if (currentState.isTtsActive) speakCurrentStep()
        }
    }

    fun toggleTts(active: Boolean) {
        val currentState = _uiState.value as? ProtocolUiState.Success ?: return
        _uiState.update { currentState.copy(isTtsActive = active) }
        if (active) {
            speakCurrentStep()
        } else {
            tts?.stop()
        }
    }

    fun toggleVoiceCommands(active: Boolean) {
        val currentState = _uiState.value as? ProtocolUiState.Success ?: return
        _uiState.update { currentState.copy(isVoiceCommandActive = active) }
        if (active) {
            startListening()
        } else {
            speechRecognizer?.stopListening()
        }
    }

    private fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }
        speechRecognizer?.startListening(intent)
    }

    private fun speakCurrentStep() {
        val currentState = _uiState.value as? ProtocolUiState.Success ?: return
        val step = currentState.protocol.steps[currentState.currentStepIndex]
        val text = "Step ${step.stepNumber}. ${step.title}. ${step.instruction}. ${step.warning ?: ""}"
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "step_${step.stepNumber}")
    }

    override fun onCleared() {
        super.onCleared()
        tts?.shutdown()
        speechRecognizer?.destroy()
    }
}
