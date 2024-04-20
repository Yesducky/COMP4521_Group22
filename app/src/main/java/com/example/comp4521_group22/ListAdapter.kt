package com.example.comp4521_group22

import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import java.text.SimpleDateFormat
import java.util.Locale

class ListAdapter(var ls: List<Todo>): RecyclerView.Adapter<ListViewHolder>() {
    val dateformat = "yyyy-MM-dd"
    val sdf = SimpleDateFormat(dateformat, Locale.CHINA)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_item, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return ls.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.id.text = ls[position].global_id.toString()
        holder.summary.text = ls[position].summary
        holder.deadline.text = ls[position].deadline?.let { sdf.parse(it) }?.let { sdf.format(it) }


        when(ls[position].importance){
            2 -> holder.deadline.setBackgroundColor(Color.rgb(255, 184, 184))
            1 -> holder.deadline.setBackgroundColor(Color.rgb(250, 255, 203))
            0 -> holder.deadline.setBackgroundColor(Color.rgb(184, 255, 203))
        }
        holder.itemView.setOnClickListener{
            val intent = Intent(holder.itemView.context, EditTodo::class.java)
            intent.putExtra("todoId", ls[position].global_id)
            startActivity(holder.itemView.context, intent, null)
        }

        
    }



}