package com.gabrielamaro.spotted.model

import kotlinx.serialization.Serializable

// -------------------------------------------------------------
// AIRPORT MODEL
// -------------------------------------------------------------
@Serializable
data class Airport(
    val id: Int? = null,
    val created_at: String? = null,

    val airport_name: String? = null,
    val airport_icao: String? = null,
    val airport_iata: String? = null,
    val airport_city: String? = null
)

// -------------------------------------------------------------
// POST MODEL (Matches posts table exactly)
// -------------------------------------------------------------
@Serializable
data class Post(
    val id: Long? = null,
    val created_at: String? = null,

    val content: String? = null,
    val user_id: String? = null,

    val aircraft_prefix: String? = null,
    val aircraft_model: String? = null,
    val aircraft_airline: String? = null,

    val airport_id: Int? = null,

    // ✅ NEW FIELD (text)
    val image_path: String? = null
)

// -------------------------------------------------------------
// POST INSERT MODEL (Used only for inserting new posts)
// -------------------------------------------------------------
@Serializable
data class PostInsert(
    val aircraft_prefix: String,
    val aircraft_model: String,
    val aircraft_airline: String,
    val airport_id: Int,

    val content: String = "",

    // ✅ NEW FIELD (nullable because user may not upload a photo)
    val image_path: String? = null
)

// -------------------------------------------------------------
// FULL POST (HomeScreen merged model)
// -------------------------------------------------------------
data class FullPost(
    val post: Post,
    val airport: Airport?
)
