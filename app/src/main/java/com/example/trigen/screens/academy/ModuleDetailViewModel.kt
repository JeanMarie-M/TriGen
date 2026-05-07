package com.example.trigen.screens.academy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trigen.data.local.entity.LessonEntity
import com.example.trigen.data.local.entity.ModuleEntity
import com.example.trigen.data.local.entity.UserProgressEntity
import com.example.trigen.data.repository.AcademyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ModuleDetailUiState(
    val module: ModuleEntity? = null,
    val lessons: List<LessonEntity> = emptyList(),
    val progress: UserProgressEntity? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class ModuleDetailViewModel @Inject constructor(
    private val repository: AcademyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ModuleDetailUiState())
    val uiState: StateFlow<ModuleDetailUiState> = _uiState.asStateFlow()

    fun loadModuleDetails(moduleId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val module = repository.getModule(moduleId)
            
            combine(
                repository.getLessonsForModule(moduleId),
                repository.getProgressFlowForModule(moduleId)
            ) { lessons, progress ->
                _uiState.update {
                    it.copy(
                        module = module,
                        lessons = lessons,
                        progress = progress,
                        isLoading = false
                    )
                }
            }.collect()
        }
    }
}
