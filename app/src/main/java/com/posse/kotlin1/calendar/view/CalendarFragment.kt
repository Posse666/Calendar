package com.posse.kotlin1.calendar.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.posse.kotlin1.calendar.databinding.FragmentCalendarBinding
import com.posse.kotlin1.calendar.model.CalendarState
import com.posse.kotlin1.calendar.viewModel.CalendarViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class CalendarFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CalendarViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CalendarViewModel::class.java)
        viewModel.getLiveData().observe(viewLifecycleOwner, { renderData(it) })
        viewModel.getWeatherFromLocalSource()
    }

    private fun renderData(appState: CalendarState) {
        when (appState) {
            is CalendarState.Success -> {
//                val weatherData = appState.weatherData
//                binding.loadingLayout.visibility = View.GONE
//                setData(weatherData)
            }
            is CalendarState.Loading -> {
//                binding.loadingLayout.visibility = View.VISIBLE
            }
            is CalendarState.Error -> {
//                binding.loadingLayout.visibility = View.GONE
//                Snackbar
//                    .make(binding.mainView, getString(R.string.error), Snackbar.LENGTH_INDEFINITE)
//                    .setAction(getString(R.string.reload)) { viewModel.getWeatherFromLocalSource() }
//                    .show()
            }
        }
    }

//    private fun setData(weatherData: Weather) {
//        binding.cityName.text = weatherData.city.city
//        binding.cityCoordinates.text = String.format(
//            getString(R.string.city_coordinates),
//            weatherData.city.lat.toString(),
//            weatherData.city.lon.toString()
//        )
//        binding.temperatureValue.text = weatherData.temperature.toString()
//        binding.feelsLikeValue.text = weatherData.feelsLike.toString()
//    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String?, param2: String?) =
            CalendarFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}