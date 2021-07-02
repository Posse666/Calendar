package com.posse.kotlin1.calendar.view.calendar

import android.content.res.TypedArray
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.databinding.FragmentCalendarBinding
import com.posse.kotlin1.calendar.view.statistic.StatisticFragment
import com.posse.kotlin1.calendar.viewModel.CalendarViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.*
import kotlin.collections.HashSet

private const val MULTIPLY = 5

class CalendarFragment : Fragment() {
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private val calendarView: CalendarView by lazy { binding.calendarView }
    private val viewModel: CalendarViewModel by lazy {
        ViewModelProvider(this).get(CalendarViewModel::class.java)
    }
    private val drinkDates: HashSet<LocalDate> = HashSet()
    private var isInitCompleted: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.calendarLayout.setPadding(0, 0, 0, getActionBarSize() + getTextSize() * MULTIPLY)
        setupStatistic()
        setupFAB()
        viewModel.getLiveData().observe(viewLifecycleOwner, { updateCalendar(it) })
    }

    private fun setupStatistic() {
        val statisticFragment = StatisticFragment.newInstance()
        requireActivity()
            .supportFragmentManager
            .beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.statsContainer, statisticFragment)
            .runOnCommit {
                statisticFragment.view?.let { setBottomSheetBehavior(it.findViewById(R.id.bottom_sheet_container)) }
            }
            .commit()
    }

    private fun setBottomSheetBehavior(bottomSheet: ConstraintLayout) {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.peekHeight = getActionBarSize() + getTextSize() * MULTIPLY
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun setupFAB() {
        val layoutParams = binding.fab.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.bottomMargin =
            resources.getDimension(R.dimen.fab_margin)
                .toInt() + getActionBarSize() + getTextSize() * MULTIPLY

        binding.fab.setOnClickListener {
            calendarView.smoothScrollToMonth(YearMonth.now())
        }
    }

    private fun getActionBarSize(): Int {
        val actionBarSizeAttr = intArrayOf(android.R.attr.actionBarSize)
        val a: TypedArray? = context?.obtainStyledAttributes(actionBarSizeAttr)
        val actionBarSize = a?.getDimensionPixelSize(0, 100) ?: 100
        a?.recycle()
        return actionBarSize
    }

    private fun getTextSize(): Int {
        return resources.getDimension(R.dimen.stats_text_size).toInt()
    }

    private fun updateCalendar(calendarState: Set<LocalDate>) {
        if ((calendarState.subtract(drinkDates).isNotEmpty()
                    || drinkDates.subtract(calendarState).isNotEmpty())
            || (!isInitCompleted && calendarState.isEmpty())
        ) {
            binding.loadingLayout.show()
            drinkDates.clear()
            drinkDates.addAll(calendarState)
            val currentMonth = YearMonth.now()

            var firstMonth = currentMonth.minusMonths(12)
            if (calendarState.isNotEmpty()) {
                val minMonth = YearMonth.from(Collections.min(calendarState))
                if (minMonth.isBefore(firstMonth)) {
                    firstMonth = minMonth
                }
            }
            calendarView.setupAsync(
                firstMonth,
                currentMonth.plusMonths(1),
                WeekFields.of(Locale.GERMAN).firstDayOfWeek
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
                                    changeDay(true, textView, day.date)
                                    calendarView.notifyDayChanged(day)
                                    viewModel.dayClicked(day.date)
                                }
                            } else {
                                container.view.setOnClickListener(null)
                            }
                            changeDay(false, textView, day.date)
                        } else {
                            textView.disappear()
                        }
                    }

                    private fun changeDay(isClicked: Boolean, textView: TextView, date: LocalDate) {
                        val circleType: CircleType
                        val textColor: Int
                        if (drinkDates.contains(date)) {
                            textColor = getTextColor(isClicked)
                            circleType = getCircleType(isClicked, date)
                            if (isClicked) drinkDates.remove(date)
                        } else {
                            textColor = getTextColor(!isClicked)
                            circleType = getCircleType(!isClicked, date)
                            if (isClicked) drinkDates.add(date)
                        }
                        textView.setTextColor(textColor)
                        textView.background = Background.getCircle(requireContext(), circleType)
                    }

                    private fun getCircleType(clicked: Boolean, date: LocalDate): CircleType {
                        if (clicked) return circle(
                            CircleType.SELECTED_EMPTY,
                            CircleType.EMPTY,
                            date
                        )
                        return circle(CircleType.SELECTED_FULL, CircleType.FULL, date)
                    }

                    private fun getTextColor(clicked: Boolean): Int {
                        if (clicked) return defaultColor()
                        return Color.WHITE
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
                isInitCompleted = true
                binding.loadingLayout.hide()
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