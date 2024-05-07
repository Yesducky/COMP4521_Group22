package com.example.comp4521_group22

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
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

class EditHabit : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_habit)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val ID = intent.getIntExtra("habitID", -1)

        val backBtn = findViewById<TextView>(R.id.edit_habit_btnBack)
        backBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        val habitID = findViewById<TextView>(R.id.edit_habit_id)
        habitID.text = ID.toString()

        val groupdata = "1"
        val habitsummary = findViewById<EditText>(R.id.edit_habit_summary)
        val descriptionData = findViewById<EditText>(R.id.edit_habit_description)
        val frequency = findViewById<EditText>(R.id.edit_habit_frequency)
        val progress = findViewById<EditText>(R.id.edit_habit_progress)
        val putbutton = findViewById<Button>(R.id.edit_habit_submit)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val monTog = findViewById<CheckBox>(R.id.edit_checkbox_mon)
        val tueTog = findViewById<CheckBox>(R.id.edit_checkbox_tue)
        val wedTog = findViewById<CheckBox>(R.id.edit_checkbox_wed)
        val thurTog = findViewById<CheckBox>(R.id.edit_checkbox_thur)
        val friTog = findViewById<CheckBox>(R.id.edit_checkbox_fri)
        val satTog = findViewById<CheckBox>(R.id.edit_checkbox_sat)
        val sunTog = findViewById<CheckBox>(R.id.edit_checkbox_sun)
        val btnLeft = findViewById<ImageButton>(R.id.edit_habit_btn_left)
        val btnRight = findViewById<ImageButton>(R.id.edit_habit_btn_right)
        val deleteButton = findViewById<Button>(R.id.edit_habit_delete)

        val btnLeftProgress = findViewById<ImageButton>(R.id.edit_habit_btn_left_p)
        val btnRightProgress = findViewById<ImageButton>(R.id.edit_habit_btn_right_p)
        var daysStates = mutableListOf(1, 1, 1, 1, 1, 1, 1)

        //get data from local room db
        Handler(Looper.getMainLooper()).post {
            val HabitDao = HabitDB.getDatabase(MainActivity()).habitDao()
            lateinit var CurrentHabitItem: Habit
            thread { CurrentHabitItem = HabitDao.getHabitById(ID) }.join()

            habitsummary.setText(CurrentHabitItem.summary)
            descriptionData.setText(if (CurrentHabitItem.description != "null") CurrentHabitItem.description else "")

            progress.setText(CurrentHabitItem.progress.toString())
            frequency.setText(CurrentHabitItem.frequency.toString())

            daysStates = CurrentHabitItem.interval?.removeSurrounding("[", "]")
                ?.split(",")?.map { it.trim().toInt() }?.toMutableList()!!

            val lsOfCheckboxes = listOf(monTog,tueTog,wedTog,thurTog,friTog,satTog,sunTog)
            for (i in 0..6){
                lsOfCheckboxes[i].isChecked = daysStates[i] == 1
            }
        }


        btnLeft.setOnClickListener() {
            frequency.setText((frequency.text.toString().toInt() - 1).toString())
        }

        btnRight.setOnClickListener() {
            frequency.setText((frequency.text.toString().toInt() + 1).toString())
        }

        btnLeftProgress.setOnClickListener() {
            progress.setText((progress.text.toString().toInt() - 1).toString())
        }

        btnRightProgress.setOnClickListener() {
            progress.setText((progress.text.toString().toInt() + 1).toString())
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


        putbutton.setOnClickListener {
            //check summary if empty
            var summary: String = ""
            val interval_days = daysStates.toString()
            Log.e("msg", daysStates.toString())
            if (habitsummary.text.toString().trim().isNotEmpty()) {
                summary = habitsummary.text.toString()
            } else {
                Toast.makeText(this, "Todo Summary is required", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (frequency.text.toString().trim().isEmpty()) {
                Toast.makeText(this, "Habit Frequency is required", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            //loading effect
            putbutton.visibility = View.GONE
            progressBar.visibility = View.VISIBLE

            //data collection
            val description: String = if (descriptionData.text.toString().trim()
                    .isNotEmpty()
            ) descriptionData.text.toString() else "null"
            val myformat = "yyyy-MM-dd"
            val sdf = SimpleDateFormat(myformat, Locale.CHINA)
            val created = sdf.format(Date())
            val habitItem = Habit(
                global_id = ID,
                group = groupdata,
                summary = summary,
                description = description,
                interval = interval_days,
                finished = false,
                created = created,
                progress = progress.text.toString().toInt(),
                importance = 0,
                shared = false,
                frequency = frequency.text.toString().toInt()
            )

            Handler(Looper.getMainLooper()).post {
                val habitDao = HabitDB.getDatabase(MainActivity()).habitDao()
                thread {
                    habitDao.deleteAHabit(ID)
                    habitDao.insertHabit(habitItem)
                }.join()  // Ensure the database operation completes

                // Save to remote server
                thread {
                    //upload data
                    UpdateHabit().putHabit("http://yesducky.com/api/habit/" + "$ID"+"/", habitItem)

                    // Navigate back to the main activity
                    Handler(Looper.getMainLooper()).post {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()  // Close this activity
                    }
                }
            }

        }

        deleteButton.setOnClickListener{
            //loading effect
            putbutton.visibility = View.GONE
            deleteButton.visibility = View.GONE
            progressBar.visibility = View.VISIBLE

            //send delete request to the server
            thread {
                UpdateHabit().deleteHabit("http://yesducky.com/api/habit/" + "$ID"+"/")

                Handler(Looper.getMainLooper()).post {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        }
    }
}