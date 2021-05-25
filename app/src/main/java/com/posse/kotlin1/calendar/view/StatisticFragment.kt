package com.posse.kotlin1.calendar.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.posse.kotlin1.calendar.databinding.FragmentStatisticBinding
import com.posse.kotlin1.calendar.viewModel.StatisticViewModel

class StatisticFragment : Fragment() {
    private var _binding: FragmentStatisticBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: StatisticViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(StatisticViewModel::class.java)
        viewModel.getLiveData().observe(viewLifecycleOwner, { updateStats() })
    }

    private fun updateStats() {
        binding.stats.yearDrinkDays.text = viewModel.getDrankDaysQuantity().toString()
        binding.stats.yearDaysTotal.text = viewModel.getThisYearDaysQuantity().toString()
    }

    companion object {
        fun newInstance() = StatisticFragment()
    }
}