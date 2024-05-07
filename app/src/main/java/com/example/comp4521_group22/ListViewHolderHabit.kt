package com.example.comp4521_group22

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
class ListViewHolderHabit(itemView: View): RecyclerView.ViewHolder(itemView) {
    val list_item_habit_title: TextView = itemView.findViewById(R.id.list_item_habit_title)
    val list_item_habit_progress: TextView = itemView.findViewById(R.id.list_item_habit_progress)
}