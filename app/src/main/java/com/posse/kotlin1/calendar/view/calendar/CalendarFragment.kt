package com.posse.kotlin1.calendar.view.calendar

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.Completion
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.posse.kotlin1.calendar.databinding.FragmentCalendarBinding
import com.posse.kotlin1.calendar.viewModel.CalendarViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.*

class CalendarFragment : Fragment() {
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CalendarViewModel
    private lateinit var calendarView: CalendarView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFAB()
        setupCalendar()
        setupViewModel()
    }

    private fun setupFAB() {
        val fab: FloatingActionButton = binding.fab
        fab.setOnClickListener {
            calendarView.smoothScrollToMonth(YearMonth.now())
        }
    }

    private fun setupCalendar() {
        calendarView = binding.calendarView
        val currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(10)
        val lastMonth = currentMonth.plusMonths(10)
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        val completion: Completion = { calendarView.scrollToMonth(currentMonth) }
        calendarView.setupAsync(firstMonth, lastMonth, firstDayOfWeek, completion)
        calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                val monthName =
                    getString(Month.values()[month.yearMonth.monthValue - 1].monthResource)
                ("$monthName ${month.year}")
                    .also { container.textView.text = it }
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(CalendarViewModel::class.java)
        viewModel.getLiveData().observe(viewLifecycleOwner, { updateCalendar(it) })
        viewModel.refreshDrankState()
    }

    private fun updateCalendar(calendarState: Map<LocalDate, Boolean>) {
        calendarView.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                val textView = container.textView
                textView.text = day.date.dayOfMonth.toString()
                if (day.owner == DayOwner.THIS_MONTH) {
                    textView.visibility = View.VISIBLE
                    container.view.setOnClickListener {
                        viewModel.dayClicked(day.date)
                        if (calendarState[day.date] == true) {
                            uncheckDay(textView)
                        } else {
                            checkDay(textView)
                        }
                        calendarView.notifyDayChanged(day)
                    }
                    if (calendarState[day.date] == true) {
                        checkDay(textView)
                    } else {
                        uncheckDay(textView)
                    }
                } else {
                    textView.visibility = View.INVISIBLE
                }
            }

            private fun checkDay(textView: TextView) {
                textView.setTextColor(Color.WHITE)
                textView.setBackgroundColor(Color.RED)
            }

            private fun uncheckDay(textView: TextView) {
                textView.setTextColor(Color.BLACK)
                textView.setBackgroundColor(Color.WHITE)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = CalendarFragment()
    }
}