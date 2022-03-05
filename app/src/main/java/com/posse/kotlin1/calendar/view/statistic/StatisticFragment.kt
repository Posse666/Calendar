package com.posse.kotlin1.calendar.view.statistic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.databinding.FragmentStatisticBinding
import com.posse.kotlin1.calendar.utils.putText
import com.posse.kotlin1.calendar.viewModel.CalendarViewModel
import com.posse.kotlin1.calendar.viewModel.STATISTIC
import java.time.LocalDate

class StatisticFragment : Fragment() {

    private var _binding: FragmentStatisticBinding? = null
    private val binding get() = _binding!!
    private var viewModel: CalendarViewModel? = null
    private var listener: StatisticListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentStatisticBinding.inflate(inflater, container, false)
        .also { _binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel?.getLiveStats()?.observe(viewLifecycleOwner) { updateStats(it) }
    }

    private fun updateStats(stats: Map<STATISTIC, Set<LocalDate>>) {
        updateTotal(stats[STATISTIC.DRINK_DAYS_THIS_YEAR])
        updateMarathonThisYear(stats[STATISTIC.DRINK_MAX_ROW_THIS_YEAR])
        updateMarathonAllTime(stats[STATISTIC.DRINK_MAX_ROW_TOTAL])
        updateFreshMarathonThisYear(stats[STATISTIC.NOT_DRINK_MAX_ROW_THIS_YEAR])
        updateFreshMarathonAllTime(stats[STATISTIC.NOT_DRINK_MAX_ROW_TOTAL])
    }

    private fun updateTotal(stats: Set<LocalDate>?) = with(binding) {
        putStatistic(
            getString(R.string.in_this_year_you_drank),
            stats,
            cardTotalYears.statsDescription,
            cardTotalYears.statsValue,
            null
        )
    }

    private fun updateMarathonThisYear(stats: Set<LocalDate>?) = with(binding) {
        putStatistic(
            getString(R.string.longest_drink_marathon_in_this_year),
            stats,
            cardThisYear.statsDescription,
            cardThisYear.statsValue,
            cardThisYear.root
        )
    }

    private fun updateMarathonAllTime(stats: Set<LocalDate>?) = with(binding) {
        putStatistic(
            getString(R.string.longest_drink_marathon_all_time),
            stats,
            cardAllTime.statsDescription,
            cardAllTime.statsValue,
            cardAllTime.root
        )
    }

    private fun updateFreshMarathonThisYear(stats: Set<LocalDate>?) = with(binding) {
        putStatistic(
            getString(R.string.longest_fresh_marathon_in_this_year),
            stats,
            cardFreshThisYear.statsDescription,
            cardFreshThisYear.statsValue,
            cardFreshThisYear.root
        )
    }

    private fun updateFreshMarathonAllTime(stats: Set<LocalDate>?) = with(binding) {
        putStatistic(
            getString(R.string.longest_fresh_marathon_all_time),
            stats,
            cardFreshAllTime.statsDescription,
            cardFreshAllTime.statsValue,
            cardFreshAllTime.root
        )
    }

    private fun putStatistic(
        description: String,
        stats: Set<LocalDate>?,
        descriptionTextView: TextView,
        valueTextView: TextView,
        cardView: MaterialCardView?
    ) {
        val stats1 = stats ?: emptySet()
        descriptionTextView.putText(description)
        valueTextView.putText(stats1.size)
        cardView?.setOnClickListener {
            listener?.cardStatsPressed(stats1.minOrNull() ?: LocalDate.now())
        }
    }

    fun setListener(listener: StatisticListener) {
        this.listener = listener
    }

    fun setViewModel(model: CalendarViewModel) {
        viewModel = model
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        listener = null
        viewModel = null
    }

    companion object {
        fun newInstance() = StatisticFragment()
    }
}

interface StatisticListener {
    fun cardStatsPressed(date: LocalDate)
}