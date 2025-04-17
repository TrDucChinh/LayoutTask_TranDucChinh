package com.eco.layouttask

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.eco.layouttask.data.Note
import com.eco.layouttask.databinding.ActivityMainBinding
import com.eco.layouttask.utils.displayText
import com.eco.layouttask.utils.setTextColorRes
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.DaySize
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private var selectedDate: LocalDate? = null
    private val noteViewModel by lazy {
        ViewModelProvider(this)[NoteViewModel::class.java]
    }
    private var notesForDays: List<Note> = emptyList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyInsets()
        selectedDate = LocalDate.now()

        setupCalendar()
        setupHeader()
        setupDayBinder()
        swipeToNextMonth()
        swipeToPreviousMonth()
        setUpNextPreviousMonth()
        lifecycleScope.launch {
            noteViewModel.allNotes.collect { notes ->
                notesForDays = notes
                binding.Calendar.notifyCalendarChanged()
            }
        }
    }

    private fun applyInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom + imeHeight
            )
            insets
        }
    }

    private fun setupCalendar() {
        val daysOfWeek = daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY)
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(200)
        val endMonth = currentMonth.plusMonths(200)

        binding.Calendar.apply {
            setup(startMonth, endMonth, daysOfWeek.first())
            scrollToMonth(currentMonth)
            outDateStyle = OutDateStyle.EndOfRow
            daySize = DaySize.SeventhWidth
            notifyDateChanged(selectedDate!!)
        }

        binding.Calendar.monthScrollListener = { month ->
            binding.MonthYearText.text = month.yearMonth.displayText()
            selectedDate?.let(binding.Calendar::notifyDateChanged)
        }
    }

    private fun setupHeader() {
        val daysOfWeek = daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY)
        val typeFace = Typeface.create("sans-serif-light", Typeface.NORMAL)

        binding.Calendar.monthHeaderBinder =
            object : MonthHeaderFooterBinder<MonthViewContainer> {
                override fun create(view: View) = MonthViewContainer(view)

                override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                    if (container.legendLayout.tag == null) {
                        container.legendLayout.tag = true
                        container.legendLayout.children.map { it as TextView }
                            .forEachIndexed { index, tv ->
                                tv.text = daysOfWeek[index].displayText(uppercase = false)
                                tv.setTextColorRes(if (index == 6) R.color.red else R.color.black)
                                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                                tv.typeface = typeFace
                            }
                    }
                }
            }
    }

    private fun setUpNextPreviousMonth() {
        binding.NextMonthImage.setOnClickListener {
            binding.Calendar.findFirstVisibleMonth()?.let {
                binding.Calendar.smoothScrollToMonth(it.yearMonth.plusMonths(1))
            }
        }
        binding.PreviousMonthImage.setOnClickListener {
            binding.Calendar.findFirstVisibleMonth()?.let {
                binding.Calendar.smoothScrollToMonth(it.yearMonth.minusMonths(1))
            }
        }
    }

    private fun setupDayBinder() {
        binding.Calendar.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View): DayViewContainer {
                return DayViewContainer(
                    view = view,
                    onDateSelected = { newDate ->
                        val oldDate = selectedDate
                        selectedDate = newDate
                        binding.Calendar.notifyDateChanged(newDate)
                        oldDate?.let { binding.Calendar.notifyDateChanged(it) }
                    },
                    getSelectedDate = { selectedDate }
                )
            }

            @SuppressLint("SetTextI18n")
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                val textView = container.binding.DayText
                val circle = container.binding.circleView
                val imgNote = container.binding.imgNote
                val dayView = container.view

                val date = data.date
                val isSelected = selectedDate == date
                val hasNote = notesForDays.any { it.date == date }
                textView.text = date.dayOfMonth.toString()

                when (data.position) {
                    DayPosition.MonthDate -> {
                        textView.setTextColorRes(R.color.black)
                        when {
                            hasNote -> {
                                imgNote.setImageResource(R.drawable.flower_svgrepo_com)
                                imgNote.visibility = View.VISIBLE
                            }
                            isSelected -> {
                                imgNote.setImageResource(R.drawable.ic_add)
                                imgNote.visibility = View.VISIBLE
                            }
                            else -> {
                                imgNote.visibility = View.GONE
                            }
                        }

                        dayView.setOnClickListener {
                            if (selectedDate == data.date) {
                                showQuickNoteBottomSheet(data.date)
                            } else {
                                val oldSelectedDate = selectedDate
                                selectedDate = data.date
                                binding.Calendar.notifyDateChanged(oldSelectedDate!!)
                                binding.Calendar.notifyDateChanged(data.date)
                            }
                        }


                        if (isSelected) {
                            circle.setBackgroundResource(R.drawable.circle_selected)
                            textView.setBackgroundResource(R.drawable.bg_pill)
                        } else {
                            circle.setBackgroundResource(R.drawable.circle_default)
                            textView.setBackgroundResource(0)
                        }
                    }

                    else -> {
                        textView.setTextColorRes(R.color.colorAccent)
                        if (hasNote) {
                            imgNote.setImageResource(R.drawable.flower_svgrepo_com)
                            imgNote.visibility = View.VISIBLE
                        } else {
                            imgNote.visibility = View.GONE
                        }
                        dayView.setOnClickListener(null)
                        circle.setBackgroundResource(R.drawable.circle_default)
                        textView.setBackgroundResource(0)
                    }
                }
            }

        }
    }

    private fun swipeToPreviousMonth() {
        binding.Calendar.findFirstVisibleMonth()?.let {
            binding.Calendar.smoothScrollToMonth(it.yearMonth.minusMonths(1))
        }
    }

    private fun swipeToNextMonth() {
        binding.Calendar.findFirstVisibleMonth()?.let {
            binding.Calendar.smoothScrollToMonth(it.yearMonth.plusMonths(1))
        }
    }

    private fun showQuickNoteBottomSheet(date: LocalDate) {
        NoteBottomSheet(date) { selectedDate, content ->
            noteViewModel.saveNote(selectedDate, content)
        }.show(supportFragmentManager, "NoteBottomSheet")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
