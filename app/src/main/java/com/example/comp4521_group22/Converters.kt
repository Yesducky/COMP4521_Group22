package com.example.comp4521_group22

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromInterval(value: String?): List<Int>? {
        if (value == null) {
            return listOf()
        }
        val type = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun intervalToString(list: List<Int>?): String? {
        return gson.toJson(list)
    }
}
