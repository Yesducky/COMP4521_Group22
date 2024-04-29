package com.example.comp4521_group22

import android.content.Intent
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.time.Month
import kotlin.concurrent.thread

class CalendarAdapter(var dates: ArrayList<String>, var month: Int, var year: Int, val TodoDao: TodoDAO): RecyclerView.Adapter<CalendarViewHolder>() {
    val colorList = listOf(
        Color.rgb(153, 255, 102), // green
        Color.rgb(255, 204, 102), //orange
        Color.rgb(255, 102, 102) //red
    )
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.calendar_item, parent, false)
        return CalendarViewHolder(view)
    }
    override fun getItemCount(): Int {
        return dates.size
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.date.text = dates[position]
        if(dates[position].isNotEmpty()){
            thread {
                val datePattern = TodoDao.buildDatePattern(dates[position].toInt(), month, year)
                val todoImportanceList = TodoDao.getImportanceBySpecificDate(datePattern)
                addColorDots(holder.impotance_list, todoImportanceList)
            }.join()
            holder.itemView.setOnClickListener{
                (holder.itemView.context as AppCompatActivity).findViewById<BottomNavigationView>(R.id.bottom_nav).selectedItemId = R.id.menu_list_view
                val newFragment = FragmentList.newInstance(1, dates[position].toInt(), month, year)
                (holder.itemView.context as AppCompatActivity).supportFragmentManager.beginTransaction().apply {
                    replace(R.id.flFragment, newFragment)
                    commit()
                }
            }
        }
    }

    fun addColorDots(textView: TextView, todoImportanceList: List<Int>) {
        val spannableStringBuilder = SpannableStringBuilder()

        todoImportanceList.forEachIndexed { _, i ->
            if (i in colorList.indices) {
                spannableStringBuilder.append("•")

                val start = spannableStringBuilder.length - 1
                val end = spannableStringBuilder.length
                //color
                spannableStringBuilder.setSpan(
                    ForegroundColorSpan(colorList[i]),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                //text size
                spannableStringBuilder.setSpan(
                    RelativeSizeSpan(1.5f),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
        textView.text = spannableStringBuilder
    }

}