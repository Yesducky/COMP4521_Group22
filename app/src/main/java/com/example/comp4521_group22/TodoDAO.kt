package com.example.comp4521_group22

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDAO {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun InsertAll(vararg todo: Todo)

    @Update()
    fun update(todo: Todo)

    @Delete
    fun delete(todo: Todo)

    @Query("SELECT * FROM Todo ORDER BY deadline ASC")
    fun getAll(): List<Todo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(todo: Todo): Long

    @Query("DELETE FROM Todo")
    fun deleteAll()

    @Query("SELECT MAX(global_id) From Todo")
    fun getMaxGlobalID(): Int

    @Query("SELECT * FROM todo WHERE global_id=:globalId ")
    fun get(globalId:Int):Todo

    @Query("DELETE FROM todo WHERE global_id = :globalId")
    fun deleteATodo(globalId: Int)

    @Query("SELECT importance FROM Todo WHERE deadline LIKE :datePattern ORDER BY importance DESC")
    fun getImportanceBySpecificDate(datePattern: String): List<Int>

    @Query("SELECT * FROM Todo WHERE deadline LIKE :datePattern OR deadline IS NULL ORDER BY importance DESC")
    fun getBySpecificDateAndNull(datePattern: String): List<Todo>

    @Query("SELECT * FROM Todo WHERE deadline LIKE :datePattern ORDER BY importance DESC")
    fun getBySpecificDate(datePattern: String): List<Todo>

    fun buildDatePattern(day: Int, month: Int, year: Int): String{
        val formattedDay = if (day < 10) "0$day" else "$day"
        val formattedMonth = if (month < 10) "0$month" else "$month"
        return "%$year-$formattedMonth-$formattedDay%"
    }

}