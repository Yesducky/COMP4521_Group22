package com.example.comp4521_group22

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        thread { TodoDB.getDatabase(this) }.join()
        val intent = Intent(this, TodoListView::class.java)
        startActivity(intent)
        finish()
    }

}



