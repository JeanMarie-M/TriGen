package com.example.trigen.screens.academy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trigen.data.local.entity.LessonEntity
import com.example.trigen.data.local.entity.UserProgressEntity
import com.example.trigen.data.repository.AcademyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LessonUiState(
    val lesson: LessonEntity? = null,
    val isLoading: Boolean = true,
    val isCompleted: Boolean = false
)

@HiltViewModel
class LessonViewModel @Inject constructor(
    private val repository: AcademyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LessonUiState())
    val uiState: StateFlow<LessonUiState> = _uiState.asStateFlow()

    fun loadLesson(lessonId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val lesson = repository.getLesson(lessonId)
            if (lesson != null) {
                val progress = repository.getProgressForModule(lesson.moduleId)
                _uiState.update {
                    it.copy(
                        lesson = lesson,
                        isLoading = false,
                        // Lesson is completed if lessonsCompleted count is >= its order index
                        isCompleted = (progress?.lessonsCompleted ?: 0) >= lesson.orderIndex
                    )
                }
            }
        }
    }

    fun completeLesson() {
        val currentLesson = _uiState.value.lesson ?: return
        viewModelScope.launch {
            val progress = repository.getProgressForModule(currentLesson.moduleId)
            val currentLessonsCompleted = progress?.lessonsCompleted ?: 0
            
            // Only increment if this is the next lesson to complete
            // e.g., if 0 lessons completed, we can complete lesson with orderIndex 1
            if (currentLessonsCompleted == currentLesson.orderIndex - 1) {
                val updatedProgress = progress?.copy(
                    lessonsCompleted = currentLessonsCompleted + 1,
                    lastAccessedLesson = currentLesson.id
                ) ?: UserProgressEntity(
                    moduleId = currentLesson.moduleId,
                    lessonsCompleted = 1,
                    lastAccessedLesson = currentLesson.id
                )
                repository.updateProgress(updatedProgress)
            }
            _uiState.update { it.copy(isCompleted = true) }
        }
    }
}
