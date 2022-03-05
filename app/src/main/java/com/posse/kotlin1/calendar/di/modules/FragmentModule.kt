package com.posse.kotlin1.calendar.di.modules

import com.posse.kotlin1.calendar.view.calendar.CalendarFragment
import com.posse.kotlin1.calendar.view.friends.FriendsFragment
import com.posse.kotlin1.calendar.view.friends.list.FriendsListFragment
import com.posse.kotlin1.calendar.view.myCalendar.MyCalendarFragment
import com.posse.kotlin1.calendar.view.settings.SettingsFragment
import com.posse.kotlin1.calendar.view.settings.blackList.BlackListFragment
import com.posse.kotlin1.calendar.view.settings.share.ShareFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module(
    includes = [
        ViewModelModule::class,
        ImageModule::class
    ]
)
interface FragmentModule {

    @ContributesAndroidInjector
    fun contributeCalendarFragment(): CalendarFragment

    @ContributesAndroidInjector
    fun contributeFriendsFragment(): FriendsFragment

    @ContributesAndroidInjector
    fun contributeSettingsFragment(): SettingsFragment

    @ContributesAndroidInjector
    fun contributeMyCalendarFragment(): MyCalendarFragment

    @ContributesAndroidInjector
    fun contributeFriendListFragment(): FriendsListFragment

    @ContributesAndroidInjector
    fun contributeShareFragment(): ShareFragment

    @ContributesAndroidInjector
    fun contributeBlackListFragment(): BlackListFragment
}