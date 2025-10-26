package com.gabrielamaro.spotted.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "aircrafts")
data class AircraftEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tail: String,
    val manufacturer: String,
    val model: String,
    val airportCity: String,
    val airportIcao: String,
    val airportIata: String,
    val datetime: String
)
