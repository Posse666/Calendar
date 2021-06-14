package com.posse.kotlin1.calendar.view.calendar

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.databinding.FragmentCalendarBinding
import com.posse.kotlin1.calendar.viewModel.CalendarViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.*
import kotlin.collections.HashSet

class CalendarFragment : Fragment() {
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private val calendarView: CalendarView by lazy { binding.calendarView }
    private val viewModel: CalendarViewModel by lazy {
        ViewModelProvider(this).get(CalendarViewModel::class.java)
    }
    private val drinkDates: HashSet<LocalDate> = HashSet()
    private lateinit var statisticSwitcher: StatisticSwitcher

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getLiveData().observe(viewLifecycleOwner, { updateCalendar(it) })
        viewModel.refreshDrankState()

        binding.fab.setOnClickListener {
            calendarView.smoothScrollToMonth(YearMonth.now())
        }

        binding.statsCard.setOnClickListener {
            statisticSwitcher.switchToStatistic()
        }
    }

    private fun updateCalendar(calendarState: Set<LocalDate>) {
        if (calendarState.subtract(drinkDates).size != 1 && drinkDates.subtract(calendarState).size != 1) {
            drinkDates.clear()
            drinkDates.addAll(calendarState)
            binding.loadingLayout.show()
            val currentMonth = YearMonth.now()
            var firstMonth = YearMonth.from(Collections.min(calendarState))
            if (firstMonth.isAfter(currentMonth.minusMonths(12))) {
                firstMonth = currentMonth.minusMonths(12)
            }
            calendarView.setupAsync(
                firstMonth,
                currentMonth.plusMonths(1),
                WeekFields.of(Locale.getDefault()).firstDayOfWeek
            ) {
                calendarView.monthHeaderBinder =
                    object : MonthHeaderFooterBinder<MonthViewContainer> {
                        override fun create(view: View) = MonthViewContainer(view)
                        override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                            val monthName =
                                getString(Month.values()[month.yearMonth.monthValue - 1].monthResource)
                            ("$monthName ${month.year}").also { container.textView.putText(it) }
                        }
                    }
                calendarView.dayBinder = object : DayBinder<DayViewContainer> {
                    override fun create(view: View) = DayViewContainer(view)
                    override fun bind(container: DayViewContainer, day: CalendarDay) {
                        container.day = day
                        val textView = container.textView
                        textView.putText(day.date.dayOfMonth)
                        if (day.owner == DayOwner.THIS_MONTH) {
                            textView.show()
                            if (day.date.isBefore(LocalDate.now()) || day.date.isEqual(LocalDate.now())) {
                                container.view.setOnClickListener {
                                    viewModel.dayClicked(day.date)
                                    changeDay(textView, day)
                                    calendarView.notifyDayChanged(day)
                                    setupStats()
                                }
                            } else container.view.setOnClickListener(null)
                            changeDay(textView, day)
                        } else {
                            textView.disappear()
                        }
                    }

                    private fun changeDay(textView: TextView, day: CalendarDay) {
                        val circleType: CircleType
                        val textColor: Int
                        if (calendarState.contains(day.date)) {
                            textColor = Color.WHITE
                            circleType = circle(CircleType.SELECTED_FULL, CircleType.FULL, day.date)
                            drinkDates.add(day.date)
                        } else {
                            textColor = defaultColor()
                            circleType =
                                circle(CircleType.SELECTED_EMPTY, CircleType.EMPTY, day.date)
                            drinkDates.remove(day.date)
                        }
                        textView.setTextColor(textColor)
                        textView.background = Background.getCircle(requireContext(), circleType)
                    }

                    private val defaultColor = {
                        val attrs = intArrayOf(android.R.attr.textColorSecondary)
                        val a: TypedArray? = context?.theme?.obtainStyledAttributes(attrs)
                        val result = a?.getColor(0, Color.BLACK) ?: Color.BLACK
                        a?.recycle()
                        result
                    }

                    private val circle =
                        { circle: CircleType, circle2: CircleType, date: LocalDate ->
                            if (date == LocalDate.now()) circle
                            else circle2
                        }
                }
                calendarView.scrollToMonth(currentMonth)
                setupStats()
                binding.loadingLayout.hide()
            }
        }
    }

    private fun setupStats() {
        binding.stats.caption.putText(getString(R.string.in_this_year_you_drank))
        binding.stats.firstStat.putText(viewModel.getDrankDaysQuantity())
        binding.stats.description.putText(getString(R.string.days_of))
        binding.stats.secondStat.putText(viewModel.getThisYearDaysQuantity())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            statisticSwitcher = context as StatisticSwitcher
        } catch (castException: ClassCastException) {
            /** The activity does not implement the listener. */
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = CalendarFragment()
    }
}

private fun AppCompatTextView.putText(newValue: Any) {
    text = newValue.toString()
}

private fun View.show() {
    visibility = View.VISIBLE
}

private fun FrameLayout.hide() {
    visibility = View.GONE
}

private fun TextView.disappear() {
    visibility = View.INVISIBLE
}

interface StatisticSwitcher {
    fun switchToStatistic()
}