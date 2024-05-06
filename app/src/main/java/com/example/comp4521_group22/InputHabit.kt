package com.example.comp4521_group22

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.annotation.RequiresApi
import kotlin.concurrent.thread
import androidx.activity.enableEdgeToEdge


class InputHabit : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_input_habit)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val from: String? = intent.getStringExtra("from")
        if (from != null) {
            Log.e("msg", from)
        }
        val backBtn = findViewById<TextView>(R.id.btnBack)
        backBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }


        val groupdata = "1"


        val habitsummary = findViewById<EditText>(R.id.habit_summary)
        val descriptionData = findViewById<EditText>(R.id.habit_description)
        val deadlinebutton = findViewById<ImageButton>(R.id.habit_datepickerbutton)
        val tvdeadline = findViewById<TextView>(R.id.habit_deadline_datepicker_tv)
        val progressData = findViewById<EditText>(R.id.habit_progress)
        val postbutton = findViewById<Button>(R.id.habit_submit)
        var freq = findViewById<EditText>(R.id.habit_frequency)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val monTog = findViewById<CheckBox>(R.id.checkbox_mon)
        val tueTog = findViewById<CheckBox>(R.id.checkbox_tue)
        val wedTog = findViewById<CheckBox>(R.id.checkbox_wed)
        val thurTog = findViewById<CheckBox>(R.id.checkbox_thur)
        val friTog = findViewById<CheckBox>(R.id.checkbox_fri)
        val satTog = findViewById<CheckBox>(R.id.checkbox_sat)
        val sunTog = findViewById<CheckBox>(R.id.checkbox_sun)


        val daysStates = mutableListOf(0, 0, 0, 0, 0, 0, 0)


        var deadlinedata: String = ""

        val mode = intent.getIntExtra("inputCalendarMode", 0)
        if (mode == 1) {
            val date = intent.getIntExtra("date", 0)
            val month = intent.getIntExtra("month", 0)
            val year = intent.getIntExtra("year", 0)
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE)
            val calendar = Calendar.getInstance()
            calendar.set(year, month - 1, date)
            deadlinedata = sdf.format(calendar.time)
            tvdeadline.text = deadlinedata
        }

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

        monTog.setOnCheckedChangeListener { _, isChecked ->
            daysStates[0] = if (isChecked) 1 else 0
        }
        tueTog.setOnCheckedChangeListener { _, isChecked ->
            daysStates[1] = if (isChecked) 1 else 0
        }
        wedTog.setOnCheckedChangeListener { _, isChecked ->
            daysStates[2] = if (isChecked) 1 else 0
        }
        thurTog.setOnCheckedChangeListener { _, isChecked ->
            daysStates[3] = if (isChecked) 1 else 0
        }
        friTog.setOnCheckedChangeListener { _, isChecked ->
            daysStates[4] = if (isChecked) 1 else 0
        }
        satTog.setOnCheckedChangeListener { _, isChecked ->
            daysStates[5] = if (isChecked) 1 else 0
        }
        sunTog.setOnCheckedChangeListener { _, isChecked ->
            daysStates[6] = if (isChecked) 1 else 0
        }




        postbutton.setOnClickListener {

            //check summary if empty
            var summary: String = ""
            var freqType: String = ""
            if (habitsummary.text.toString().trim().isNotEmpty()) {
                summary = habitsummary.text.toString()
            } else {
                Toast.makeText(this, "Todo Summary is required", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (freq.text.toString().trim().isNotEmpty()) {
                freqType = habitsummary.text.toString()
            } else {
                Toast.makeText(this, "Habit Frequency is required", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            //loading effect
            postbutton.visibility = View.GONE
            progressBar.visibility = View.VISIBLE

            //data collection
            val description: String = if (descriptionData.text.toString().trim()
                    .isNotEmpty()
            ) descriptionData.text.toString() else "null"
            val progress: String = if (progressData.text.toString().trim()
                    .isNotEmpty()
            ) progressData.text.toString() else "null"
            val myformat = "yyyy-MM-dd"
            val sdf = SimpleDateFormat(myformat, Locale.CHINA)
            val created = sdf.format(Date())
            val newHabitItem = Habit(
                global_id = 1,
                group = groupdata,
                summary = summary,
                description = description,
                interval = daysStates,
                finished = false,
                created = created,
                progress = progress,
                shared = false,
                frequency = freqType
            )






            Handler(Looper.getMainLooper()).post {
                val habitDao = HabitDB.getDatabase(MainActivity()).habitDao()
                thread {
                    habitDao.insertHabit(newHabitItem)
                }.join()  // Ensure the database operation completes

                // Save to remote server
                thread {
                    val updateHabit = UpdateHabit()
                    updateHabit.postHabit("http://yesducky.com/api/habit/", newHabitItem)

                    // Navigate back to the main activity
                    Handler(Looper.getMainLooper()).post {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()  // Close this activity
                    }
                }

            }

        }

    }
}
