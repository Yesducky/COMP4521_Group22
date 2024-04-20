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

    @Update
    fun update(todo: Todo)

    @Delete
    fun delete(todo: Todo)

    @Query("SELECT * FROM Todo ORDER BY id ASC")
    fun getAll(): List<Todo>

    @Insert
    fun insert(todo: Todo): Long

    @Query("DELETE FROM Todo")
    fun deleteAll()

    @Query("SELECT MAX(global_id) From Todo")
    fun getMaxGlobalID(): Int

    @Query("SELECT * FROM todo WHERE global_id=:globalId ")
    fun get(globalId:Int):Todo

}