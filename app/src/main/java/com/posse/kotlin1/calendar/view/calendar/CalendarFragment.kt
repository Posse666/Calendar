package com.posse.kotlin1.calendar.view.calendar

import android.content.res.TypedArray
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
    private val mBinding get() = _binding!!
    private lateinit var mViewModel: CalendarViewModel
    private lateinit var mCalendarView: CalendarView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mCalendarView = mBinding.calendarView

        mViewModel = ViewModelProvider(this).get(CalendarViewModel::class.java)
        mViewModel.getLiveData().observe(viewLifecycleOwner, { updateCalendar(it) })
        mViewModel.refreshDrankState()

        val fab: FloatingActionButton = mBinding.fab
        fab.setOnClickListener {
            mCalendarView.smoothScrollToMonth(YearMonth.now())
        }
    }

    private fun updateCalendar(calendarState: Set<LocalDate>) {
        mBinding.loadingLayout.visibility = View.VISIBLE
        val currentMonth = YearMonth.now()
        var firstMonth = YearMonth.from(Collections.min(calendarState))
        if (firstMonth.isAfter(currentMonth.minusMonths(12))) {
            firstMonth = currentMonth.minusMonths(12)
        }
        mCalendarView.setupAsync(
            firstMonth,
            currentMonth.plusMonths(1),
            WeekFields.of(Locale.getDefault()).firstDayOfWeek
        ) {
            mCalendarView.monthHeaderBinder =
                object : MonthHeaderFooterBinder<MonthViewContainer> {
                    override fun create(view: View) = MonthViewContainer(view)
                    override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                        val monthName =
                            getString(Month.values()[month.yearMonth.monthValue - 1].monthResource)
                        ("$monthName ${month.year}")
                            .also { container.textView.text = it }
                    }
                }
            mCalendarView.dayBinder = object : DayBinder<DayViewContainer> {
                override fun create(view: View) = DayViewContainer(view)
                override fun bind(container: DayViewContainer, day: CalendarDay) {
                    container.day = day
                    val textView = container.textView
                    textView.text = day.date.dayOfMonth.toString()
                    if (day.owner == DayOwner.THIS_MONTH) {
                        textView.visibility = View.VISIBLE
                        if (day.date.isBefore(LocalDate.now()) || day.date.isEqual(LocalDate.now())) {
                            container.view.setOnClickListener {
                                mViewModel.dayClicked(day.date)
                                changeDay(textView, day)
                                mCalendarView.notifyDayChanged(day)
                            }
                        } else container.view.setOnClickListener(null)
                        changeDay(textView, day)
                    } else {
                        textView.visibility = View.INVISIBLE
                    }
                }

                private fun changeDay(textView: TextView, day: CalendarDay) {
                    val circleType: CircleType
                    val textColor: Int
                    if (calendarState.contains(day.date)) {
                        textColor = Color.WHITE
                        circleType =
                            if (day.date == LocalDate.now()) {
                                CircleType.SELECTED_FULL
                            } else CircleType.FULL
                    } else {
                        val attrs = intArrayOf(android.R.attr.textColorSecondary)
                        val a: TypedArray? =
                            context?.theme?.obtainStyledAttributes(attrs)
                        textColor = a?.getColor(0, Color.BLACK) ?: Color.BLACK
                        a?.recycle()
                        circleType =
                            if (day.date == LocalDate.now()) {
                                CircleType.SELECTED_EMPTY
                            } else CircleType.EMPTY
                    }
                    textView.setTextColor(textColor)
                    textView.background = Background.getCircle(requireContext(), circleType)
                }
            }
            mCalendarView.scrollToMonth(currentMonth)
            mBinding.loadingLayout.visibility = View.GONE
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