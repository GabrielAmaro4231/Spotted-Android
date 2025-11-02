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

    val aircrafts = dao.getAllAircrafts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addOrUpdateAircraft(
        id: Int? = null,
        tail: String,
        manufacturer: String,
        model: String,
        airportCity: String,
        airportIcao: String,
        airportIata: String,
        datetime: String
    ) {
        val aircraft = AircraftEntity(
            id = id ?: 0,
            tail = tail,
            manufacturer = manufacturer,
            model = model,
            airportCity = airportCity,
            airportIcao = airportIcao,
            airportIata = airportIata,
            datetime = datetime
        )
        viewModelScope.launch {
            dao.upsertAircraft(aircraft)
        }
    }

    fun deleteAircraft(id: Int) {
        viewModelScope.launch {
            dao.deleteById(id)
        }
    }

    fun preloadIfEmpty(defaults: List<AircraftEntity>) {
        viewModelScope.launch {
            if (aircrafts.value.isEmpty()) {
                defaults.forEach { dao.upsertAircraft(it) }
            }
        }
    }
}

