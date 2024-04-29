package com.example.comp4521_group22

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
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


class FragmentCalendar : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var rvCalendar: RecyclerView
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var dates:ArrayList<String>
    private var month by Delegates.notNull<Int>()
    private var year by Delegates.notNull<Int>()
    private lateinit var cur_month_date:LocalDate
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var tv_month_year:TextView
    private lateinit var gestureDetector: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)

        val addBtn = view.findViewById<FloatingActionButton>(R.id.fragment_calendar_add_button)
        val prev_month_btn = view.findViewById<TextView>(R.id.prev_month)
        val next_month_btn = view.findViewById<TextView>(R.id.next_month)

        tv_month_year = view.findViewById<TextView>(R.id.monthYearTV)
        rvCalendar = view.findViewById(R.id.fragment_calendar_recyclerView)
        swipeRefreshLayout = view.findViewById(R.id.container)

        //add new Todo button
        addBtn.setOnClickListener{
            val intent = Intent(activity, InputTodo::class.java)
            intent.putExtra("from", "calendar");
            startActivity(intent)
        }

        Handler(Looper.getMainLooper()).post {
            swipeRefreshLayout.isRefreshing = true
            cur_month_date = LocalDate.now()
            dates = datesArray(cur_month_date)
            month = cur_month_date.month.value
            year = cur_month_date.year

            rvCalendar.layoutManager = GridLayoutManager(context, 7)
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

        return view
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

    companion object {

        @JvmStatic
        fun newInstance() =
            FragmentCalendar().apply {
                arguments = Bundle().apply {
                }
            }
    }
}