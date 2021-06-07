package com.posse.kotlin1.calendar.view.pagerAdapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.view.FriendsFragment
import com.posse.kotlin1.calendar.view.SettingsFragment
import com.posse.kotlin1.calendar.view.calendar.CalendarFragment
import com.posse.kotlin1.calendar.view.statistic.StatisticFragment

class SectionsPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return TabTitles.values().size
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            TabTitles.CALENDAR.position -> CalendarFragment.newInstance()
            TabTitles.FRIENDS.position -> FriendsFragment.newInstance()
            TabTitles.STATISTIC.position -> StatisticFragment.newInstance()
            TabTitles.SETTINGS.position -> SettingsFragment.newInstance()
            else -> throw RuntimeException()
        }
    }
}

enum class TabTitles(val tabResources: Int, val position: Int) {
    CALENDAR(R.string.tab_calendar_text, 0),
    FRIENDS(R.string.tab_friends_text, 1),
    STATISTIC(R.string.tab_statistic_text, 2),
    SETTINGS(R.string.tab_settings_text, 3)
}