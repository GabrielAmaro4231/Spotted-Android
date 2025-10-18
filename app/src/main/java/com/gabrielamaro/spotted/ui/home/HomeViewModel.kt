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

    fun deleteAircraft(tail: String) {
        _aircrafts.removeAll { it.tail == tail }
    }
}
