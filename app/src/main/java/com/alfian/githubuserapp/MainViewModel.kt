package com.alfian.githubuserapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alfian.githubuserapp.entity.User
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONObject

class MainViewModel : ViewModel() {
    private val listUsers = MutableLiveData<ArrayList<User>>()
    private val listUserFollowers = MutableLiveData<ArrayList<User>>()
    private val list = MutableLiveData<User>()
    private var errorApi = MutableLiveData<String>()

    fun setSearchGithub(query: String?) {
        val listItems = ArrayList<User>()
        val client = AsyncHttpClient()
        client.addHeader("Authorization", "token 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")
        client.addHeader("User-Agent", "request")
        val url = "https://api.github.com/search/users?q=$query"
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray
            ) {
                try {
                    val result = String(responseBody)
                    val responseObject = JSONObject(result)
                    val items = responseObject.getJSONArray("items")

                    for (i in 0 until items.length()) {
                        val item = items.getJSONObject(i)
                        val user = User()
                        user.username = item.getString("login")
                        user.id = item.getInt("id")
                        user.avatar = item.getString("avatar_url")
                        user.url = item.getString("html_url")

                        listItems.add(user)
                    }
                    listUsers.postValue(listItems)
                } catch (e: Exception) {
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error?.message}"
                }
                errorApi.postValue(errorMessage)

            }
        })
    }

    fun setDetailUser(username: String?) {
        val client = AsyncHttpClient()
        client.addHeader("Authorization", "token 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")
        client.addHeader("User-Agent", "request")

        val url = "https://api.github.com/users/$username"
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray
            ) {
                val result = String(responseBody)
                try {

                    val user = User()
                    val responseObject = JSONObject(result)
                    user.name = responseObject.getString("name")
                    user.username = responseObject.getString("login")
                    user.follower = responseObject.getInt("followers")
                    user.following = responseObject.getInt("following")
                    user.repository = responseObject.getInt("public_repos")
                    user.location = responseObject.getString("location")
                    user.company = responseObject.getString("company")
                    user.avatar = responseObject.getString("avatar_url")
                    user.url = responseObject.getString("html_url")
                    user.id = responseObject.getInt("id")

                    list.postValue(user)

                } catch (e: Exception) {

                }

            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error?.message}"
                }
                errorApi.postValue(errorMessage)
            }
        })

    }

    fun setFollowersOrFollowing(username: String?, type: String) {
        val listFollowers = ArrayList<User>()
        val client = AsyncHttpClient()
        client.addHeader("Authorization", "token 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")
        client.addHeader("User-Agent", "request")

        var url: String? = null
        when (type) {
            "followers" -> url = "https://api.github.com/users/${username}/followers"
            "following" -> url = "https://api.github.com/users/${username}/following"
        }

        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray
            ) {
                val result = String(responseBody)
                try {

                    val responseArray = JSONArray(result)
                    for (i in 0 until responseArray.length()) {
                        val item = responseArray.getJSONObject(i)
                        val user = User()
                        user.username = item.getString("login")
                        user.id = item.getInt("id")
                        user.avatar = item.getString("avatar_url")
                        user.url = item.getString("html_url")

                        listFollowers.add(user)
                    }
                    listUserFollowers.postValue(listFollowers)
                } catch (e: Exception) {
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error?.message}"
                }
                errorApi.postValue(errorMessage)
            }
        })
    }

    fun getFollowers(): LiveData<ArrayList<User>> {
        return listUserFollowers
    }

    fun getDetail(): LiveData<User> {
        return list
    }

    fun getUsers(): LiveData<ArrayList<User>> {
        return listUsers
    }

    fun getError(): LiveData<String> {
        return errorApi
    }

}