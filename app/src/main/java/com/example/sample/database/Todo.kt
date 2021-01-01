package com.example.sample.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_table")
data class Todo(
        @PrimaryKey(autoGenerate = true)
        var todoId: Long = 0L,

        @ColumnInfo(name = "text")
        var text: String = "",

        @ColumnInfo(name = "isCompleted")
        var isCompleted: Boolean = false,
)
