package com.posse.kotlin1.calendar.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.posse.kotlin1.calendar.databinding.ActivityMainBinding
import com.posse.kotlin1.calendar.view.calendar.StatisticSwitcher
import com.posse.kotlin1.calendar.view.pagerAdapter.SectionsPagerAdapter
import com.posse.kotlin1.calendar.view.pagerAdapter.TabTitles


class MainActivity : AppCompatActivity(), StatisticSwitcher {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val viewPager: ViewPager2 by lazy { binding.viewPager }
    private val loadResultsReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (checkInternet(context)) {
                Toast.makeText(context, "Network ONLINE", Toast.LENGTH_LONG).show()
            } else Toast.makeText(context, "No Internet", Toast.LENGTH_LONG).show()
        }

        private fun checkInternet(context: Context): Boolean {
            val serviceManager = ServiceManager(context)
            return serviceManager.isNetworkAvailable
        }

        inner class ServiceManager(var context: Context) {
            val isNetworkAvailable: Boolean
                get() {
                    val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
                    val networkInfo = cm.activeNetworkInfo
                    return networkInfo != null && networkInfo.isConnected
                }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        viewPager.adapter = sectionsPagerAdapter

        TabLayoutMediator(binding.tabs, viewPager) { tab, position ->
            tab.text = getString(TabTitles.values()[position].tabResources)
            viewPager.setCurrentItem(tab.position, true)
        }.attach()

        registerReceiver(
            loadResultsReceiver, IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION
            )
        )
    }

    override fun switchToStatistic() {
        viewPager.setCurrentItem(TabTitles.STATISTIC.position, true)
    }
}