package com.example.comp4521_group22

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CalendarViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val date = itemView.findViewById<TextView>(R.id.date)
    val impotance_list = itemView.findViewById<TextView>(R.id.importance)
}