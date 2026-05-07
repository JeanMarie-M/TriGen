package com.example.trigen.screens.academy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trigen.data.local.entity.QuizQuestionEntity
import com.example.trigen.data.local.entity.UserProgressEntity
import com.example.trigen.data.repository.AcademyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizUiState(
    val questions: List<QuizQuestionEntity> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedOption: String? = null,
    val score: Int = 0,
    val isFinished: Boolean = false,
    val isLoading: Boolean = true,
    val showExplanation: Boolean = false
)

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val repository: AcademyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    fun loadQuiz(moduleId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val questions = repository.getQuestionsForModule(moduleId)
            _uiState.update { 
                it.copy(
                    questions = questions,
                    isLoading = false
                )
            }
        }
    }

    fun selectOption(option: String) {
        if (_uiState.value.showExplanation) return
        _uiState.update { it.copy(selectedOption = option) }
    }

    fun submitAnswer() {
        val state = _uiState.value
        val currentQuestion = state.questions[state.currentQuestionIndex]
        
        if (state.selectedOption == null) return

        val isCorrect = state.selectedOption == currentQuestion.correctAnswer
        val newScore = if (isCorrect) state.score + 1 else state.score
        
        _uiState.update { 
            it.copy(
                score = newScore,
                showExplanation = true
            )
        }
    }

    fun nextQuestion(moduleId: String) {
        val state = _uiState.value
        if (state.currentQuestionIndex + 1 < state.questions.size) {
            _uiState.update {
                it.copy(
                    currentQuestionIndex = it.currentQuestionIndex + 1,
                    selectedOption = null,
                    showExplanation = false
                )
            }
        } else {
            finishQuiz(moduleId)
        }
    }

    private fun finishQuiz(moduleId: String) {
        viewModelScope.launch {
            val state = _uiState.value
            val totalQuestions = state.questions.size
            val passed = if (totalQuestions > 0) state.score >= (totalQuestions * 0.7) else true
            
            val progress = repository.getProgressForModule(moduleId)
            val updatedProgress = progress?.copy(
                quizScore = state.score,
                quizPassed = passed,
                badgeEarned = passed,
                completionDate = if (passed) System.currentTimeMillis() else progress.completionDate
            ) ?: UserProgressEntity(
                moduleId = moduleId,
                quizScore = state.score,
                quizPassed = passed,
                badgeEarned = passed,
                completionDate = if (passed) System.currentTimeMillis() else null
            )
            repository.updateProgress(updatedProgress)
            
            _uiState.update { it.copy(isFinished = true) }
        }
    }
}
