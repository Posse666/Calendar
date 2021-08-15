package com.posse.kotlin1.calendar.view

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.databinding.ActivityMainBinding
import com.posse.kotlin1.calendar.utils.getAppTheme
import com.posse.kotlin1.calendar.view.friends.FriendsFragment
import com.posse.kotlin1.calendar.view.myCalendar.MyCalendarFragment
import com.posse.kotlin1.calendar.view.settings.SettingsFragment
import kotlin.system.exitProcess

private const val KEY_SELECTED = "Selected item"
private const val BACK_BUTTON_EXIT_DELAY = 3000

class MainActivity : AppCompatActivity(), SettingsTabSwitcher {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var isBackShown = false
    private var lastTimeBackPressed: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
        setTheme(getAppTheme())
        setContentView(binding.root)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottomCalendar -> replaceFragment(MyCalendarFragment.newInstance())
                R.id.bottomFriends -> replaceFragment(FriendsFragment.newInstance())
                R.id.bottomSettings -> replaceFragment(SettingsFragment.newInstance())
                else -> replaceFragment(MyCalendarFragment.newInstance())
            }
            true
        }

        @IdRes
        val startPage: Int = savedInstanceState?.getInt(KEY_SELECTED) ?: R.id.bottomCalendar
        binding.bottomNavigation.selectedItemId = startPage
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .replace(R.id.mainContainer, fragment)
            .commit()

        isBackShown = false
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putInt(KEY_SELECTED, binding.bottomNavigation.selectedItemId)
    }

    override fun onBackPressed() {
        if (binding.bottomNavigation.selectedItemId == R.id.bottomCalendar) {
            checkExit()
        } else {
            binding.bottomNavigation.selectedItemId = R.id.bottomCalendar
        }
        lastTimeBackPressed = System.currentTimeMillis()
    }

    private fun checkExit() {
        Toast.makeText(this, getString(R.string.back_again_to_exit), Toast.LENGTH_SHORT).show()
        if (System.currentTimeMillis() - lastTimeBackPressed < BACK_BUTTON_EXIT_DELAY && isBackShown) {
            exitProcess(0)
        }
        isBackShown = true
    }

    override fun switchToSettings() {
        binding.bottomNavigation.selectedItemId = R.id.bottomSettings
    }

    companion object{
        var instance: MainActivity? = null
    }
}

interface SettingsTabSwitcher{
    fun switchToSettings()
}