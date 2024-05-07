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

class UpdateTodo {
    //only 1 user allowed which is me, acc my name, pwd my tel no.
    private val credentials = Credentials.basic("leohong", "92816881")

    //get all the to-do elements from online
    fun getTodo(TodoDAO: TodoDAO):Boolean{
        lateinit var items: List<Todo>
        var JsonString: String = ""
        val t1 = thread {
            JsonString = getJsonData("http://yesducky.com/api/todo/", credentials)

            if (JsonString != ""){
                items = Gson().fromJson(JsonString, Array<Todo>::class.java).toList()
                TodoDAO.deleteAll()
                for(i in items){
                    TodoDAO.insert(i)
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

    //post a todo item
    fun postTodo(url: String, item: Todo):Boolean {
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
            .add("deadline", item.deadline.toString())
            .add("progress",item.progress.toString())
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

    //change a todo item
    fun putTodo(url: String, item: Todo):Boolean {
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val putbody: RequestBody = FormBody.Builder()
            .add("group", item.group.toString())
            .add("summary", item.summary.toString())
            .add("description", item.description.toString())
            .add("deadline", item.deadline.toString())
            .add("progress",item.progress.toString())
            .add("importance", item.importance.toString())
            .add("shared", item.shared.toString())
            .add("finished", item.finished.toString())
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .header("Authorization", credentials)
            .put(putbody)
            .build()

        var connection_success = true
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

    fun deleteTodo(url: String):Boolean {
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