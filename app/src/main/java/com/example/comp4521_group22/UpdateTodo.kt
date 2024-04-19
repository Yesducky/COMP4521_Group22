package com.example.comp4521_group22

import android.os.AsyncTask
import android.util.Log
import com.google.gson.Gson
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class UpdateTodo {
    fun updateDatabaseFromOnline(TodoDAO: TodoDAO){
        val credentials = Credentials.basic("leohong", "92816881")
        lateinit var items: List<Todo>
        val t1 = thread {
                val JsonString = getJsonData("http://yesducky.com/api/todo/", credentials)
                items = Gson().fromJson(JsonString, Array<Todo>::class.java).toList()

                TodoDAO.deleteAll()
                for(i in items){
                    TodoDAO.insert(i)
            }
        }
        t1.join()

    }

    private fun getJsonData(url: String, credentials: String): String {
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

        val request: Request = Request.Builder()
            .url(url)
            .header("Authorization", credentials)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            return response.body!!.string()
        }
    }
}