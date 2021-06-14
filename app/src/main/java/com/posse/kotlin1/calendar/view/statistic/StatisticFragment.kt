package com.posse.kotlin1.calendar.view.statistic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getLiveData().observe(viewLifecycleOwner, { updateStats() })
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