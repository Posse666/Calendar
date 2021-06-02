package com.posse.kotlin1.calendar.view.statistic

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.animation.ArgbEvaluatorCompat
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.databinding.FragmentStatisticBinding
import com.posse.kotlin1.calendar.viewModel.ALL_TIME
import com.posse.kotlin1.calendar.viewModel.StatisticViewModel
import com.posse.kotlin1.calendar.viewModel.THIS_YEAR

class StatisticFragment : Fragment() {
    private var _binding: FragmentStatisticBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StatisticViewModel by lazy {
        ViewModelProvider(this).get(StatisticViewModel::class.java)
    }
    private val cards: Set<CardView> by lazy {
        setOf(
            binding.cardTotal,
            binding.cardThisYear,
            binding.cardAllTime
        )
    }
    private val startTemperature: Int by lazy {
        viewModel.getStartTemperature(requireContext()) + 40
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackgroundColor(startTemperature)
        viewModel.getLiveData().observe(viewLifecycleOwner, { updateStats() })
        viewModel.getTemperature().observe(viewLifecycleOwner, { updateColor(it) })
        viewModel.refreshTemperature(requireContext())
    }

    private fun setBackgroundColor(temperature: Int) {
        cards.forEach {
            it.setCardBackgroundColor(getColorFromTemperature(temperature))
        }
    }

    private fun getColorFromTemperature(temperature: Int): Int {
        return ArgbEvaluatorCompat.getInstance()
            .evaluate(
                temperature / 100f,
                context?.resources?.getColor(android.R.color.holo_blue_dark, null),
                context?.resources?.getColor(android.R.color.holo_red_dark, null)
            )
    }

    private fun updateColor(temperature: Int) {
        val handler = Handler(Looper.getMainLooper())
        Thread {
            if ((temperature + 40) > startTemperature) {
                for (i in startTemperature + 1..temperature + 40) {
                    handler.post {
                        setBackgroundColor(i)
                    }
                    Thread.sleep(100)
                }
            } else for (i in startTemperature - 1 downTo temperature + 40) {
                handler.post {
                    setBackgroundColor(i)
                }
                Thread.sleep(100)
            }
        }.start()
    }

    private fun updateStats() {
        updateTotal()
        updateMarathonThisYear()
        updateMarathonAllTime()
    }

    private fun updateTotal() {
        binding.totalYearStats.caption.putText(getString(R.string.in_this_year_you_drank))
        binding.totalYearStats.firstStat.putText(viewModel.getDrankDaysQuantity())
        binding.totalYearStats.description.putText(getString(R.string.days_of))
        binding.totalYearStats.secondStat.putText(viewModel.getThisYearDaysQuantity())
    }

    private fun updateMarathonThisYear() {
        binding.longestDrinkThisYear.caption.putText(getString(R.string.longest_drink_marathon_in_this_year))
        binding.longestDrinkThisYear.firstStat.putText(viewModel.getDrinkMarathon(THIS_YEAR))
        binding.longestDrinkThisYear.description.putText(getString(R.string.days))
    }

    private fun updateMarathonAllTime() {
        binding.longestDrinkAllTime.caption.putText(getString(R.string.longest_drink_marathon_all_time))
        binding.longestDrinkAllTime.firstStat.putText(viewModel.getDrinkMarathon(ALL_TIME))
        binding.longestDrinkAllTime.description.putText(getString(R.string.days))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = StatisticFragment()
    }
}

private fun AppCompatTextView.putText(newValue: Any) {
    text = newValue.toString()
}