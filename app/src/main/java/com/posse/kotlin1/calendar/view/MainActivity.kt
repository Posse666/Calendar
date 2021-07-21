package com.posse.kotlin1.calendar.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.databinding.ActivityMainBinding
import com.posse.kotlin1.calendar.utils.getAppTheme
import com.posse.kotlin1.calendar.view.calendar.CalendarFragment
import com.posse.kotlin1.calendar.view.settings.SettingsFragment

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(getAppTheme())
        setContentView(binding.root)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottomCalendar -> replaceFragment(CalendarFragment.newInstance())
                R.id.bottomFriends -> replaceFragment(FriendsFragment.newInstance())
                R.id.bottomSettings -> replaceFragment(SettingsFragment.newInstance())
                else -> replaceFragment(CalendarFragment.newInstance())
            }
            true
        }

        binding.bottomNavigation.selectedItemId = R.id.bottomCalendar
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .replace(R.id.mainContainer, fragment)
            .commit()
    }

    override fun onBackPressed() {
        if (binding.bottomNavigation.selectedItemId == R.id.bottomCalendar) {
            super.onBackPressed()
        } else {
            binding.bottomNavigation.selectedItemId = R.id.bottomCalendar
        }
    }
}