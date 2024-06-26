package com.example.comp4521_group22

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.withContext
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.concurrent.thread


class FragmentList : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var rvList: RecyclerView
    private lateinit var listAdapter: ListAdapter
    private lateinit var rvListHabit: RecyclerView
    private lateinit var ListHabitAdapter: ListAdapterHabit
    private lateinit var checkAllBtn: LinearLayout
    private lateinit var HabitDAO: HabitDAO
    private lateinit var TodoDAO: TodoDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint( "SetTextI18n", "CutPasteId", "NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        val addBtn = view.findViewById<FloatingActionButton>(R.id.fragment_list_add_button)
        checkAllBtn = view.findViewById(R.id.btn_check_all)
        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.container)
        TodoDAO = TodoDB.getDatabase(MainActivity()).TodoDAO()
        HabitDAO = HabitDB.getDatabase(MainActivity()).habitDao()
        val tvDate = view.findViewById<TextView>(R.id.fragment_list_date)

        //get the requested date
        //Usage: click on a date in calendar view, it transacts to here with dates provided
        //mode: 0 <- all, 1 <- specific date
        val currentDate = LocalDate.now()
        val mode = arguments?.getInt("mode", 0) ?: 0
        var date = arguments?.getInt("date", 0) ?: 0
        var month = arguments?.getInt("month", 0) ?: 0
        var year = arguments?.getInt("year", 0) ?: 0
        if(mode == 0){
            date = currentDate.dayOfMonth
            month = currentDate.monthValue
            year = currentDate.year
            checkAllBtn.visibility = View.VISIBLE
        }

        //set header date string
        rvList = view.findViewById(R.id.fragment_list_recycler_view)
        rvListHabit = view.findViewById(R.id.fragment_list_habit_recycler_view)
        val layoutManager = GridLayoutManager(context, 1)
        val layoutManager_habit = GridLayoutManager(context, 2)
        rvList.layoutManager = layoutManager
        rvListHabit.layoutManager = layoutManager_habit

        val datePattern: String = TodoDAO.buildDatePattern(date,month,year)
        tvDate.text = if(mode == 1) "$date/$month/$year" else "TODAY"

        lateinit var habitlist_all:MutableList<Habit>
        var habitlist_today:MutableList<Habit> = mutableListOf()

        //init list
        CoroutineScope(Main).launch {
            swipeRefreshLayout.isRefreshing = true
            checkAllBtn.visibility = View.GONE
            withContext(IO) {
                habitlist_all = HabitDAO.getAllHabits()
                for (i in habitlist_all){
                    val interval = i.interval?.removeSurrounding("[", "]")
                        ?.split(",")?.map { it.trim().toInt() }?.toMutableList()!!
                    if(interval[currentDate.dayOfWeek.value-1] == 1){
                        habitlist_today.add(i)
                    }
                }
                ListHabitAdapter = ListAdapterHabit(habitlist_today)
                listAdapter = if(mode == 0) ListAdapter(TodoDAO.getBySpecificDateAndNull(datePattern)) else ListAdapter(TodoDAO.getBySpecificDate(datePattern))
            }
            if(mode == 1){
                view.findViewById<RecyclerView>(R.id.fragment_list_habit_recycler_view).visibility = View.GONE
            }
            rvList.adapter = listAdapter
            rvListHabit.adapter = ListHabitAdapter

            withContext(IO) {
                if (UpdateTodo().getTodo(TodoDAO)) {
                    if(mode == 0){
                        listAdapter.ls = TodoDAO.getBySpecificDateAndNull(datePattern)
                    }else{
                        listAdapter.ls = TodoDAO.getBySpecificDate(datePattern)
                    }
                }
                if (UpdateHabit().getHabit(HabitDAO)){
                    habitlist_all = HabitDAO.getAllHabits()
                    habitlist_today = mutableListOf()
                    for (i in habitlist_all){
                        val interval = i.interval?.removeSurrounding("[", "]")
                            ?.split(",")?.map { it.trim().toInt() }?.toMutableList()!!
                        if(interval[currentDate.dayOfWeek.value-1] == 1){
                            habitlist_today.add(i)
                        }
                    }
                    ListHabitAdapter.ls = habitlist_today
                }
            }
            listAdapter.notifyDataSetChanged()
            ListHabitAdapter.notifyDataSetChanged()
            swipeRefreshLayout.isRefreshing = false
            checkAllBtn.visibility = View.VISIBLE

        }

        //swipe down to refresh
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = true
            CoroutineScope(Main).launch {
                withContext(IO) {
                    habitlist_all = HabitDAO.getAllHabits()
                    habitlist_today = mutableListOf()
                    for (i in habitlist_all){
                        val interval = i.interval?.removeSurrounding("[", "]")
                            ?.split(",")?.map { it.trim().toInt() }?.toMutableList()!!
                        if(interval[currentDate.dayOfWeek.value-1] == 1){
                            habitlist_today.add(i)
                        }
                    }
                    ListHabitAdapter.ls = habitlist_today
                }
                ListHabitAdapter.notifyDataSetChanged()

                val updatedList = withContext(IO) {
                    UpdateTodo().getTodo(TodoDAO)
                    if(mode == 0){
                        if (checkAllBtn.visibility == View.GONE){
                            TodoDAO.getAll()
                        }else{
                            TodoDAO.getBySpecificDateAndNull(datePattern)
                        }
                    }else{
                        TodoDAO.getBySpecificDate(datePattern)
                    }
                }
                val la =ListAdapter(updatedList)
                rvList.adapter = la
                la.notifyDataSetChanged()

                swipeRefreshLayout.isRefreshing = false
            }
        }

        //add button
        addBtn.setOnClickListener {
            val intent = Intent(activity, InputTodo::class.java)
            if(mode == 1){
                intent.putExtra("inputCalendarMode", 1)
                intent.putExtra("date", date)
                intent.putExtra("month", month)
                intent.putExtra("year", year)
            }
            activity?.startActivity(intent)
        }


        //check all
        checkAllBtn.setOnClickListener{
            CoroutineScope(Main).launch {
                val updatedList = withContext(IO) {
                    TodoDAO.getAll()
                }
                habitlist_all = withContext(IO) {
                    HabitDAO.getAllHabits()
                }
                val la =ListAdapter(updatedList)
                ListHabitAdapter.ls = habitlist_all
                rvList.adapter = la
                ListHabitAdapter.notifyDataSetChanged()
                la.notifyDataSetChanged()
                tvDate.text = "ALL"
                checkAllBtn.visibility = View.GONE
            }
        }

        return view
    }

    companion object {
        fun newInstance(mode: Int, date: Int = 0, month: Int = 0, year: Int = 0): FragmentList {
            val fragment = FragmentList()
            val args = Bundle()
            args.putInt("mode", mode)
            args.putInt("date", date)
            args.putInt("month", month)
            args.putInt("year", year)
            fragment.arguments = args
            return fragment
        }
    }
}