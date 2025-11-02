package com.gabrielamaro.spotted.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.gabrielamaro.spotted.data.local.entity.AircraftEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AircraftDao {

    @Query("SELECT * FROM aircrafts ORDER BY datetime DESC")
    fun getAllAircrafts(): Flow<List<AircraftEntity>>

    @Upsert
    suspend fun upsertAircraft(aircraft: AircraftEntity)

    @Query("SELECT * FROM aircrafts WHERE id = :id LIMIT 1")
    suspend fun getAircraftById(id: Int): AircraftEntity?

    @Query("DELETE FROM aircrafts WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM aircrafts")
    suspend fun clearAll()


}
