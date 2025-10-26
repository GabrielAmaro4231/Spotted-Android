package com.gabrielamaro.spotted.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gabrielamaro.spotted.data.local.DatabaseProvider
import com.gabrielamaro.spotted.data.local.entity.AircraftEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = DatabaseProvider.getDatabase(application).aircraftDao()

    // Flow of all aircrafts from Room
    val aircrafts = dao.getAllAircrafts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Insert new aircraft into Room */
    fun addAircraft(
        tail: String,
        manufacturer: String,
        model: String,
        airportCity: String,
        airportIcao: String,
        airportIata: String,
        datetime: String
    ) {
        val newAircraft = AircraftEntity(
            tail = tail,
            manufacturer = manufacturer,
            model = model,
            airportCity = airportCity,
            airportIcao = airportIcao,
            airportIata = airportIata,
            datetime = datetime
        )
        viewModelScope.launch {
            dao.insertAircraft(newAircraft)
        }
    }

    /** Delete by tail (simple example) */
    fun deleteAircraft(tail: String) {
        viewModelScope.launch {
            val all = aircrafts.value
            val target = all.firstOrNull { it.tail.equals(tail, ignoreCase = true) }
            if (target != null) {
                dao.clearAll() // optional: you can instead write a custom delete query
                all.filterNot { it.tail.equals(tail, ignoreCase = true) }
                    .forEach { dao.insertAircraft(it) }
            }
        }
    }

    /** Optionally preload placeholder data on first app open */
    fun preloadIfEmpty(defaults: List<AircraftEntity>) {
        viewModelScope.launch {
            if (aircrafts.value.isEmpty()) {
                defaults.forEach { dao.insertAircraft(it) }
            }
        }
    }
}
