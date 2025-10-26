package com.gabrielamaro.spotted.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gabrielamaro.spotted.data.local.dao.AircraftDao
import com.gabrielamaro.spotted.data.local.entity.AircraftEntity

@Database(entities = [AircraftEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun aircraftDao(): AircraftDao
}
