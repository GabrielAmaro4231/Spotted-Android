package com.gabrielamaro.spotted.ui.home

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.gabrielamaro.spotted.data.AircraftPlaceholder
import com.gabrielamaro.spotted.data.aircraftPlaceholders

class HomeViewModel : ViewModel() {

    private val _aircrafts = mutableStateListOf<AircraftPlaceholder>().apply {
        addAll(aircraftPlaceholders)
    }

    val aircrafts: List<AircraftPlaceholder> get() = _aircrafts

    fun addAircraft(
        tail: String,
        manufacturer: String,
        model: String,
        airportCity: String,
        airportIcao: String,
        airportIata: String,
        datetime: String
    ) {
        val newAircraft = AircraftPlaceholder(
            tail = tail,
            manufacturer = manufacturer,
            model = model,
            airportCity = airportCity,
            airportIcao = airportIcao,
            airportIata = airportIata,
            datetime = datetime
        )
        _aircrafts.add(0, newAircraft)
    }

    fun deleteAircraft(tail: String) {
        _aircrafts.removeAll { it.tail.equals(tail, ignoreCase = true) }
    }
}
