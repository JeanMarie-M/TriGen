package com.example.trigen.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "modules")
data class ModuleEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val color: String,
    val orderIndex: Int,
    val totalLessons: Int,
    val totalQuestions: Int
)

@Entity(tableName = "lessons")
data class LessonEntity(
    @PrimaryKey
    val id: String,
    val moduleId: String,
    val title: String,
    val content: String,
    val keyPoints: String,
    val orderIndex: Int,
    val durationMinutes: Int
)

@Entity(tableName = "quiz_questions")
data class QuizQuestionEntity(
    @PrimaryKey
    val id: String,
    val moduleId: String,
    val question: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctAnswer: String,
    val explanation: String,
    val orderIndex: Int
)

@Entity(tableName = "badges")
data class BadgeEntity(
    @PrimaryKey
    val id: String,
    val moduleId: String,
    val name: String,
    val description: String,
    val icon: String
)

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey
    val moduleId: String,
    val lessonsCompleted: Int = 0,
    val totalLessons: Int = 0,
    val quizScore: Int = 0,
    val quizPassed: Boolean = false,
    val badgeEarned: Boolean = false,
    val completionDate: Long? = null,
    val lastAccessedLesson: String = ""
)