package com.gabrielamaro.spotted.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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

    // DB field storing the path to the uploaded storage image
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

    // Nullable because user may not upload an image
    val image_path: String? = null
)

// -------------------------------------------------------------
// FULL POST (HomeScreen merged model)
// -------------------------------------------------------------
data class FullPost(
    val post: Post,
    val airport: Airport?
)


// =================================================================
// HOME VIEW MODEL (added here exactly as you requested)
// =================================================================

class HomeViewModel : ViewModel() {

    // Holds currently selected post for AddAircraftScreen (view/edit mode)
    private val _selectedPost = MutableStateFlow<FullPost?>(null)
    val selectedPost: StateFlow<FullPost?> = _selectedPost

    // Called when clicking a post (view mode) or FAB (create mode)
    fun setSelectedPost(post: FullPost?) {
        viewModelScope.launch {
            _selectedPost.emit(post)
        }
    }
}
