package com.posse.kotlin1.calendar.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.ui.main.CalendarFragment
import com.posse.kotlin1.calendar.ui.settings.SettingsFragment

class SectionsPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    val tabTitles = arrayOf(
        fragmentActivity.getString(R.string.tab_text_1),
        fragmentActivity.getString(R.string.tab_text_2)
    )

    override fun getItemCount(): Int {
        return tabTitles.size
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CalendarFragment.newInstance(null, null)
            1 -> SettingsFragment.newInstance(null, null)
            else -> throw RuntimeException()
        }
    }
}