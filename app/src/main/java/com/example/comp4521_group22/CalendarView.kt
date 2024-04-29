package com.example.comp4521_group22

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.abs
import kotlin.properties.Delegates


class CalendarView : AppCompatActivity() {

    private lateinit var rvCalendar: RecyclerView
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var dates:ArrayList<String>
    private var month by Delegates.notNull<Int>()
    private var year by Delegates.notNull<Int>()
    private lateinit var cur_month_date:LocalDate
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var tv_month_year:TextView
    private lateinit var gestureDetector: GestureDetector

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n", "ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_calendar_view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val addBtn = findViewById<FloatingActionButton>(R.id.activity_calendar_add_button)
        swipeRefreshLayout = findViewById(R.id.container)
        val prev_month_btn = findViewById<TextView>(R.id.prev_month)
        val next_month_btn = findViewById<TextView>(R.id.next_month)
        val calendar_bottom_button = findViewById<ImageButton>(R.id.activity_calendar_bottom_button)
        tv_month_year = findViewById<TextView>(R.id.monthYearTV)
        rvCalendar = findViewById(R.id.activity_calendar_recyclerView)

        //add new Todo button
        addBtn.setOnClickListener{
            val intent = Intent(this, InputTodo::class.java)
            intent.putExtra("from", "calendar");
            startActivity(intent)
        }

        Handler(Looper.getMainLooper()).post {
            swipeRefreshLayout.isRefreshing = true
            cur_month_date = LocalDate.now()
            dates = datesArray(cur_month_date)
            month = cur_month_date.month.value
            year = cur_month_date.year

            rvCalendar.layoutManager = GridLayoutManager(this, 7)
            calendarAdapter = CalendarAdapter(dates, month, year, TodoDB.getDatabase(MainActivity()).TodoDAO())
            rvCalendar.adapter = calendarAdapter
            calendarAdapter.notifyDataSetChanged()
            tv_month_year.text = "${cur_month_date.month} ${cur_month_date.year}"
            swipeRefreshLayout.isRefreshing = false
        }

        //swipe down to refresh
        swipeRefreshLayout.setOnRefreshListener {
            calendarAdapter.notifyDataSetChanged()
            swipeRefreshLayout.isRefreshing = false
        }

        //prev month
        prev_month_btn.setOnClickListener{
            cur_month_date = cur_month_date.minusMonths(1)
            updateData()
        }

        //next month
        next_month_btn.setOnClickListener{
            cur_month_date = cur_month_date.plusMonths(1)
            updateData()
        }

        //intent to main
        calendar_bottom_button.setOnClickListener{
            startActivity(Intent(this,MainActivity::class.java))
        }

        //swipe to calendar view
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (e1 != null) {
                    if (e2.x - e1.x > 100 && abs(velocityX) > 200) {
                        // Left swipe detected
                        val intent = Intent(this@CalendarView, TodoListView::class.java)
                        val animation = ActivityOptions.makeCustomAnimation(this@CalendarView, R.anim.slide_in_left, R.anim.slide_out_right)
                        startActivity(intent,animation.toBundle())
                    }
                }
                return super.onFling(e1, e2, velocityX, velocityY)
            }
        })
        rvCalendar.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event) }
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateData(){
        dates = datesArray(cur_month_date)
        month = cur_month_date.month.value
        year = cur_month_date.year
        calendarAdapter.dates = datesArray(cur_month_date)
        calendarAdapter.month = month
        calendarAdapter.year = year
        tv_month_year.text = "${cur_month_date.month} ${cur_month_date.year}"
        calendarAdapter.notifyDataSetChanged()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun datesArray(date: LocalDate): ArrayList<String> {
        val daysInMonthArray = ArrayList<String>()
        val yearMonth = YearMonth.from(date)
        val daysInMonth = yearMonth.lengthOfMonth()
        val firstOfMonth = cur_month_date.withDayOfMonth(1)
        val dayOfWeek = firstOfMonth.dayOfWeek.value
        for (i in 1..42) {
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add("")
            } else {
                daysInMonthArray.add((i - dayOfWeek).toString())
            }
        }
        return daysInMonthArray
    }

}