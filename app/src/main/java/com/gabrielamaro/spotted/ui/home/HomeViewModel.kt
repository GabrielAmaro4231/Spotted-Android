package com.gabrielamaro.spotted.ui.home

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.gabrielamaro.spotted.model.FullPost

class HomeViewModel : ViewModel() {

    // ---------------------------------------------------------
    // SELECTED POST TO VIEW / EDIT INSIDE AddAircraftScreen
    // ---------------------------------------------------------
    var selectedPost by mutableStateOf<FullPost?>(null)
        private set

    // Renamed to avoid JVM signature clash
    fun updateSelectedPost(post: FullPost?) {
        selectedPost = post
    }
}
