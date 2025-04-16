package com.eco.layouttask.utils

import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate): String = date.toString()

    @TypeConverter
    fun toLocalDate(date: String): LocalDate = LocalDate.parse(date)
}
