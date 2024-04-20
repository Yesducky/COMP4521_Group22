package com.example.comp4521_group22

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ListAdapter(var ls: List<Todo>): RecyclerView.Adapter<ListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_item, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return ls.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.id.text = ls[position].id.toString()
        holder.summary.text = ls[position].summary
        holder.deadline.text = ls[position].deadline

        when(ls[position].importance){
            2 -> holder.deadline.setBackgroundColor(Color.rgb(255, 184, 184))
            1 -> holder.deadline.setBackgroundColor(Color.rgb(250, 255, 203))
            0 -> holder.deadline.setBackgroundColor(Color.rgb(184, 255, 203))
        }
        
    }


}