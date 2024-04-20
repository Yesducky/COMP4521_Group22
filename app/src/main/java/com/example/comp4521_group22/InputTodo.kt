package com.example.comp4521_group22

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.concurrent.thread



class InputTodo : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_input_todo)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //nav
        val backBtn = findViewById<Button>(R.id.btnBack)
        backBtn.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }


        //form area
        var groupdata: String = "1"

        val summaryData = findViewById<EditText>(R.id.todo_summary)
        val descriptionData = findViewById<EditText>(R.id.todo_description)
        val deadlinebutton = findViewById<Button>(R.id.todo_datepickerbutton)
        val tvdeadline = findViewById<TextView>(R.id.todo_deadline_datepicker_tv)
        val progressData = findViewById<EditText>(R.id.todo_progress)
        val importance_group  = findViewById<RadioGroup>(R.id.todo_importance)
        val importance_group_0 = findViewById<RadioButton>(R.id.todo_importance_0)
        val importance_group_1 = findViewById<RadioButton>(R.id.todo_importance_1)
        val importance_group_2 = findViewById<RadioButton>(R.id.todo_importance_2)
        var importance_data:Int = 0
        val postbutton = findViewById<Button>(R.id.todo_submit)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        var deadlinedata: String = ""


        val calendar = Calendar.getInstance()
        val listener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
                calendar.set(year, month, day)
                val myformat = "yyyy-MM-dd"
                val sdf = SimpleDateFormat(myformat, Locale.CHINA)
                deadlinedata = sdf.format(calendar.time)
                tvdeadline.text = deadlinedata
            }
        }

        deadlinebutton.setOnClickListener {  //日曆按鈕的點擊事件
            DatePickerDialog(
                this,
                listener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        importance_group.setOnCheckedChangeListener(){group, checkedID->
            if(checkedID == importance_group_0.id){
                importance_data = 0
            }
            else if (checkedID == importance_group_1.id){
                importance_data = 1
            }
            else if (checkedID == importance_group_2.id){
                importance_data = 2
            }
        }
        var global_id: Int = 0
        val TodoDAO = TodoDB.getDatabase(MainActivity()).TodoDAO()
        thread {
            global_id = TodoDAO.getMaxGlobalID()
        }


        postbutton.setOnClickListener{

            var summary:String = ""
            if(summaryData.text.toString().trim().isNotEmpty()){
                summary = summaryData.text.toString()
            } else {
                Toast.makeText(this, "Todo Summary is required", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            postbutton.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            val description:String = if(descriptionData.text.toString().trim().isNotEmpty()) summaryData.text.toString() else "null"
            val progress:String = if(progressData.text.toString().trim().isNotEmpty()) summaryData.text.toString() else "null"


            thread {
                Log.e("p","todo")
                val myformat = "yyyy-MM-dd"
                val sdf = SimpleDateFormat(myformat, Locale.CHINA)
                val created = sdf.format(Date())
                if(deadlinedata == null){
                    deadlinedata = ""
                }

                val newTodoItem = Todo(
                    global_id = global_id,
                    group = groupdata,
                    summary = summary,
                    description = description,
                    deadline = deadlinedata,
                    importance = importance_data,
                    finished = false,
                    created = created,
                    progress = progress,
                    shared = false
                )

                UpdateTodo().postJsonData("http://yesducky.com/api/todo/",  newTodoItem)

                Handler(Looper.getMainLooper()).post {
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }



        }

    }


}

