package com.gabrielamaro.spotted.model

import kotlinx.serialization.Serializable

@Serializable
data class Airport(
    val id: Int? = null,
    val created_at: String? = null,
    val airport_name: String? = null,
    val airport_icao: String? = null,
    val airport_iata: String? = null,
    val airport_city: String? = null
)

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
    val image_path: String? = null
)

@Serializable
data class PostInsert(
    val aircraft_prefix: String,
    val aircraft_model: String,
    val aircraft_airline: String,
    val airport_id: Int,
    val content: String = "",
    val image_path: String? = null // Nullable because user may not upload an image
)

data class FullPost(
    val post: Post,
    val airport: Airport?
)

