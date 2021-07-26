package com.posse.kotlin1.calendar.view.statistic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.databinding.FragmentStatisticBinding
import com.posse.kotlin1.calendar.utils.putText
import com.posse.kotlin1.calendar.viewModel.CalendarViewModel
import com.posse.kotlin1.calendar.viewModel.STATISTIC
import java.time.LocalDate

class StatisticFragment : Fragment() {
    private var _binding: FragmentStatisticBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CalendarViewModel by activityViewModels()
    private var listener: StatisticListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getLiveStats().observe(viewLifecycleOwner, { updateStats(it) })
    }

    private fun updateStats(stats: Map<STATISTIC, Set<LocalDate>>) {
        updateTotal(stats)
        updateMarathonThisYear(stats[STATISTIC.DRINK_MAX_ROW_THIS_YEAR])
        updateMarathonAllTime(stats[STATISTIC.DRINK_MAX_ROW_TOTAL])
    }

    private fun updateTotal(stats: Map<STATISTIC, Set<LocalDate>>) {
        val total = stats[STATISTIC.DRINK_DAYS_THIS_YEAR]?.size ?: 0
        binding.totalYearStats.caption.putText(getString(R.string.in_this_year_you_drank))
        binding.totalYearStats.firstStat.putText("$total ")
        binding.totalYearStats.description.putText(
            resources.getQuantityString(
                R.plurals.days,
                total
            ) + getString(R.string.of)
        )
        binding.totalYearStats.secondStat.putText(stats[STATISTIC.DAYS_THIS_YEAR]?.size ?: 0)
    }

    private fun updateMarathonThisYear(stats: Set<LocalDate>?) {
        val thisYear = stats?.size ?: 0
        binding.longestDrinkThisYear.caption.putText(getString(R.string.longest_drink_marathon_in_this_year))
        binding.longestDrinkThisYear.firstStat.putText("$thisYear ")
        binding.longestDrinkThisYear.description.putText(
            resources.getQuantityString(
                R.plurals.days,
                thisYear
            )
        )
        binding.cardThisYear.setOnClickListener {
            listener?.cardStatsPressed(stats?.minOrNull() ?: LocalDate.now())
        }
    }

    private fun updateMarathonAllTime(stats: Set<LocalDate>?) {
        val allTime = stats?.size ?: 0
        binding.longestDrinkAllTime.caption.putText(getString(R.string.longest_drink_marathon_all_time))
        binding.longestDrinkAllTime.firstStat.putText("$allTime ")
        binding.longestDrinkAllTime.description.putText(
            resources.getQuantityString(
                R.plurals.days,
                allTime
            )
        )
        binding.cardAllTime.setOnClickListener {
            listener?.cardStatsPressed(stats?.minOrNull() ?: LocalDate.now())
        }
    }

    fun setListener(listener: StatisticListener) {
        this.listener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        listener = null
    }

    companion object {
        fun newInstance() = StatisticFragment()
    }
}

interface StatisticListener {
    fun cardStatsPressed(date: LocalDate)
}