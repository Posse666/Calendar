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
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class MyCalendarFragment : Fragment() {

    @Inject
    lateinit var account: Account

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentMyCalendarBinding.inflate(inflater, container, false).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mail = account.getEmail()
        if (mail != null) swapFragment(mail)
        else account.anonymousLogin(requireContext()) { email: String ->
            swapFragment(email)
        }
    }

    private fun swapFragment(email: String) = childFragmentManager
        .beginTransaction()
        .setReorderingAllowed(true)
        .replace(R.id.myCalendarContainer, CalendarFragment.newInstance(email, true))
        .commit()

    companion object {
        @JvmStatic
        fun newInstance() = MyCalendarFragment()
    }
}