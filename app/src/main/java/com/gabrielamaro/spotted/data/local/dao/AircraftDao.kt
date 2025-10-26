package com.gabrielamaro.spotted.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.gabrielamaro.spotted.data.local.entity.AircraftEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AircraftDao {

    @Query("SELECT * FROM aircrafts ORDER BY datetime DESC")
    fun getAllAircrafts(): Flow<List<AircraftEntity>>

    @Insert
    suspend fun insertAircraft(aircraft: AircraftEntity)

    @Query("DELETE FROM aircrafts")
    suspend fun clearAll()
}
