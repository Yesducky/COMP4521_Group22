package com.example.comp4521_group22

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    private lateinit var rvList: RecyclerView
    private lateinit var listAdapter: ListAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //init todo list
        rvList = findViewById(R.id.TodoList)
        val layoutManager = GridLayoutManager(this, 1)
        rvList.layoutManager = layoutManager

        val t1 = thread {
            //init with local db
            val TodoDAO = TodoDB.getDatabase(this).TodoDAO()
            listAdapter = ListAdapter(TodoDAO.getAll())
            rvList.adapter = listAdapter
            //update from online
            UpdateTodo().updateDatabaseFromOnline(TodoDAO)
        }
        t1.join()
        listAdapter.notifyDataSetChanged()


        //swipe down to refresh
        val swipeRefreshLayout:SwipeRefreshLayout = findViewById(R.id.container)
        swipeRefreshLayout.setOnRefreshListener {
            thread {
                val TodoDAO = TodoDB.getDatabase(this).TodoDAO()
                UpdateTodo().updateDatabaseFromOnline(TodoDAO)
                listAdapter.ls = TodoDAO.getAll()

                Handler(Looper.getMainLooper()).post {
                    listAdapter.notifyDataSetChanged()
                    swipeRefreshLayout.isRefreshing = false
                }
            }
        }

        val btn:FloatingActionButton = findViewById(R.id.addBtn)
        btn.setOnClickListener(){

        }



//        val t1 = thread {
//            val temp = TodoDB.getDatabase(this).TodoDAO().getAll()
//            for (i in temp){
//                val msg = i.id.toString()
//                Log.i(msg,"global_id = ${i.global_id},\n" +
//                        "group = ${i.group},\n" +
//                        "summary = ${i.summary},\n" +
//                        "description = ${i.description},\n" +
//                        "created = ${i.created},\n" +
//                        "deadline = ${i.deadline},\n" +
//                        "progress = ${i.progress},\n" +
//                        "importance = ${i.importance},\n" +
//                        "shared = ${i.shared},\n" +
//                        "finished = ${i.finished}")
//            }
//        }
//        t1.join()





    }

}



