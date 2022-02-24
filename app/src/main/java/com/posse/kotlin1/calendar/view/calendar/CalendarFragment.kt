package com.posse.kotlin1.calendar.view.calendar

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
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
import com.posse.kotlin1.calendar.model.DataModel
import com.posse.kotlin1.calendar.utils.*
import com.posse.kotlin1.calendar.view.calendar.chooseLayout.ChooseFragment
import com.posse.kotlin1.calendar.view.statistic.StatisticFragment
import com.posse.kotlin1.calendar.view.statistic.StatisticListener
import com.posse.kotlin1.calendar.view.update.UpdateDialog
import com.posse.kotlin1.calendar.viewModel.CalendarViewModel
import dagger.android.support.AndroidSupportInjection
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.*
import javax.inject.Inject

class CalendarFragment : Fragment(), StatisticListener {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private val animator = Animator()
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private val calendarView: CalendarView by lazy { binding.calendarView }
    private val viewModel: CalendarViewModel by lazy {
        viewModelFactory.create(CalendarViewModel::class.java)
    }
    private val actualState: MutableSet<DataModel> = mutableSetOf()
    private var isInitCompleted: Boolean = false
    private lateinit var email: String
    private var isMyCalendar = false
    private var isStatsUsed: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
        isStatsUsed = sharedPreferences.statsUsed
        restoreArguments()
    }

    private fun restoreArguments() {
        arguments?.let {
            email = it.getString(ARG_MAIL)!!
            isMyCalendar = it.getBoolean(ARG_MY_CALENDAR)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentCalendarBinding.inflate(inflater, container, false)
        .also { _binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.calendarLayout.setPadding(0, 0, 0, (getTextSize() * MULTIPLY).toInt())
        setupStatistic()
        setupFAB()
        viewModel.refreshLiveData(email) {
            when (it) {
                Result.Error -> UpdateDialog.newInstance().show(childFragmentManager, null)
                is Result.Offline -> {
                    context?.showToast(getString(R.string.no_connection))
                    setupLiveData()
                }
                is Result.Success -> setupLiveData()
            }
        }
    }

    private fun setupLiveData() = if (this.isVisible) {
        viewModel.getLiveData().observe(viewLifecycleOwner) {
            if (it.first) {
                actualState.clear()
                actualState.addAll(it.second)
                if (!isInitCompleted) updateCalendar()
            }
        }
    } else Unit

    private fun setupStatistic() {
        val statisticFragment = StatisticFragment
            .newInstance()
            .apply {
                setListener(this@CalendarFragment)
                setViewModel(viewModel)
            }
        childFragmentManager
            .beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.statsContainer, statisticFragment)
            .runOnCommit {
                statisticFragment.view?.let {
                    setBottomSheetBehavior(it.findViewById(R.id.bottom_sheet_container))
                    if (isStatsUsed != true) setBottomSheetAnimation()
                }
            }
            .commit()
    }

    private fun setBottomSheetBehavior(bottomSheet: ConstraintLayout) {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.peekHeight = (getTextSize() * MULTIPLY).toInt()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (isStatsUsed != true && bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                    isStatsUsed = true
                    sharedPreferences.statsUsed = true
                }
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) { /*not needed*/
            }
        })
    }

    private fun setBottomSheetAnimation() {
        Thread {
            while (isStatsUsed != true && this@CalendarFragment.isAdded) {
                Thread.sleep(BOTTOM_ANIMATION_INTERVAL)
                if (isStatsUsed != true
                    && this@CalendarFragment.isAdded
                    && bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED
                ) {
                    bottomSheetBehavior
                        .setPeekHeight(((getTextSize() * MULTIPLY * 2) * 1.3).toInt(), true)
                    Thread.sleep(400)
                    if (this@CalendarFragment.isAdded) bottomSheetBehavior.setPeekHeight(
                        (getTextSize() * MULTIPLY).toInt(),
                        true
                    )
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

    private fun getTextSize() = resources.getDimension(R.dimen.stats_text_size).toInt()

    private fun updateCalendar() {
        binding.loadingLayout.show()
        val currentMonth = YearMonth.now()
        var firstMonth = if (isMyCalendar) currentMonth.minusMonths(12) else currentMonth
        if (actualState.isNotEmpty()) {
            val minDate = actualState.minByOrNull { it.date }?.date
            val minMonth = if (minDate == null) currentMonth
            else YearMonth.from(convertLongToLocalDale(minDate))
            if (minMonth.isBefore(firstMonth)) firstMonth = minMonth
        }

        calendarView.setup(
            firstMonth,
            currentMonth.plusMonths(1),
            WeekFields.of(Locale.GERMAN).firstDayOfWeek
        )

        calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
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
                val rootView = container.rootView
                rootView.shotGlassText.putText(day.date.dayOfMonth)
                if (day.owner == DayOwner.THIS_MONTH) {
                    rootView.root.show()

                    val currentDayType = DrinkType.values().find { type ->
                        type.value == actualState.find {
                            convertLongToLocalDale(it.date) == day.date
                        }?.drinkType
                    }

                    if (isMyCalendar
                        && (day.date.isBefore(LocalDate.now()) || day.date.isEqual(LocalDate.now()))
                    ) {
                        container.view.setOnClickListener {
                            ChooseFragment.newInstance(day.date.dayOfMonth) {
                                viewModel.dayClicked(
                                    DataModel(convertLocalDateToLong(day.date), it?.value)
                                ) {
                                    UpdateDialog.newInstance()
                                        .show(childFragmentManager, null)
                                }
                                animator.animate(rootView.root) {
                                    container.changeDay(day.date, it)
                                }
                            }.show(childFragmentManager, null)
                        }
                    } else {
                        container.view.setOnClickListener(null)
                    }
                    container.changeDay(day.date, currentDayType)
                } else rootView.root.hide()
            }
        }
        calendarView.scrollToMonth(currentMonth)
        isInitCompleted = true
        binding.loadingLayout.disappear()
    }

    override fun cardStatsPressed(date: LocalDate) {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        calendarView.smoothScrollToMonth(YearMonth.from(date))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(email: String, isMyCalendar: Boolean) = CalendarFragment()
            .apply {
                arguments = bundleOf(
                    ARG_MAIL to email,
                    ARG_MY_CALENDAR to isMyCalendar
                )
            }

        private const val ARG_MY_CALENDAR = "My calendar"
        private const val ARG_MAIL = "eMail"
        private const val MULTIPLY: Double = 3.5
        private const val BOTTOM_ANIMATION_INTERVAL: Long = 20000
    }
}