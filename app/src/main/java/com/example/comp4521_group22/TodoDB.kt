package com.example.comp4521_group22

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Todo::class], version=1)
abstract class TodoDB: RoomDatabase(){
    abstract fun TodoDAO(): TodoDAO

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: TodoDB? = null

        fun getDatabase(context: MainActivity): TodoDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TodoDB::class.java,
                    "TodoDB"
                ).build()
                INSTANCE = instance
                instance
            }
        }


    }
}

