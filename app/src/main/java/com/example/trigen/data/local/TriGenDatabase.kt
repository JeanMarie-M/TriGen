package com.example.trigen.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.trigen.data.local.dao.AcademyDao
import com.example.trigen.data.local.dao.IncidentDao
import com.example.trigen.data.local.dao.ProtocolDao
import com.example.trigen.data.local.entity.*

@Database(
    entities = [
        ProtocolEntity::class,
        IncidentEntity::class,
        ModuleEntity::class,
        LessonEntity::class,
        QuizQuestionEntity::class,
        BadgeEntity::class,
        UserProgressEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class TriGenDatabase : RoomDatabase() {
    abstract fun protocolDao(): ProtocolDao
    abstract fun incidentDao(): IncidentDao
    abstract fun academyDao(): AcademyDao

    companion object {
        const val DATABASE_NAME = "trigen_db"
    }
}