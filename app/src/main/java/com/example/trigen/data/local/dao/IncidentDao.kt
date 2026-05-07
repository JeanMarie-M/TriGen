package com.example.trigen.data.local.dao

import androidx.room.*
import com.example.trigen.data.local.entity.IncidentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IncidentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(incident: IncidentEntity)

    @Update
    suspend fun update(incident: IncidentEntity)

    @Delete
    suspend fun delete(incident: IncidentEntity)

    @Query("SELECT * FROM incidents ORDER BY createdAt DESC")
    fun getAllIncidents(): Flow<List<IncidentEntity>>

    @Query("SELECT * FROM incidents WHERE id = :id")
    suspend fun getIncidentById(id: String): IncidentEntity?

    @Query("SELECT * FROM incidents WHERE isSynced = 0")
    suspend fun getUnsyncedIncidents(): List<IncidentEntity>

    @Query("UPDATE incidents SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)

    @Query("DELETE FROM incidents WHERE id = :id")
    suspend fun deleteById(id: String)

}