package com.gabrielamaro.spotted.data

import kotlin.random.Random

data class Airport(val city: String, val icao: String, val iata: String)
data class Manufacturer(val name: String, val models: List<String>)
data class AircraftPlaceholder(
    val tail: String,
    val manufacturer: String,
    val model: String,
    val airportCity: String,
    val airportIcao: String,
    val airportIata: String,
    val datetime: String
)

/**
 * Editable default lists. Modify these to change generated placeholders.
 * Delete this file later when you provide real dynamic data.
 */
val defaultAirports = listOf(
    Airport(city = "São José do Rio Preto", icao = "SBSR", iata = "SJP"),
    Airport(city = "Curitiba", icao = "SBCT", iata = "CWB"),
    Airport(city = "São Paulo", icao = "SBGR", iata = "GRU"),
    Airport(city = "Rio de Janeiro", icao = "SBGL", iata = "GIG"),
    Airport(city = "Belo Horizonte", icao = "SBCF", iata = "CNF")
)

val defaultManufacturers = listOf(
    Manufacturer(name = "Boeing", models = listOf("737-800", "737-900", "777-300ER", "787-9")),
    Manufacturer(name = "Airbus", models = listOf("A320neo", "A321neo", "A330-900", "A350-900")),
    Manufacturer(name = "Embraer", models = listOf("E190-E2", "E195-E2", "E175")),
    Manufacturer(name = "Cessna", models = listOf("172 Skyhawk", "208 Caravan"))
)

/**
 * Generate placeholder list.
 * - count: number of items (default 25)
 * - seed: optional deterministic seed (useful in tests)
 * - airports / manufacturers: override the default lists if desired
 */
fun generatePlaceholders(
    count: Int = 25,
    seed: Long? = null,
    airports: List<Airport> = defaultAirports,
    manufacturers: List<Manufacturer> = defaultManufacturers
): List<AircraftPlaceholder> {
    val rnd = if (seed != null) Random(seed) else Random.Default

    fun randomTail(): String {
        val number = rnd.nextInt(100, 1000) // 100..999
        val letter = ('A' + rnd.nextInt(0, 26))
        return "PT-$number$letter"
    }

    fun randomDatetime(): String {
        val year = 2025
        val month = 10
        val day = 1 + rnd.nextInt(0, 28)
        val hour = rnd.nextInt(0, 24)
        val minute = rnd.nextInt(0, 60)
        return String.format("%04d-%02d-%02d %02d:%02d", year, month, day, hour, minute)
    }

    return List(count) {
        val airport = airports.random(rnd)
        val man = manufacturers.random(rnd)
        val model = man.models.random(rnd)
        AircraftPlaceholder(
            tail = randomTail(),
            manufacturer = man.name,
            model = model,
            airportCity = airport.city,
            airportIcao = airport.icao,
            airportIata = airport.iata,
            datetime = randomDatetime()
        )
    }
}
