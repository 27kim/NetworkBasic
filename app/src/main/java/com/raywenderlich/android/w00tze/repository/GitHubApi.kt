package com.raywenderlich.android.w00tze.repository

import com.raywenderlich.android.w00tze.model.Gist
import com.raywenderlich.android.w00tze.model.GistRequest
import com.raywenderlich.android.w00tze.model.Repo
import com.raywenderlich.android.w00tze.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface GitHubApi {
    @GET("users/{user}/repos")
    fun getRepos(@Path("user") user: String): Call<List<Repo>>

    @GET("users/{user}/gists")
    fun getGists(@Path("user") user: String): Call<List<Gist>>

    @GET("users/{user}")
    fun getUser(@Path("user") user: String): Call<User>

    @POST("gists")
    fun postGist(@Body body: GistRequest): Call<Gist>
}