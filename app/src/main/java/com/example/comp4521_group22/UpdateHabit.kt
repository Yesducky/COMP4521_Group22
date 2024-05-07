package com.example.comp4521_group22

import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import okhttp3.Credentials
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class UpdateHabit {
    //only 1 user allowed which is me, acc my name, pwd my tel no.
    private val credentials = Credentials.basic("leohong", "92816881")

    //get all the habit elements from online
    fun getHabit(HabitDao: HabitDAO):Boolean{
        lateinit var items: List<Habit>
        var JsonString: String = ""
        val t1 = thread {
            JsonString = getJsonData("http://yesducky.com/api/habit/", credentials)

            if (JsonString != ""){
                items = Gson().fromJson(JsonString, Array<Habit>::class.java).toList()

                HabitDao.deleteAllHabits()
                for(i in items){
                    HabitDao.insertHabit(i)
                }
            }
        }
        t1.join()
        if(JsonString == "Error"){
            return false
        }
        return true
    }

    //helper function to retrieve all elements from server db
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

        var r = ""
        try {
            client.newCall(request).execute().use { response ->
                r = if (!response.isSuccessful) {
                    ""
                } else{
                    response.body!!.string()
                }
            }
        }catch (t: Throwable){
            Log.e("TAG", "Caught unexpected Throwable: ${t.message}", t)
        }
        return r
    }

    //post a habit item
    fun postHabit(url: String, item: Habit):Boolean {
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val postbody: RequestBody = FormBody.Builder()
            .add("group", item.group.toString())
            .add("summary", item.summary.toString())
            .add("description", item.description.toString())
            .add("created", item.created.toString())
            .add("interval", item.interval.toString())
            .add("frequency", item.frequency.toString())
            .add("progress", item.progress.toString())
            .add("importance", item.importance.toString())
            .add("shared", item.shared.toString())
            .add("finished", item.finished.toString())
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .header("Authorization", credentials)
            .post(postbody)
            .build()

        var connection_success: Boolean = true
        try {
            client.newCall(request).execute().use { response ->
                response.body!!.string()
            }
        }catch (t: Throwable){
            Log.e("TAG", "Caught unexpected Throwable: ${t.message}", t)
            connection_success = false
        }
        return connection_success
    }

    //change a habit item
    fun putHabit(url: String, item: Habit):Boolean {
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val putBody: RequestBody = FormBody.Builder()
            .add("group", item.group.toString())
            .add("summary", item.summary.toString())
            .add("description", item.description.toString())
            .add("created", item.created.toString())
            .add("interval", item.interval.toString())
            .add("frequency", item.frequency.toString())
            .add("progress", item.progress.toString())
            .add("importance", item.importance.toString())
            .add("shared", item.shared.toString())
            .add("finished", item.finished.toString())
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .header("Authorization", credentials)
            .put(putBody)
            .build()

        var connection_success = true
        try {
            client.newCall(request).execute().use { response ->
                response.body!!.string()
                Log.e("msg", response.toString())
            }
        }catch (t: Throwable){

            Log.e("TAG", "Caught unexpected Throwable: ${t.message}", t)
            connection_success = false
        }
        return connection_success
    }

    fun deleteHabit(url: String):Boolean {
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()


        val request: Request = Request.Builder()
            .url(url)
            .header("Authorization", credentials)
            .delete()
            .build()

        var connection_success = true
        try {
            client.newCall(request).execute().use { response ->
                response.body!!.string()
            }
        }catch (t: Throwable){
//            Toast.makeText(MainActivity(), t.message, Toast.LENGTH_LONG).show()
            connection_success = false
        }
        return connection_success
    }
}