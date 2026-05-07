package com.example.trigen.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.trigen.data.local.entity.ProtocolEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProtocolDao {

    @Query("SELECT * FROM protocols WHERE injuryType = :injuryType")
    suspend fun getProtocol(injuryType: String): ProtocolEntity?

    @Query("SELECT * FROM protocols")
    fun getAllProtocols(): Flow<List<ProtocolEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(protocols: List<ProtocolEntity>)

    @Query("SELECT COUNT(*) FROM protocols")
    suspend fun getCount(): Int
}