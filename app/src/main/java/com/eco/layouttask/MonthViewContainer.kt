package com.eco.layouttask

import android.view.View
import com.eco.layouttask.databinding.CalendarHeaderBinding
import com.kizitonwose.calendar.view.ViewContainer

class MonthViewContainer(view: View) : ViewContainer(view) {
    val legendLayout = CalendarHeaderBinding.bind(view).legendLayout.root
}