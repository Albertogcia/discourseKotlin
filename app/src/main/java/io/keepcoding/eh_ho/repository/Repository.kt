package io.keepcoding.eh_ho.repository

import io.keepcoding.eh_ho.model.LogIn
import io.keepcoding.eh_ho.model.LogUp
import io.keepcoding.eh_ho.model.Post
import io.keepcoding.eh_ho.model.Topic
import io.keepcoding.eh_ho.network.Client

class Repository(private val client: Client) {

    private var userName: String = ""

    fun signIn(
        userName: String,
        password: String,
        callback: Callback<LogIn>
    ) {
        client.signIn(userName, password) {
            storeLogIn(it)
            callback.onResult(it)
        }
    }

    fun signup(
        username: String,
        email: String,
        password: String,
        callback: Callback<LogUp>,
    ) {
        client.signUp(username, email, password) {
            callback.onResult(it)
        }
    }

    fun getTopics(callback: Callback<Result<List<Topic>>>) {
        client.getTopics {
            callback.onResult(it)
        }
    }

    fun getPosts(topicId: Int, callback: Callback<Result<List<Post>>>) {
        client.getPosts(topicId) {
            callback.onResult(it)
        }
    }

    fun createPost(topicId: Int, text: String, callback: Callback<Result<Boolean>>){
        client.createPost(topicId, text, userName) {
            callback.onResult(it)
        }
    }

    private fun storeLogIn(logIn: LogIn) {
        userName = (logIn as? LogIn.Success)?.userName ?: userName
    }

    fun interface Callback<T> {
        fun onResult(t: T)
    }
}