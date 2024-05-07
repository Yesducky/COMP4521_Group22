package com.example.comp4521_group22

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListAdapterHabit(var ls: MutableList<Habit>): RecyclerView.Adapter<ListViewHolderHabit>() {
    val colorList = listOf(
        Color.rgb(0, 94, 82), // green
        Color.rgb(204, 102, 0), //orange
        Color.rgb(62, 0, 0) //red
    )
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolderHabit {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.habit_item, parent, false)
        return ListViewHolderHabit(view)
    }

    override fun getItemCount(): Int {
        return ls.size
    }

    override fun onBindViewHolder(holder: ListViewHolderHabit, position: Int) {
        holder.list_item_habit_title.text = ls[position].summary.toString()
        changeTitleColor(holder, position)

        val str: String = ls[position].progress.toString()+"/"+ls[position].frequency.toString()
        holder.list_item_habit_progress.text = str

        holder.itemView.setOnClickListener{
            val str2: String = (ls[position].progress+1).toString()+"/"+ls[position].frequency.toString()
            holder.list_item_habit_progress.text = str2
            var color_int: Int = 0
            val result:Double = (ls[position].progress+1).toDouble()/ls[position].frequency
            if(result<0.5){
                color_int = 2
            }
            else if (result < 1){
                color_int = 1
            }
            holder.list_item_habit_title.setBackgroundColor(colorList[color_int])

            val updatedHabit = ls[position].copy(progress = ls[position].progress + 1)
            ls.set(position, updatedHabit)

            CoroutineScope(Dispatchers.Main).launch {
                val HabitDao = HabitDB.getDatabase(MainActivity()).habitDao()
                withContext(Dispatchers.IO){
                    HabitDao.updateHabit(updatedHabit)
                    UpdateHabit().putHabit("http://yesducky.com/api/habit/" + ls[position].global_id.toString()+"/", updatedHabit)
                }
            }

        }

        holder.itemView.setOnLongClickListener{
            // Long click action
            // Start intent
            val intent = Intent(holder.itemView.context, EditHabit::class.java)
            intent.putExtra("habitID", ls[position].global_id)
            ContextCompat.startActivity(holder.itemView.context, intent, null)
            true
        }
    }

    fun changeTitleColor(holder: ListViewHolderHabit, position: Int){
        var color_int: Int = 0
        val result:Double = ls[position].progress.toDouble()/ls[position].frequency
        if(result<0.5){
            color_int = 2
        }
        else if (result < 1){
            color_int = 1
        }
        holder.list_item_habit_title.setBackgroundColor(colorList[color_int])
    }
}