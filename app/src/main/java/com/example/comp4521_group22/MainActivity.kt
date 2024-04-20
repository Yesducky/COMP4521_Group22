package com.example.comp4521_group22

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    private lateinit var rvList: RecyclerView
    private lateinit var listAdapter: ListAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e("p","main")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val addBtn:FloatingActionButton = findViewById(R.id.addBtn)
        val swipeRefreshLayout:SwipeRefreshLayout = findViewById(R.id.container)

        //init todo list
        rvList = findViewById(R.id.TodoList)
        val layoutManager = GridLayoutManager(this, 1)
        rvList.layoutManager = layoutManager

        //init with local db
        thread {
            val TodoDAO = TodoDB.getDatabase(this).TodoDAO()
            listAdapter = ListAdapter(TodoDAO.getAll())
            rvList.adapter = listAdapter
            listAdapter.notifyDataSetChanged()
        }.join()

        //initial update from the remote
        thread {
            swipeRefreshLayout.isRefreshing = true
            val TodoDAO = TodoDB.getDatabase(this).TodoDAO()
            UpdateTodo().updateDatabaseFromOnline(TodoDAO)
            listAdapter.ls = TodoDAO.getAll()
            Handler(Looper.getMainLooper()).post {
                listAdapter.notifyDataSetChanged()
                swipeRefreshLayout.isRefreshing = false
            }
        }

        //swipe down to refresh
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


        addBtn.setOnClickListener{
            startActivity(Intent(this, InputTodo::class.java))
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



