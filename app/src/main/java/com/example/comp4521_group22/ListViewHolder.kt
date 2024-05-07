package com.example.comp4521_group22

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val id:TextView = itemView.findViewById(R.id.todo_item_id)
    val summary:TextView = itemView.findViewById(R.id.todo_item_summary)
    val deadline:TextView = itemView.findViewById(R.id.todo_item_deadline)
}