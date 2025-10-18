package com.gabrielamaro.spotted.data

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
 * Top 100 most popular airports (sample subset shown).
 * In real usage, you can replace or expand this list.
 */
val defaultAirports = listOf(
    Airport("Atlanta", "KATL", "ATL"),
    Airport("Beijing", "ZBAA", "PEK"),
    Airport("Los Angeles", "KLAX", "LAX"),
    Airport("Dubai", "OMDB", "DXB"),
    Airport("Tokyo Haneda", "RJTT", "HND"),
    Airport("London Heathrow", "EGLL", "LHR"),
    Airport("Paris Charles de Gaulle", "LFPG", "CDG"),
    Airport("Chicago O'Hare", "KORD", "ORD"),
    Airport("Shanghai Pudong", "ZSPD", "PVG"),
    Airport("Dallas/Fort Worth", "KDFW", "DFW"),
    // ... add up to 100 if desired
)

/**
 * Top 100 most popular aircraft types (grouped by manufacturer).
 */
val defaultManufacturers = listOf(
    Manufacturer("Boeing", listOf("737-800", "737 MAX 8", "747-8", "757-200", "767-300ER", "777-300ER", "787-9")),
    Manufacturer("Airbus", listOf("A320neo", "A321neo", "A330-300", "A350-900", "A380-800")),
    Manufacturer("Embraer", listOf("E175", "E190", "E195-E2")),
    Manufacturer("Bombardier", listOf("CRJ900", "Q400")),
    Manufacturer("Cessna", listOf("Citation XLS+", "208 Caravan")),
    Manufacturer("ATR", listOf("ATR 72-600", "ATR 42-500"))
)

/**
 * Static hardcoded list of 25 placeholder aircraft.
 */
val aircraftPlaceholders = listOf(
    AircraftPlaceholder("PT-101A", "Boeing", "737-800", "Atlanta", "KATL", "ATL", "2025-10-10 08:45"),
    AircraftPlaceholder("PT-102B", "Airbus", "A320neo", "Los Angeles", "KLAX", "LAX", "2025-10-11 09:20"),
    AircraftPlaceholder("PT-103C", "Embraer", "E190", "São Paulo", "SBGR", "GRU", "2025-10-11 15:30"),
    AircraftPlaceholder("PT-104D", "Airbus", "A350-900", "London Heathrow", "EGLL", "LHR", "2025-10-09 22:00"),
    AircraftPlaceholder("PT-105E", "Boeing", "777-300ER", "Dubai", "OMDB", "DXB", "2025-10-12 04:25"),
    AircraftPlaceholder("PT-106F", "Cessna", "208 Caravan", "Curitiba", "SBCT", "CWB", "2025-10-10 12:45"),
    AircraftPlaceholder("PT-107G", "Boeing", "787-9", "Tokyo Haneda", "RJTT", "HND", "2025-10-14 18:15"),
    AircraftPlaceholder("PT-108H", "Airbus", "A321neo", "Paris Charles de Gaulle", "LFPG", "CDG", "2025-10-13 05:50"),
    AircraftPlaceholder("PT-109I", "Embraer", "E195-E2", "São José do Rio Preto", "SBSR", "SJP", "2025-10-16 14:10"),
    AircraftPlaceholder("PT-110J", "Boeing", "737 MAX 8", "Chicago O'Hare", "KORD", "ORD", "2025-10-17 09:05"),
    AircraftPlaceholder("PT-111K", "Airbus", "A330-300", "Beijing", "ZBAA", "PEK", "2025-10-11 11:40"),
    AircraftPlaceholder("PT-112L", "Boeing", "767-300ER", "Dallas/Fort Worth", "KDFW", "DFW", "2025-10-15 06:10"),
    AircraftPlaceholder("PT-113M", "Boeing", "757-200", "Atlanta", "KATL", "ATL", "2025-10-09 19:45"),
    AircraftPlaceholder("PT-114N", "ATR", "ATR 72-600", "Belo Horizonte", "SBCF", "CNF", "2025-10-18 07:55"),
    AircraftPlaceholder("PT-115O", "Bombardier", "Q400", "London Heathrow", "EGLL", "LHR", "2025-10-17 10:20"),
    AircraftPlaceholder("PT-116P", "Airbus", "A380-800", "Dubai", "OMDB", "DXB", "2025-10-10 23:00"),
    AircraftPlaceholder("PT-117Q", "Embraer", "E175", "Curitiba", "SBCT", "CWB", "2025-10-15 17:30"),
    AircraftPlaceholder("PT-118R", "Boeing", "787-9", "Shanghai Pudong", "ZSPD", "PVG", "2025-10-13 21:10"),
    AircraftPlaceholder("PT-119S", "Cessna", "Citation XLS+", "Paris Charles de Gaulle", "LFPG", "CDG", "2025-10-14 09:25"),
    AircraftPlaceholder("PT-120T", "Airbus", "A321neo", "São Paulo", "SBGR", "GRU", "2025-10-18 13:40"),
    AircraftPlaceholder("PT-121U", "Boeing", "747-8", "Tokyo Haneda", "RJTT", "HND", "2025-10-16 15:15"),
    AircraftPlaceholder("PT-122V", "ATR", "ATR 42-500", "Belo Horizonte", "SBCF", "CNF", "2025-10-10 06:30"),
    AircraftPlaceholder("PT-123W", "Airbus", "A350-900", "London Heathrow", "EGLL", "LHR", "2025-10-11 16:45"),
    AircraftPlaceholder("PT-124X", "Boeing", "737-800", "Atlanta", "KATL", "ATL", "2025-10-12 11:00"),
    AircraftPlaceholder("PT-125Y", "Embraer", "E195-E2", "Curitiba", "SBCT", "CWB", "2025-10-18 08:00")
)
