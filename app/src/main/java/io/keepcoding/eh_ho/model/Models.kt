package io.keepcoding.eh_ho.model

import org.json.JSONObject

sealed class LogIn {
    data class Success(val userName: String) : LogIn()
    data class Error(val error: String) : LogIn()
}

sealed class LogUp{
    data class Success(val message: String) : LogUp()
    data class Error(val error: String) : LogUp()
}

data class Topic(
    val id: Int,
    val title: String,
    val excerpt: String?,
    val pinned: Boolean,
    val lastPosterUsername: String,
    val postCount: Int,
    val likeCount: Int

)