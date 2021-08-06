package com.posse.kotlin1.calendar.view.myCalendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.databinding.FragmentMyCalendarBinding
import com.posse.kotlin1.calendar.utils.Account
import com.posse.kotlin1.calendar.view.calendar.CalendarFragment

class MyCalendarFragment : Fragment() {
    private var _binding: FragmentMyCalendarBinding? = null
    private val binding get() = _binding!!
    private var email = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        email = Account.getEmail()
        childFragmentManager
            .beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.myCalendarContainer, CalendarFragment.newInstance(email, true))
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = MyCalendarFragment()
    }
}