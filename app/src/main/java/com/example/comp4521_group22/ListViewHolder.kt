package com.example.comp4521_group22

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.room.ColumnInfo


class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val id:TextView = itemView.findViewById(R.id.todo_item_id)
    val summary:TextView = itemView.findViewById(R.id.todo_item_summary)

//    val global_id = itemView.findViewById(R.id.global_id)
//    val group = itemView.findViewById(R.id.group)

//    val description = itemView.findViewById(R.id.description)
//    val created = itemView.findViewById(R.id.created)
    val deadline:TextView = itemView.findViewById(R.id.todo_item_deadline)
//    val progress = itemView.findViewById(R.id.progress)
//    val importance = itemView.findViewById(R.id.importance)
//    val shared = itemView.findViewById(R.id.shared)
//    val finished = itemView.findViewById(R.id.finished)
}