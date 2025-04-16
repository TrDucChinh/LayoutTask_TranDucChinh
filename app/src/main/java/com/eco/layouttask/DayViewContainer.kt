package com.eco.layouttask

import android.view.View
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.view.ViewContainer
import com.eco.layouttask.databinding.CalendarDayBinding
import java.time.LocalDate

class DayViewContainer(
    view: View,
    private val onDateSelected: (LocalDate) -> Unit,
    private val getSelectedDate: () -> LocalDate?
) : ViewContainer(view) {

    lateinit var day: CalendarDay
    val binding = CalendarDayBinding.bind(view)

    init {
        view.setOnClickListener {
            if (day.position == DayPosition.MonthDate) {
                if (getSelectedDate() != day.date) {
                    onDateSelected(day.date)
                }
            }
        }
    }
}
