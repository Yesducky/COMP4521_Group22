package com.example.comp4521_group22

import android.util.Log
import com.google.gson.Gson
import okhttp3.*
import java.util.concurrent.TimeUnit

class UpdateHabit {
    private val credentials = Credentials.basic("leohong", "92816881")

    fun getHabits(habitDao: HabitDAO): Boolean {
        lateinit var items: List<Habit>
        var jsonString = ""

        val thread = Thread {
            jsonString = getJsonData("http://yesducky.com/api/habit/", credentials)

            if (jsonString.isNotEmpty()) {
                items = Gson().fromJson(jsonString, Array<Habit>::class.java).toList()
                habitDao.deleteAllHabits()
                items.forEach {
                    habitDao.insertHabit(it)
                }
            }
        }
        thread.start()
        thread.join()

        return jsonString != "Error"
    }

    private fun getJsonData(url: String, credentials: String): String {
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val request = Request.Builder()
            .url(url)
            .header("Authorization", credentials)
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) "" else response.body!!.string()
            }
        } catch (t: Throwable) {
            Log.e("TAG", "Caught unexpected Throwable: ${t.message}", t)
            "Error"
        }
    }

    fun postHabit(url: String, item: Habit): Boolean {
        return sendHabitData("POST", url, item)
    }

    fun putHabit(url: String, item: Habit): Boolean {
        return sendHabitData("PUT", url, item)
    }

    private fun sendHabitData(method: String, url: String, item: Habit): Boolean {
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val requestBody = FormBody.Builder()
            .add("global_id", item.global_id.toString())
            .add("group", item.group.orEmpty())
            .add("summary", item.summary.orEmpty())
            .add("description", item.description.orEmpty())
            .add("created", item.created.orEmpty())
            .add("interval", item.interval.joinToString(","))
            .add("frequency", item.frequency.toString())
            .add("progress", item.progress.orEmpty())
            .add("shared", item.shared.toString())
            .add("finished", item.finished.toString())
            .build()

        val request = Request.Builder()
            .url(url)
            .header("Authorization", credentials)
            .method(method, requestBody)
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                response.body!!.string()
                true
            }
        } catch (t: Throwable) {
            Log.e("TAG", "Caught unexpected Throwable: ${t.message}", t)
            false
        }
    }

    fun deleteHabit(url: String): Boolean {
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val request = Request.Builder()
            .url(url)
            .header("Authorization", credentials)
            .delete()
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                response.body!!.string()
                true
            }
        } catch (t: Throwable) {
            Log.e("TAG", "Caught unexpected Throwable: ${t.message}", t)
            false
        }
    }
}
