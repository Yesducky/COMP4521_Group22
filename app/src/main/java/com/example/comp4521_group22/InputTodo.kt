package com.example.comp4521_group22

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
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
        val backBtn = findViewById<TextView>(R.id.edit_habit_btnBack)
        backBtn.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }

        val habitbtn = findViewById<TextView>(R.id.btnHabit)
        habitbtn.setOnClickListener{
            startActivity(Intent(this, InputHabit::class.java))
        }


        //form area
        val groupdata = "1"

        val summaryData = findViewById<EditText>(R.id.todo_summary)
        val descriptionData = findViewById<EditText>(R.id.todo_description)
        val deadlinebutton = findViewById<ImageButton>(R.id.todo_datepickerbutton)
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

        //set default deadline if enter this page from calendar mode
        val mode = intent.getIntExtra("inputCalendarMode", 0)
        if(mode == 1){
            val date = intent.getIntExtra("date",0)
            val month = intent.getIntExtra("month", 0)
            val year = intent.getIntExtra("year", 0)
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE)
            val calendar = Calendar.getInstance()
            calendar.set(year, month - 1, date)
            deadlinedata = sdf.format(calendar.time)
            tvdeadline.text = deadlinedata
        }

        //set datepicker for deadline
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
        deadlinebutton.setOnClickListener {
            DatePickerDialog(
                this,
                listener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        //radio group for importance
        importance_group.setOnCheckedChangeListener(){ _, checkedID->
            when(checkedID){
                importance_group_0.id -> importance_data = 0
                importance_group_1.id -> importance_data = 1
                importance_group_2.id -> importance_data = 2
            }

        }

        //post button
        postbutton.setOnClickListener{

            //check summary if empty
            var summary:String = ""
            if(summaryData.text.toString().trim().isNotEmpty()){
                summary = summaryData.text.toString()
            } else {
                Toast.makeText(this, "Todo Summary is required", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            //loading effect
            postbutton.visibility = View.GONE
            progressBar.visibility = View.VISIBLE

            //data collection
            val description:String = if(descriptionData.text.toString().trim().isNotEmpty()) descriptionData.text.toString() else "null"
            val progress:String = if(progressData.text.toString().trim().isNotEmpty()) progressData.text.toString() else "null"
            val myformat = "yyyy-MM-dd"
            val sdf = SimpleDateFormat(myformat, Locale.CHINA)
            val created = sdf.format(Date())
            val newTodoItem = Todo(
                global_id = 1,
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

            //save in local database
            Handler(Looper.getMainLooper()).post {
                if (deadlinedata == ""){newTodoItem.deadline = null}
                val TodoDAO = TodoDB.getDatabase(MainActivity()).TodoDAO()
                thread{TodoDAO.insert(newTodoItem)}.join()
            }

            //save in remote server
            thread {
                if (deadlinedata == ""){newTodoItem.deadline = ""}
                UpdateTodo().postTodo("http://yesducky.com/api/todo/",  newTodoItem)

                Handler(Looper.getMainLooper()).post {
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
        }
    }
}

