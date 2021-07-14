package com.posse.kotlin1.calendar.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.posse.kotlin1.calendar.databinding.ActivityMainBinding
import com.posse.kotlin1.calendar.utils.getAppTheme
import com.posse.kotlin1.calendar.view.pagerAdapter.SectionsPagerAdapter
import com.posse.kotlin1.calendar.view.pagerAdapter.TabTitles

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val viewPager: ViewPager2 by lazy { binding.viewPager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(getAppTheme())
        setContentView(binding.root)
        viewPager.adapter = SectionsPagerAdapter(this)

        TabLayoutMediator(binding.tabs, viewPager) { tab, position ->
            tab.text = getString(TabTitles.values()[position].tabResources)
            viewPager.setCurrentItem(tab.position, true)
        }.attach()
    }
}