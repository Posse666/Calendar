package com.posse.kotlin1.calendar.view.calendar

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
    private val binding get() = _binding!!
    private val calendarView: CalendarView
        get() = binding.calendarView
    private lateinit var viewModel: CalendarViewModel
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

        viewModel = ViewModelProvider(this).get(CalendarViewModel::class.java)
        viewModel.getLiveData().observe(viewLifecycleOwner, { updateCalendar(it) })
        viewModel.refreshDrankState()
        setupStats()

        binding.fab.setOnClickListener {
            calendarView.smoothScrollToMonth(YearMonth.now())
        }

        binding.statsCard.setOnClickListener {
            statisticSwitcher.switchToStatistic()
        }
    }

    private fun updateCalendar(calendarState: Set<LocalDate>) {
        binding.loadingLayout.visibility = View.VISIBLE
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
            calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
                override fun create(view: View) = MonthViewContainer(view)
                override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                    val monthName =
                        getString(Month.values()[month.yearMonth.monthValue - 1].monthResource)
                    ("$monthName ${month.year}")
                        .also { container.textView.text = it }
                }
            }
            calendarView.dayBinder = object : DayBinder<DayViewContainer> {
                override fun create(view: View) = DayViewContainer(view)
                override fun bind(container: DayViewContainer, day: CalendarDay) {
                    container.day = day
                    val textView = container.textView
                    textView.text = day.date.dayOfMonth.toString()
                    if (day.owner == DayOwner.THIS_MONTH) {
                        textView.visibility = View.VISIBLE
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
            calendarView.scrollToMonth(currentMonth)
            binding.loadingLayout.visibility = View.GONE
        }
    }

    private fun setupStats() {
        binding.stats.yearDrinkDays.text = viewModel.getDrankDaysQuantity().toString()
        binding.stats.yearDaysTotal.text = viewModel.getThisYearDaysQuantity().toString()
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

interface StatisticSwitcher {
    fun switchToStatistic()
}