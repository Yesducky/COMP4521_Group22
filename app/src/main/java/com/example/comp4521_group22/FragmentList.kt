package com.example.comp4521_group22

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.withContext
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch


class FragmentList : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var rvList: RecyclerView
    private lateinit var listAdapter: ListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        val addBtn = view.findViewById<FloatingActionButton>(R.id.fragment_list_add_button)
        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.container)
        val TodoDAO = TodoDB.getDatabase(MainActivity()).TodoDAO()
        val tvDate = view.findViewById<TextView>(R.id.fragment_list_date)

        //get the requested date
        //Usage: click on a date in calendar view, it transacts to here with dates provided
        //mode: 0 <- all, 1 <- specific date
        val mode = arguments?.getInt("mode", 0) ?: 0
        val date = arguments?.getInt("date", 0) ?: 0
        val month = arguments?.getInt("month", 0) ?: 0
        val year = arguments?.getInt("year", 0) ?: 0


        //set header date string
        rvList = view.findViewById(R.id.fragment_list_recycler_view)
        val layoutManager = GridLayoutManager(context, 1)
        rvList.layoutManager = layoutManager
        var datePattern = ""
        if(mode == 1){
            datePattern = TodoDAO.buildDatePattern(date,month,year)
            tvDate.text = "$date/$month/$year"
        }

        //init list
        CoroutineScope(Main).launch {
            swipeRefreshLayout.isRefreshing = true
            withContext(IO) {
                if(mode == 0){
                    listAdapter = ListAdapter(TodoDAO.getAll())
                }
                else if (mode == 1){
                    listAdapter = ListAdapter(TodoDAO.getBySpecificDate(datePattern))
                }
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