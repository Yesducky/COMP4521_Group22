package com.example.comp4521_group22

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TodoDAO {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun InsertAll(vararg todo: Todo)

    @Update
    fun update(todo: Todo)

    @Delete
    fun delete(todo: Todo)

    @Query("SELECT * FROM Todo")
    fun getAll(): List<Todo>

}