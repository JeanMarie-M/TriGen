package com.example.trigen.screens.academy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trigen.data.local.entity.ModuleEntity
import com.example.trigen.data.local.entity.UserProgressEntity
import com.example.trigen.data.repository.AcademyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ModuleWithProgress(
    val module: ModuleEntity,
    val progress: UserProgressEntity?,
    val isLocked: Boolean = false
)

@HiltViewModel
class AcademyViewModel @Inject constructor(
    private val repository: AcademyRepository
) : ViewModel() {

    private val _modules = repository.getAllModules()
    private val _progress = repository.getAllProgress()

    val modulesWithProgress: StateFlow<List<ModuleWithProgress>> = combine(_modules, _progress) { modules, progressList ->
        modules.mapIndexed { index, module ->
            val progress = progressList.find { it.moduleId == module.id }
            val isLocked = if (index == 0) {
                false
            } else {
                val previousModuleId = modules[index - 1].id
                val previousProgress = progressList.find { it.moduleId == previousModuleId }
                previousProgress?.badgeEarned != true
            }
            
            ModuleWithProgress(
                module = module,
                progress = progress,
                isLocked = isLocked
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val earnedBadgesCount = repository.getAllProgress()
        .map { progressList -> progressList.count { it.badgeEarned } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
}