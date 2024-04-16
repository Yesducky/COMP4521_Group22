package com.example.comp4521_group22

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "Todo")
data class Todo(
    @PrimaryKey(autoGenerate = true)
    val id:Int = 0,

    @ColumnInfo(name = "global_id")
    val global_id:Int,

    @ColumnInfo(name = "group")
    val group:String?,

    @ColumnInfo(name = "summary")
    val summary:String?,

    @ColumnInfo(name = "description")
    val description:String?,

    @ColumnInfo(name = "created")
    val created:String?,

    @ColumnInfo(name = "deadline")
    val deadline:String?,

    @ColumnInfo(name = "progress")
    val progress:String?,

    @ColumnInfo(name = "importance")
    val importance:Int,

    @ColumnInfo(name = "shared")
    val shared:Boolean,

    @ColumnInfo(name = "finished")
    val finished:Boolean,

    )
