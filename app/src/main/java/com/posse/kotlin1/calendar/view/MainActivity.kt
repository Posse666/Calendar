package com.posse.kotlin1.calendar.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.posse.kotlin1.calendar.databinding.ActivityMainBinding
import com.posse.kotlin1.calendar.view.calendar.StatisticSwitcher
import com.posse.kotlin1.calendar.view.pagerAdapter.SectionsPagerAdapter
import com.posse.kotlin1.calendar.view.pagerAdapter.TabTitles

class MainActivity : AppCompatActivity(), StatisticSwitcher {
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        viewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter

        TabLayoutMediator(binding.tabs, viewPager) { tab, position ->
            tab.text = getString(TabTitles.values()[position].tabResources)
            viewPager.setCurrentItem(tab.position, true)
        }.attach()
    }

    override fun switchToStatistic() {
        viewPager.setCurrentItem(TabTitles.STATISTIC.position, true)
    }
}