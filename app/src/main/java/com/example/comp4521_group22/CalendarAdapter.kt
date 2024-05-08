package com.example.comp4521_group22

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlin.concurrent.thread

class CalendarAdapter(var dates: ArrayList<String>, var month: Int, var year: Int, val TodoDao: TodoDAO): RecyclerView.Adapter<CalendarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.calendar_item, parent, false)
        return CalendarViewHolder(view)
    }
    override fun getItemCount(): Int {
        return dates.size
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val colorList = listOf(
            ContextCompat.getColor(holder.itemView.context, R.color.dark_green),
            ContextCompat.getColor(holder.itemView.context, R.color.dark_orange),
            ContextCompat.getColor(holder.itemView.context, R.color.dark_red),
        )

        holder.date.text = dates[position]
        if(dates[position].isEmpty()){
            holder.itemView.background = null
        }

        if(dates[position].isNotEmpty()){
            thread {
                val datePattern = TodoDao.buildDatePattern(dates[position].toInt(), month, year)
                val todoImportanceList = TodoDao.getImportanceBySpecificDate(datePattern)
                addColorDots(holder.impotance_list, todoImportanceList, colorList)
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

    fun addColorDots(textView: TextView, todoImportanceList: List<Int>, colorList: List<Int>) {
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