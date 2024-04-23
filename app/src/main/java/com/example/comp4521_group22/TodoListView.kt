package com.example.comp4521_group22

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.concurrent.thread


class TodoListView : AppCompatActivity() {

    private lateinit var rvList: RecyclerView
    private lateinit var listAdapter: ListAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_list_view)

        val addBtn = findViewById<FloatingActionButton>(R.id.activity_main_add_button)
        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.container)
        val activity_main_bottom_button = findViewById<TextView>(R.id.activity_main_bottom_button)
        val TodoDAO = TodoDB.getDatabase(MainActivity()).TodoDAO()
        val mode = intent.getIntExtra("mode",0)

        //init todo list
        rvList = findViewById(R.id.activity_main_todo_list)
        val layoutManager = GridLayoutManager(this, 1)
        rvList.layoutManager = layoutManager
        val date = intent.getIntExtra("date",1)
        val month = intent.getIntExtra("month", 1)
        val year = intent.getIntExtra("year", 1)
        var datePattern = ""
        var title = "List"
        if(mode == 1){
            datePattern = TodoDAO.buildDatePattern(date,month,year)
            title = "$date/$month/$year"
        }

        CoroutineScope(Main).launch {
            swipeRefreshLayout.isRefreshing = true
            withContext(IO) {
                if(mode == 0){
                    listAdapter = ListAdapter(TodoDAO.getAll())
                }
                else if (mode == 1){
                    listAdapter = ListAdapter(TodoDAO.getBySpecificDate(datePattern))
                }
                activity_main_bottom_button.text = title
            }
            rvList.adapter = listAdapter
            withContext(IO) {
                if(mode == 0){
                    if (UpdateTodo().getTodo(TodoDAO)) {
                        UpdateTodo().getTodo(TodoDAO)
                        listAdapter.ls = TodoDAO.getAll()
                    }
                }
            }
            listAdapter.notifyDataSetChanged()
            swipeRefreshLayout.isRefreshing = false
        }



        //swipe down to refresh
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = true
            CoroutineScope(Main).launch {
                withContext(IO) {
                    UpdateTodo().getTodo(TodoDAO)
                    if(mode == 0){
                        listAdapter.ls = TodoDAO.getAll()
                    }
                    else if (mode == 1){
                        listAdapter.ls = TodoDAO.getBySpecificDate(datePattern)

                    }
                }
                listAdapter.notifyDataSetChanged()
                swipeRefreshLayout.isRefreshing = false
            }
        }

        //add new Todo button
        addBtn.setOnClickListener {
            val intent = Intent(this, InputTodo::class.java)
            intent.putExtra("inputCalendarMode", 1)
            intent.putExtra("date", date)
            intent.putExtra("month", month)
            intent.putExtra("year", year)
            ContextCompat.startActivity(this, intent, null)
        }

        //temporary use: Intent to calendar view
        activity_main_bottom_button.setOnClickListener {
            startActivity(Intent(this, CalendarView::class.java))
        }

            //debug
//            val t1 = thread {
//                val TodoDAO = TodoDB.getDatabase(MainActivity()).TodoDAO()
//                val temp = TodoDAO.getAll()
//                for (i in temp){
//                    val msg = i.id.toString()
//                    Log.i(msg,"global_id = ${i.global_id},\n" +
//                            "group = ${i.group},\n" +
//                            "summary = ${i.summary},\n" +
//                            "description = ${i.description},\n" +
//                            "created = ${i.created},\n" +
//                            "deadline = ${i.deadline},\n" +
//                            "progress = ${i.progress},\n" +
//                            "importance = ${i.importance},\n" +
//                            "shared = ${i.shared},\n" +
//                            "finished = ${i.finished}")
//                }
//                val datePattern = TodoDAO.buildDatePattern(25, 5, 2024)

//                val m = TodoDAO.getBySpecificDate(datePattern)
//                for (i in m){
//                    Log.i("msg", i.toString())
//                }
//                for (i in m){
//                    val msg = i.id.toString()
//                    Log.i(msg,"global_id = ${i.global_id},\n" +
//                            "group = ${i.group},\n" +
//                            "summary = ${i.summary},\n" +
//                            "description = ${i.description},\n" +
//                            "created = ${i.created},\n" +
//                            "deadline = ${i.deadline},\n" +
//                            "progress = ${i.progress},\n" +
//                            "importance = ${i.importance},\n" +
//                            "shared = ${i.shared},\n" +
//                            "finished = ${i.finished}")
//                }
//            }
//            t1.join()
//        }

    }

}



