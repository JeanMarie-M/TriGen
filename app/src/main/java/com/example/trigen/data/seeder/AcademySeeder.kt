package com.example.trigen.data.seeder

import android.content.Context
import com.example.trigen.data.local.dao.AcademyDao
import com.example.trigen.data.local.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AcademySeeder @Inject constructor(
    @ApplicationContext private val context: Context,
    private val academyDao: AcademyDao
) {
    suspend fun seedIfNeeded() = withContext(Dispatchers.IO) {
        val count = academyDao.getModuleCount()
        if (count == 0) {
            val json = context.assets.open("academy_content.json")
                .bufferedReader().use { it.readText() }
            val root = JSONObject(json)
            seedModules(root.getJSONArray("modules"))
            seedLessons(root.getJSONArray("lessons"))
            seedQuestions(root.getJSONArray("questions"))
            seedBadges(root.getJSONArray("badges"))
        }
    }

    private suspend fun seedModules(array: JSONArray) {
        val modules = mutableListOf<ModuleEntity>()
        val progress = mutableListOf<UserProgressEntity>()
        
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val moduleId = obj.getString("id")
            val totalLessons = obj.getInt("totalLessons")
            
            modules.add(ModuleEntity(
                id = moduleId,
                title = obj.getString("title"),
                description = obj.getString("description"),
                icon = obj.getString("icon"),
                color = obj.getString("color"),
                orderIndex = obj.getInt("orderIndex"),
                totalLessons = totalLessons,
                totalQuestions = obj.getInt("totalQuestions")
            ))
            
            progress.add(UserProgressEntity(
                moduleId = moduleId,
                totalLessons = totalLessons
            ))
        }
        
        academyDao.insertModules(modules)
        academyDao.insertProgress(progress)
    }

    private suspend fun seedLessons(array: JSONArray) {
        val lessons = (0 until array.length()).map { i ->
            val obj = array.getJSONObject(i)
            LessonEntity(
                id = obj.getString("id"),
                moduleId = obj.getString("moduleId"),
                title = obj.getString("title"),
                content = obj.getString("content"),
                keyPoints = obj.getString("keyPoints"),
                orderIndex = obj.getInt("orderIndex"),
                durationMinutes = obj.getInt("durationMinutes")
            )
        }
        academyDao.insertLessons(lessons)
    }

    private suspend fun seedQuestions(array: JSONArray) {
        val questions = (0 until array.length()).map { i ->
            val obj = array.getJSONObject(i)
            QuizQuestionEntity(
                id = obj.getString("id"),
                moduleId = obj.getString("moduleId"),
                question = obj.getString("question"),
                optionA = obj.getString("optionA"),
                optionB = obj.getString("optionB"),
                optionC = obj.getString("optionC"),
                optionD = obj.getString("optionD"),
                correctAnswer = obj.getString("correctAnswer"),
                explanation = obj.getString("explanation"),
                orderIndex = obj.getInt("orderIndex")
            )
        }
        academyDao.insertQuestions(questions)
    }

    private suspend fun seedBadges(array: JSONArray) {
        val badges = (0 until array.length()).map { i ->
            val obj = array.getJSONObject(i)
            BadgeEntity(
                id = obj.getString("id"),
                moduleId = obj.getString("moduleId"),
                name = obj.getString("name"),
                description = obj.getString("description"),
                icon = obj.getString("icon")
            )
        }
        academyDao.insertBadges(badges)
    }
}