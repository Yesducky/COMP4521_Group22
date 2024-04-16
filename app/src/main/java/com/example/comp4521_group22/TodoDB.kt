package com.example.comp4521_group22

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Todo::class], version=1)
abstract class TodoDB: RoomDatabase(){
    abstract fun TodoDAO(): TodoDAO
}