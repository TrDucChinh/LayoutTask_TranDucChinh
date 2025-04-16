package com.eco.layouttask.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey val date: LocalDate,
    val content: String
)
