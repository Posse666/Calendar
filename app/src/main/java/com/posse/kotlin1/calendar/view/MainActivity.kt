package com.posse.kotlin1.calendar.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.posse.kotlin1.calendar.databinding.ActivityMainBinding
import com.posse.kotlin1.calendar.view.pagerAdapter.SectionsPagerAdapter

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        val viewPager: ViewPager2 = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter

        TabLayoutMediator(binding.tabs, viewPager) { tab, position ->
            tab.text = sectionsPagerAdapter.tabTitles[position]
            viewPager.setCurrentItem(tab.position, true)
        }.attach()
    }
}