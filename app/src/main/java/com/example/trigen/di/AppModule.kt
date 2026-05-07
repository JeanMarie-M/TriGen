package com.example.trigen.di

import android.content.Context
import androidx.room.Room
import com.example.trigen.data.local.TriGenDatabase
import com.example.trigen.data.local.dao.AcademyDao
import com.example.trigen.data.local.dao.IncidentDao
import com.example.trigen.data.local.dao.ProtocolDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApplicationContext(
        @ApplicationContext context: Context
    ): Context = context

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): TriGenDatabase = Room.databaseBuilder(
        context,
        TriGenDatabase::class.java,
        TriGenDatabase.DATABASE_NAME
    )
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideProtocolDao(database: TriGenDatabase): ProtocolDao =
        database.protocolDao()

    @Provides
    @Singleton
    fun provideIncidentDao(database: TriGenDatabase): IncidentDao =
        database.incidentDao()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore =
        FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth =
        FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideAcademyDao(database: TriGenDatabase): AcademyDao =
        database.academyDao()
}