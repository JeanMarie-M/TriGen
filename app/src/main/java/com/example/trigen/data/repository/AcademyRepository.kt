package com.example.trigen.data.repository

import com.example.trigen.data.local.dao.AcademyDao
import com.example.trigen.data.local.entity.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AcademyRepository @Inject constructor(
    private val academyDao: AcademyDao
) {
    fun getAllModules(): Flow<List<ModuleEntity>> = academyDao.getAllModules()
    
    suspend fun getModule(moduleId: String): ModuleEntity? = academyDao.getModule(moduleId)
    
    fun getLessonsForModule(moduleId: String): Flow<List<LessonEntity>> = academyDao.getLessonsForModule(moduleId)
    
    suspend fun getLesson(lessonId: String): LessonEntity? = academyDao.getLesson(lessonId)
    
    suspend fun getQuestionsForModule(moduleId: String): List<QuizQuestionEntity> = academyDao.getQuestionsForModule(moduleId)
    
    fun getAllBadges(): Flow<List<BadgeEntity>> = academyDao.getAllBadges()
    
    suspend fun getBadgeForModule(moduleId: String): BadgeEntity? = academyDao.getBadgeForModule(moduleId)
    
    fun getAllProgress(): Flow<List<UserProgressEntity>> = academyDao.getAllProgress()
    
    suspend fun getProgressForModule(moduleId: String): UserProgressEntity? = academyDao.getProgress(moduleId)
    
    fun getProgressFlowForModule(moduleId: String): Flow<UserProgressEntity?> = academyDao.getProgressFlow(moduleId)
    
    suspend fun updateProgress(progress: UserProgressEntity) = academyDao.upsertProgress(progress)
    
    suspend fun getEarnedBadgeCount(): Int = academyDao.getEarnedBadgeCount()

    fun getEarnedBadgeCountFlow(): Flow<Int> = academyDao.getEarnedBadgeCountFlow()
}