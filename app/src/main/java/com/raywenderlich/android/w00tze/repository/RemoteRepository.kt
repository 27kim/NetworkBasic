/*
 * Copyright (c) 2018 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package com.raywenderlich.android.w00tze.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import com.raywenderlich.android.w00tze.app.Constants.fullUrlString
import com.raywenderlich.android.w00tze.app.Injection
import com.raywenderlich.android.w00tze.app.isNullorBlankorNullString
import com.raywenderlich.android.w00tze.model.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

object RemoteRepository : Repository {

    //    private const val LOGIN = "27kim123"
    private const val TAG = "RemoteRepository"

    private val LOGIN = AuthenticationPrefs.getUsername()

    private val api = Injection.provideGitHubApi()

    override fun getRepos(): LiveData<Either<List<Repo>>> {
        val liveData = MutableLiveData<Either<List<Repo>>>()

        //1. AsyncTask 로 호출
//        FetchReposAsyncTask({ repos ->
//            liveData.value = repos
//        }).execute()

        //2. FetchAsync 로 통합
//        FetchAsyncTask("/users/${LOGIN}/repos", ::parseRepos ,{ repos ->
//                        liveData.value = repos
//        }).execute()

        //3.Retrofit 사용
        api.getRepos(LOGIN).enqueue(object : Callback<List<Repo>> {
            override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>?) {
              if(response != null){
                  if(response !=null  && response.isSuccessful) {
                      liveData.value = Either.success(response.body())
                  }else{
                      liveData.value = Either.error(ApiError.REPO, null)
                  }
              }
            }

            override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
                liveData.value = Either.error(ApiError.REPO, null)
            }

        })
        return liveData
    }

    override fun getGists(): LiveData<Either<List<Gist>>> {
        val liveData = MutableLiveData<Either<List<Gist>>>()

        //3.Retrofit 사용
        api.getGists(LOGIN).enqueue(object : Callback<List<Gist>> {
            override fun onResponse(call: Call<List<Gist>>, response: Response<List<Gist>>) {
                if(response !=null  && response.isSuccessful) {
                    liveData.value = Either.success(response.body())
                }else{
                    liveData.value = Either.error(ApiError.GIST, null)
                }
            }

            override fun onFailure(call: Call<List<Gist>>, t: Throwable) {
                liveData.value = Either.error(ApiError.GIST, null)
            }

        })

        //1. FetchAsyncTask 로 통합
//        FetchAsyncTask("/users/${LOGIN}/gists", ::parseGists ,{ gists ->
//            liveData.value = gists
//        }).execute()
//        return liveData

        //1. AsyncTask 로 호출
//        FetchGistsAsyncTask({ gists ->
//            liveData.value = gists
//        }).execute()

        return liveData
    }

    override fun getUser(): LiveData<Either<User>> {
        val liveData = MutableLiveData<Either<User>>()

        //3.Retrofit 사용
        api.getUser(LOGIN).enqueue(object : Callback<User>{
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if(response !=null  && response.isSuccessful) {
                    liveData.value = Either.success(response.body())
                }else{
                    liveData.value = Either.error(ApiError.USER, null)
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
//        //1. AsyncTask 로 호출
//        FetchUserAsyncTask { liveData.value = it }.execute()
//
//        //2. FetchAsync로 통합
//        FetchAsyncTask("/users/${LOGIN}", ::parseUser ,{ user ->
//            liveData.value = user
//        }).execute()


        return liveData
    }

    private fun fetchRepos(): List<Repo>? {
        try {
            val url = Uri.parse(fullUrlString("/users/${LOGIN}/repos")).toString()
            val jsonString = getUrlAsString(url)

            Log.i(TAG, "Repo data: $jsonString")

//            val repos = mutableListOf<Repo>()
//
//            for (i in 0 until 100) {
//                val repo = Repo("repo name")
//                repos.add(repo)
//            }
//            return repos

            return parseRepos(jsonString)
        } catch (e: IOException) {
            Log.e(TAG, "Error retrieving repos : ${e.localizedMessage}")
        } catch (e: JSONException) {
            Log.e(TAG, "Error retrieving repos : ${e.localizedMessage}")
        }
        return null
    }

    private fun fetchGists(): List<Gist>? {
        try {
            val url = Uri.parse(fullUrlString("/users/${LOGIN}/gists")).toString()
            val jsonString = getUrlAsString(url)

            Log.i(TAG, "Gist data: $jsonString")

            return parseGists(jsonString)
        } catch (e: IOException) {
            Log.e(TAG, "Error retrieving gists : ${e.localizedMessage}")
        }
        return null
    }

    private fun fetchUser(): User? {
        try {
            val url = Uri.parse(fullUrlString("/users/${LOGIN}")).toString()
            val jsonString = getUrlAsString(url)

            Log.i(TAG, "User data: $jsonString")

            return parseUser(jsonString)
        } catch (e: IOException) {
            Log.e(TAG, "Error retrieving User : ${e.localizedMessage}")
        }
        return null
    }


    private fun <T> fetch(path: String, parser: (String) -> T): T? {
        try {
            val url = Uri.parse(fullUrlString(path)).toString()
            val jsonString = getUrlAsString(url)

            Log.i(TAG, "User data: $jsonString")

            return parser(jsonString)
        } catch (e: IOException) {
            Log.e(TAG, "Error retrieving User : ${e.localizedMessage}")
        }
        return null
    }

    private class FetchAsyncTask<T>(val path: String, val parser: (String) -> T, val callback: (T) -> Unit)
        : AsyncTask<(T) -> Unit, Void, T>() {
        override fun doInBackground(vararg p0: ((T) -> Unit)?): T? {
            return fetch(path, parser)
        }

        override fun onPostExecute(result: T) {
            super.onPostExecute(result)

            if(result !=null){
                callback(result)
            }
        }
    }

    private class FetchReposAsyncTask(val callback: ReposCallback) : AsyncTask<ReposCallback, Void, List<Repo>>() {
        override fun doInBackground(vararg params: ReposCallback?): List<Repo>? {
            return fetchRepos()
        }

        override fun onPostExecute(result: List<Repo>?) {
            super.onPostExecute(result)
            if (result != null) {
                callback(result)
            }
        }
    }

    private class FetchGistsAsyncTask(val callback: GistsCallback) : AsyncTask<GistsCallback, Void, List<Gist>>() {
        override fun doInBackground(vararg params: GistsCallback?): List<Gist>? {
            return fetchGists()
        }

        override fun onPostExecute(result: List<Gist>?) {
            super.onPostExecute(result)
            if (result != null) {
                callback(result)
            }
        }
    }

    private class FetchUserAsyncTask(val callback: UserCallback) : AsyncTask<UserCallback, Void, User>() {
        override fun doInBackground(vararg params: UserCallback?): User? {
            return fetchUser()
        }

        override fun onPostExecute(result: User?) {
            super.onPostExecute(result)
            if (result != null) {
                callback(result)
            }
        }
    }

    private fun parseRepos(jsonString: String): List<Repo> {

        val repos = mutableListOf<Repo>()

        val reposArray = JSONArray(jsonString)
        for (i in 0 until reposArray.length()) {
            val repoObject = reposArray.getJSONObject(i)
            val repo = Repo(repoObject.getString("name"))
            repos.add(repo)
        }
        return repos
    }

    private fun parseGists(jsonString: String): List<Gist> {
        val gists = mutableListOf<Gist>()

//        val gistsArray = JSONArray(jsonString)
//
//        for (i in 0 until gistsArray.length()) {
//            val jsonObject = gistsArray.getJSONObject(i)
//            val gist = Gist(jsonObject.getString("created_at"), jsonObject.getString("description"))
//            gists.add(gist)
//        }

        return gists
    }


    private fun parseUser(jsonString: String): User {

        val input = JSONObject(jsonString)

        val id = input.getLong("id")
        val name = if (input.getString("name").isNullorBlankorNullString()) "" else input.getString("name")
        val login = if (input.getString("login").isNullorBlankorNullString()) "" else input.getString("login")
        val company = if (input.getString("company").isNullorBlankorNullString()) "" else input.getString("company")
        val avatar_url = if (input.getString("avatar_url").isNullorBlankorNullString()) "" else input.getString("avatar_url")

        return User(
                id
                , name
                , login
                , company
                , avatar_url
        )
    }

    override fun postGist(request: GistRequest): LiveData<Either<Gist>> {
        val liveData = MutableLiveData<Either<Gist>>()

        api.postGist(request).enqueue(object : Callback<Gist> {
            override fun onResponse(call: Call<Gist>?, response: Response<Gist>?) {
                if (response != null && response.isSuccessful) {
                    liveData.value = Either.success(response.body())
                } else {
                    liveData.value = Either.error(ApiError.POST_GIST, null)
                }
            }

            override fun onFailure(call: Call<Gist>?, t: Throwable?) {
                liveData.value = Either.error(ApiError.POST_GIST, null)
            }
        })

        return liveData
    }
}