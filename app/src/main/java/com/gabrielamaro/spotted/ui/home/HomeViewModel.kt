package com.gabrielamaro.spotted.ui.home

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.gabrielamaro.spotted.model.FullPost

class HomeViewModel : ViewModel() {

    var selectedPost by mutableStateOf<FullPost?>(null)
        private set

    fun updateSelectedPost(post: FullPost?) {
        selectedPost = post
    }
}
