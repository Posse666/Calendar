package com.posse.kotlin1.calendar.view.statistic

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.card.MaterialCardView
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.databinding.FragmentStatisticBinding
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
        putStatistic(
            total,
            stats[STATISTIC.DAYS_THIS_YEAR]?.size.toString(),
            getString(R.string.in_this_year_you_drank),
            resources.getQuantityString(
                R.plurals.days,
                total
            ) + getString(R.string.of),
            null,
            binding.totalYearStats,
            null
        )
    }

    private fun updateMarathonThisYear(stats: Set<LocalDate>?) {
        val thisYear = stats?.size ?: 0
        putStatistic(
            thisYear,
            "",
            getString(R.string.longest_drink_marathon_in_this_year),
            resources.getQuantityString(
                R.plurals.days,
                thisYear
            ),
            stats,
            binding.longestDrinkThisYear,
            binding.cardThisYear
        )
    }

    private fun updateMarathonAllTime(stats: Set<LocalDate>?) {
        val allTime = stats?.size ?: 0
        putStatistic(
            allTime,
            "",
            getString(R.string.longest_drink_marathon_all_time),
            resources.getQuantityString(
                R.plurals.days,
                allTime
            ),
            stats,
            binding.longestDrinkAllTime,
            binding.cardAllTime
        )
    }

    private fun putStatistic(
        firstStat: Int,
        secondStat: String,
        description: String,
        plurals: String,
        stats: Set<LocalDate>?,
        textView: TextView,
        cardView: MaterialCardView?
    ) {
        val text = "$description$firstStat $plurals $secondStat"
        val spannable = SpannableString(text)
        spannable.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.fillColor, null)),
            description.length, description.length + firstStat.toString().length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textView.text = spannable
        cardView?.setOnClickListener {
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