package com.example.comp4521_group22

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.concurrent.thread
import kotlin.properties.Delegates

class EditTodo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_todo)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val ID = intent.getIntExtra("todoId", -1)

        val edit_todo_id = findViewById<TextView>(R.id.edit_todo_id)
        edit_todo_id.text = ID.toString()

        val groupdata = "1"
        var importance_data by Delegates.notNull<Int>()
        val edit_todo_summary = findViewById<TextView>(R.id.edit_todo_summary)
        val edit_todo_description = findViewById<TextView>(R.id.edit_todo_description)
        val edit_todo_deadline = findViewById<TextView>(R.id.edit_todo_deadline_datepicker_tv)
        val edit_todo_deadline_datepicker =
            findViewById<ImageButton>(R.id.edit_todo_datepickerbutton)
        val edit_todo_progress = findViewById<TextView>(R.id.edit_todo_progress)
        val edit_todo_importance = findViewById<RadioGroup>(R.id.edit_todo_importance)
        val edit_todo_importance_0 = findViewById<RadioButton>(R.id.edit_todo_importance_0)
        val edit_todo_importance_1 = findViewById<RadioButton>(R.id.edit_todo_importance_1)
        val edit_todo_importance_2 = findViewById<RadioButton>(R.id.edit_todo_importance_2)

        val edit_todo_delete = findViewById<Button>(R.id.edit_todo_delete)
        val edit_todo_save = findViewById<Button>(R.id.edit_todo_submit)
        val edit_todo_progressBar = findViewById<ProgressBar>(R.id.edit_todo_progressBar)

        val edit_todo_back_button = findViewById<TextView>(R.id.edit_todo_btnBack)

        var deadlinedata: String? = ""
        val dateformat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(dateformat, Locale.CHINA)

        //get data from local room db
        Handler(Looper.getMainLooper()).post {
            val TodoDAO = TodoDB.getDatabase(MainActivity()).TodoDAO()
            lateinit var Current_Todo_Item: Todo
            thread { Current_Todo_Item = TodoDAO.get(ID) }.join()

            edit_todo_summary.text = Current_Todo_Item.summary
            edit_todo_description.text = if (Current_Todo_Item.description != "null") Current_Todo_Item.description else ""
            deadlinedata = Current_Todo_Item.deadline?.let { sdf.parse(it) }?.let { sdf.format(it) }
            edit_todo_deadline.text = deadlinedata
            edit_todo_progress.text = if (Current_Todo_Item.progress != "null") Current_Todo_Item.progress else ""
            importance_data = Current_Todo_Item.importance
            when (importance_data) {
                0 -> edit_todo_importance_0.isChecked = true
                1 -> edit_todo_importance_1.isChecked = true
                2 -> edit_todo_importance_2.isChecked = true
            }

        }

        //set datepicker for deadline
        val calendar = Calendar.getInstance()
        val listener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
                calendar.set(year, month, day)
                val myformat = "yyyy-MM-dd"
                val sdf = SimpleDateFormat(myformat, Locale.CHINA)
                deadlinedata = sdf.format(calendar.time)
                edit_todo_deadline.text = deadlinedata
            }
        }
        edit_todo_deadline_datepicker.setOnClickListener {
            DatePickerDialog(
                this,
                listener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        //Radio group for importance
        edit_todo_importance.setOnCheckedChangeListener() { _, checkedID ->
            when (checkedID) {
                edit_todo_importance_0.id -> importance_data = 0
                edit_todo_importance_1.id -> importance_data = 1
                edit_todo_importance_2.id -> importance_data = 2
            }
        }

        //Save the edited data
        edit_todo_save.setOnClickListener {
            //retrieve summary and check if empty
            var summary: String = ""
            if (edit_todo_summary.text.toString().trim().isNotEmpty()) {
                summary = edit_todo_summary.text.toString()
            } else {
                Toast.makeText(this, "Todo Summary is required", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            //loading effect
            edit_todo_save.visibility = View.GONE
            edit_todo_delete.visibility = View.GONE
            edit_todo_progressBar.visibility = View.VISIBLE

            //retrieve other data and submit
            thread {
                val description: String = if (edit_todo_description.text.toString().trim().isNotEmpty()) edit_todo_description.text.toString() else "null"
                val progress: String = if (edit_todo_progress.text.toString().trim().isNotEmpty()) edit_todo_progress.text.toString() else "null"
                val myformat = "yyyy-MM-dd"
                val sdf = SimpleDateFormat(myformat, Locale.CHINA)
                val created = sdf.format(Date())
                if (edit_todo_deadline == null) { deadlinedata = "" }
                if(deadlinedata == null){ deadlinedata = "" }

                val updatedTodo = Todo(
                    global_id = 0,
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

                //upload data
                UpdateTodo().putTodo("http://yesducky.com/api/todo/" + "$ID"+"/", updatedTodo)

                //back to the main activity
                Handler(Looper.getMainLooper()).post {
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
        }

        edit_todo_delete.setOnClickListener {

            //loading effect
            edit_todo_save.visibility = View.GONE
            edit_todo_delete.visibility = View.GONE
            edit_todo_progressBar.visibility = View.VISIBLE

            //send delete request to the server
            thread {
                UpdateTodo().deleteTodo("http://yesducky.com/api/todo/" + "$ID"+"/")

                Handler(Looper.getMainLooper()).post {
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
        }

        //back to the main activity
        edit_todo_back_button.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}