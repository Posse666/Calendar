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
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.app.App
import com.posse.kotlin1.calendar.databinding.FragmentCalendarBinding
import com.posse.kotlin1.calendar.utils.statsUsed
import com.posse.kotlin1.calendar.view.statistic.StatisticFragment
import com.posse.kotlin1.calendar.view.statistic.StatisticListener
import com.posse.kotlin1.calendar.viewModel.CalendarViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.*
import kotlin.collections.HashSet

private const val MULTIPLY = 3.5

class CalendarFragment : Fragment(), StatisticListener {
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private val calendarView: CalendarView by lazy { binding.calendarView }
    private val viewModel: CalendarViewModel by activityViewModels()
    private val clickedDates: HashMap<CalendarDay, TextView> = hashMapOf()
    private var isInitCompleted: Boolean = false
    private var isStatsUsed = App.sharedPreferences?.statsUsed ?: false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.calendarLayout.setPadding(0, 0, 0, (getTextSize() * MULTIPLY).toInt())
        setupStatistic()
        setupFAB()
        viewModel.getLiveData()
            .observe(viewLifecycleOwner, {
                if (!isInitCompleted) updateCalendar(it)
                confirmDayChange(it)
            })
    }

    private fun confirmDayChange(days: Set<LocalDate>?) {
        days?.let {
            val keys = clickedDates.keys.map { it.date }
            val intersected = keys.intersect(days)
            val calendarDays: HashSet<CalendarDay> = hashSetOf()
            clickedDates.keys.forEach {
                if (intersected.contains(it.date)) calendarDays.add(it)
            }
            if (calendarDays.isNotEmpty()) {
                calendarDays.forEach {
                    clickedDates[it]?.let { view -> changeDay(intersected, view, it.date) }
                }
            }
            val subtracted = clickedDates.keys.subtract(days)

        }
        calendarView.notifyDayChanged(day)
    }

    private fun setupStatistic() {
        val statisticFragment = StatisticFragment.newInstance()
        statisticFragment.setListener(this)
        childFragmentManager
            .beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.statsContainer, statisticFragment)
            .runOnCommit {
                statisticFragment.view?.let {
                    setBottomSheetBehavior(it.findViewById(R.id.bottom_sheet_container))
                    if (!isStatsUsed) setBottomSheetAnimation()
                }
            }
            .commit()
    }

    private fun setBottomSheetBehavior(bottomSheet: ConstraintLayout) {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.peekHeight = (getTextSize() * MULTIPLY).toInt()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        if (!isStatsUsed) {
            val callback = object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                        isStatsUsed = true
                        App.sharedPreferences?.statsUsed = true
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    //not needed
                }
            }
            bottomSheetBehavior.addBottomSheetCallback(callback)
        }
    }

    private fun setBottomSheetAnimation() {
        Thread {
            while (this@CalendarFragment.isAdded && !isStatsUsed) {
                Thread.sleep(30000)
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED
                    && this@CalendarFragment.isAdded && !isStatsUsed
                ) {
                    bottomSheetBehavior
                        .setPeekHeight(((getTextSize() * MULTIPLY) * 1.3).toInt(), true)
                    Thread.sleep(200)
                    bottomSheetBehavior.setPeekHeight((getTextSize() * MULTIPLY).toInt(), true)
                }
            }
        }.start()
    }

    private fun setupFAB() {
        val layoutParams = binding.fab.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.bottomMargin =
            (resources.getDimension(R.dimen.fab_margin)
                .toInt() + getTextSize() * MULTIPLY).toInt()

        binding.fab.setOnClickListener {
            calendarView.smoothScrollToMonth(YearMonth.now())
        }
    }

    private fun getTextSize(): Int {
        return resources.getDimension(R.dimen.stats_text_size).toInt()
    }

    private fun updateCalendar(calendarState: Set<LocalDate>) {
        binding.loadingLayout.show()
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
                                clickedDates[day] = textView
                                viewModel.dayClicked(day.date)
                                animateButton(textView, day.date)
                            }
                        } else {
                            container.view.setOnClickListener(null)
                        }
                        changeDay(calendarState, textView, day.date)
                    } else {
                        textView.hide()
                    }
                }

                private fun animateButton(textView: AppCompatTextView, date: LocalDate) {
                    TODO("Not yet implemented")
                }
            }
            calendarView.scrollToMonth(currentMonth)
            isInitCompleted = true
            binding.loadingLayout.disappear()
        }
    }

    private fun changeDay(dates: Set<LocalDate>, textView: TextView, date: LocalDate) {
        val textColor: Int = getTextColor(dates.contains(date))
        val circleType: CircleType = getCircleType(dates.contains(date), date)
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
        if (clicked) return defaultColor
        return Color.WHITE
    }

    private val defaultColor: Int
        get() {
            val attrs = intArrayOf(android.R.attr.textColorSecondary)
            val a: TypedArray? = context?.theme?.obtainStyledAttributes(attrs)
            val result = a?.getColor(0, Color.BLACK) ?: Color.BLACK
            a?.recycle()
            return result
        }

    private val circle =
        { circle: CircleType, circle2: CircleType, date: LocalDate ->
            if (date == LocalDate.now()) circle
            else circle2
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = CalendarFragment()
    }

    override fun cardStatsPressed(date: LocalDate) {
        scrollToDate(date)
    }

    private fun scrollToDate(date: LocalDate) {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        calendarView.smoothScrollToMonth(YearMonth.from(date))
    }
}

private fun AppCompatTextView.putText(newValue: Any) {
    text = newValue.toString()
}

private fun View.show() {
    visibility = View.VISIBLE
}

private fun FrameLayout.disappear() {
    visibility = View.GONE
}

private fun TextView.hide() {
    visibility = View.INVISIBLE
}