package com.example.comp4521_group22

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale


class ListAdapter(var ls: List<Todo>): RecyclerView.Adapter<ListViewHolder>() {
    val dateformat = "yyyy-MM-dd"
    val sdf = SimpleDateFormat(dateformat, Locale.CHINA)
    val colorList = listOf(
        Color.rgb(0, 94, 82), // green
        Color.rgb(204, 102, 0), //orange
        Color.rgb(62, 0, 0) //red
    )

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

        val dateformat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(dateformat, Locale.CHINA)
        val a = ls[position].deadline?.let { sdf.parse(it) }
        val dateformat2 = "d MMM"
        val sdf2 = SimpleDateFormat(dateformat2, Locale.UK)
        val b = a?.let { sdf2.format(it) }
        holder.deadline.text = b

        var importance_color = ls[position].importance
        if(importance_color>2) importance_color = 0
        holder.deadline.setBackgroundColor(colorList[importance_color])

        holder.itemView.setOnClickListener{
            val intent = Intent(holder.itemView.context, EditTodo::class.java)
            intent.putExtra("todoId", ls[position].global_id)
            startActivity(holder.itemView.context, intent, null)
        }
    }

}