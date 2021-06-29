package com.posse.kotlin1.calendar.view.calendar

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
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
import com.posse.kotlin1.calendar.room.CalendarEntity
import com.posse.kotlin1.calendar.utils.Permission
import com.posse.kotlin1.calendar.utils.checkPermission
import com.posse.kotlin1.calendar.utils.checkPermissionsResult
import com.posse.kotlin1.calendar.view.map.GoogleMapsFragment
import com.posse.kotlin1.calendar.viewModel.CalendarViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.*
import kotlin.collections.HashSet

private const val REQUEST_CODE = 55

class CalendarFragment : Fragment() {
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private val calendarView: CalendarView by lazy { binding.calendarView }
    private val viewModel: CalendarViewModel by lazy {
        ViewModelProvider(this).get(CalendarViewModel::class.java)
    }
    private val locationManager: LocationManager? by lazy {
        requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager?
    }
    private val drinkDates: HashSet<LocalDate> = HashSet()
    private lateinit var statisticSwitcher: StatisticSwitcher
    private var lastPressedDate: LocalDate = LocalDate.now()

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

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE -> {
                when (checkPermissionsResult(
                    this,
                    grantResults,
                    getString(R.string.location_access_description),
                    getString(R.string.location_access_message),
                    getString(R.string.close)
                )) {
                    Permission.GRANTED -> {
                    }
                    Permission.NOT_GRANTED -> {
                    }
                }
            }
        }
    }

    private fun updateCalendar(calendarState: Set<LocalDate>) {
        setupStats()
        if (calendarState.subtract(drinkDates).isNotEmpty()
            || drinkDates.subtract(calendarState).isNotEmpty()
        ) {
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
                                    changeDay(true, textView, day.date)
                                    calendarView.notifyDayChanged(day)
                                    viewModel.dayClicked(day.date)
                                    if (drinkDates.contains(day.date)) {
                                        lastPressedDate = day.date
                                        requirePermission(Location.SET_LOCATION)
                                    }
                                }
                                container.view.setOnLongClickListener {
                                    if (drinkDates.contains(day.date)) {
                                        lastPressedDate = day.date
                                        requirePermission(Location.GET_LOCATION)
                                    }else{
                                        Toast.makeText(context, R.string.no_saved_data, Toast.LENGTH_LONG).show()
                                    }
                                    true
                                }
                            } else {
                                container.view.setOnClickListener(null)
                                container.view.setOnLongClickListener(null)
                            }
                            changeDay(false, textView, day.date)
                        } else {
                            textView.disappear()
                        }
                    }

                    private fun requirePermission(location: Location) {
                        when (checkPermission(
                            REQUEST_CODE,
                            this@CalendarFragment,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            getString(R.string.location_access_description),
                            getString(R.string.location_access_message),
                            getString(R.string.allow_access),
                            getString(R.string.no_thanks)
                        )) {
                            Permission.GRANTED -> {
                                when (location) {
                                    Location.GET_LOCATION -> {
                                        getLocation()
                                    }
                                    Location.SET_LOCATION -> setLocation()
                                }
                            }
                            Permission.NOT_GRANTED -> {
                            }
                        }
                    }

                    private fun getLocation() {
                        val handler = Handler(Looper.getMainLooper())
                        val callback = { day: CalendarEntity? ->
                            day?.let {
                                if (it.latitude == 0.0 && it.longitude == 0.0) {
                                    handler.post {
                                        Toast.makeText(context, R.string.no_location, Toast.LENGTH_LONG).show()
                                    }
                                } else {
                                    GoogleMapsFragment
                                        .newInstance(
                                            LocalDate.ofEpochDay(it.date),
                                            it.latitude,
                                            it.longitude
                                        )
                                        .show(requireActivity().supportFragmentManager, null)
                                }
                            }
                        }
                        viewModel.getLocation(lastPressedDate, callback)
                    }

                    private val locationListener: LocationListener = object : LocationListener {
                        override fun onLocationChanged(location: android.location.Location) {
                            viewModel.setLocation(
                                lastPressedDate,
                                location.longitude,
                                location.latitude
                            )
                            locationManager?.removeUpdates(this)
                        }

                        override fun onStatusChanged(
                            provider: String,
                            status: Int,
                            extras: Bundle
                        ) {
                        }

                        override fun onProviderEnabled(provider: String) {}
                        override fun onProviderDisabled(provider: String) {}
                    }

                    @SuppressLint("MissingPermission")
                    private fun setLocation() {
                        val lastLocation =
                            locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        lastLocation?.let {
                            viewModel.setLocation(
                                lastPressedDate,
                                lastLocation.longitude,
                                lastLocation.latitude
                            )
                        }
                        locationManager?.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            0L,
                            0f,
                            locationListener
                        )
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
            throw RuntimeException("The activity does not implement the listener")
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

private enum class Location {
    GET_LOCATION,
    SET_LOCATION
}