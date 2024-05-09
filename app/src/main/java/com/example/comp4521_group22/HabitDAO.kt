package com.example.comp4521_group22

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHabit(habit: Habit): Long

    @Update
    fun updateHabit(habit: Habit)

    @Delete
    fun deleteHabit(habit: Habit)

    @Query("SELECT * FROM Habit")
    fun getAllHabits(): MutableList<Habit>

    @Query("SELECT * FROM Habit WHERE global_id = :habitId")
    fun getHabitById(habitId: Int): Habit

    @Query("SELECT * FROM Habit WHERE finished = :status")
    fun getHabitsByStatus(status: Boolean): List<Habit>

    @Query("DELETE FROM Habit")
    fun deleteAllHabits()

    @Query("DELETE FROM Habit WHERE global_id = :habitId")
    fun deleteAHabit(habitId: Int)

    @Query("SELECT * FROM Habit WHERE frequency = :frequency")
    fun getHabitsByFrequency(frequency: Int): List<Habit>

    @Query("SELECT * FROM Habit WHERE created BETWEEN :startDate AND :endDate")
    fun getHabitsByCreationDateRange(startDate: String, endDate: String): List<Habit>
    
}
