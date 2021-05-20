package com.posse.kotlin1.calendar.view.pagerAdapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.view.FriendsFragment
import com.posse.kotlin1.calendar.view.SettingsFragment
import com.posse.kotlin1.calendar.view.StatisticFragment
import com.posse.kotlin1.calendar.view.calendar.CalendarFragment

class SectionsPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    val tabTitles = arrayOf(
        fragmentActivity.getString(R.string.tab_calendar_text),
        fragmentActivity.getString(R.string.tab_friends_text),
        fragmentActivity.getString(R.string.tab_statistic_text),
        fragmentActivity.getString(R.string.tab_settings_text)
    )

    override fun getItemCount(): Int {
        return tabTitles.size
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CalendarFragment.newInstance()
            1 -> FriendsFragment.newInstance()
            2 -> StatisticFragment.newInstance()
            3 -> SettingsFragment.newInstance()
            else -> throw RuntimeException()
        }
    }
}