package com.example.trigen.data.local.dao

import androidx.room.*
import com.example.trigen.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AcademyDao {

    // Modules
    @Query("SELECT * FROM modules ORDER BY orderIndex")
    fun getAllModules(): Flow<List<ModuleEntity>>

    @Query("SELECT * FROM modules WHERE id = :moduleId")
    suspend fun getModule(moduleId: String): ModuleEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertModules(modules: List<ModuleEntity>)

    // Lessons
    @Query("SELECT * FROM lessons WHERE moduleId = :moduleId ORDER BY orderIndex")
    fun getLessonsForModule(moduleId: String): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE id = :lessonId")
    suspend fun getLesson(lessonId: String): LessonEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLessons(lessons: List<LessonEntity>)

    // Quiz
    @Query("SELECT * FROM quiz_questions WHERE moduleId = :moduleId ORDER BY orderIndex")
    suspend fun getQuestionsForModule(moduleId: String): List<QuizQuestionEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertQuestions(questions: List<QuizQuestionEntity>)

    // Badges
    @Query("SELECT * FROM badges")
    fun getAllBadges(): Flow<List<BadgeEntity>>

    @Query("SELECT * FROM badges WHERE moduleId = :moduleId")
    suspend fun getBadgeForModule(moduleId: String): BadgeEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBadges(badges: List<BadgeEntity>)

    // Progress
    @Query("SELECT * FROM user_progress WHERE moduleId = :moduleId")
    suspend fun getProgress(moduleId: String): UserProgressEntity?

    @Query("SELECT * FROM user_progress WHERE moduleId = :moduleId")
    fun getProgressFlow(moduleId: String): Flow<UserProgressEntity?>

    @Query("SELECT * FROM user_progress")
    fun getAllProgress(): Flow<List<UserProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(progress: UserProgressEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProgress(progress: List<UserProgressEntity>)

    @Query("SELECT COUNT(*) FROM modules")
    suspend fun getModuleCount(): Int

    @Query("SELECT COUNT(*) FROM user_progress WHERE badgeEarned = 1")
    suspend fun getEarnedBadgeCount(): Int

    @Query("SELECT COUNT(*) FROM user_progress WHERE badgeEarned = 1")
    fun getEarnedBadgeCountFlow(): Flow<Int>
}