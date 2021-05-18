package io.keepcoding.eh_ho.network

import io.keepcoding.eh_ho.model.LogIn
import io.keepcoding.eh_ho.model.Topic
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

fun Response.toSignInModel(): LogIn = when (this.isSuccessful) {
    true -> LogIn.Success(JSONObject(this.body?.string()).getJSONObject("user").getString("username"))
    false -> LogIn.Error(this.body?.string() ?: "Some Error parsing response")
}
fun IOException.toSignInModel(): LogIn = LogIn.Error(this.toString())

fun Response.toSignUpModel(): LogIn = when (this.isSuccessful) {
    true -> LogIn.Success(JSONObject(this.body?.string()).getJSONObject("user").getString("username"))
    false -> LogIn.Error(this.body?.string() ?: "Some Error parsing response")
}
fun IOException.toSignUpModel(): LogIn = LogIn.Error(this.toString())

fun Response.toTopicsModel(): Result<List<Topic>> = when (this.isSuccessful) {
    true -> Result.success(parseTopics(body?.string())).also { println("JcLog: BackendResult -> $it") }
    false -> Result.failure(IOException(this.body?.string() ?: "Some Error parsing response"))
}

fun IOException.toTopicsModel(): Result<List<Topic>> = Result.failure(this)

fun parseTopics(json: String?): List<Topic> = json?.let {
    val topicsJsonArray: JSONArray = JSONObject(it).getJSONObject("topic_list").getJSONArray("topics")
    (0 until topicsJsonArray.length()).map { index ->
        val topicJsonObject = topicsJsonArray.getJSONObject(index)
        Topic(
            id = topicJsonObject.getInt("id"),
            title = topicJsonObject.getString("title"),
        )
    }
} ?: emptyList<Topic>()
