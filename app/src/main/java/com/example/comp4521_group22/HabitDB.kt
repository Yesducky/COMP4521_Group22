package com.example.comp4521_group22

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

// Declare the database, note the inclusion of Habit.class and version number.
@Database(entities = [Habit::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class HabitDB : RoomDatabase() {
    // Abstract method to access the DAO
    abstract fun habitDao(): HabitDAO

    companion object {
        // Volatile variable to ensure atomic access to the instance
        @Volatile
        private var INSTANCE: HabitDB? = null

        // Singleton pattern to ensure only one database instance is created
        fun getDatabase(context: MainActivity): HabitDB {
            // If an instance already exists, return it, otherwise create a new one
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HabitDB::class.java,
                    "HabitDB" // Database name
                ).fallbackToDestructiveMigration() // Handle migrations
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
