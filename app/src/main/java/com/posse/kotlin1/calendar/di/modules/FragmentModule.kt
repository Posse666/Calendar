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

@Module
abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeCalendarFragment(): CalendarFragment

    @ContributesAndroidInjector
    abstract fun contributeFriendsFragment(): FriendsFragment

    @ContributesAndroidInjector
    abstract fun contributeSettingsFragment(): SettingsFragment

    @ContributesAndroidInjector
    abstract fun contributeMyCalendarFragment(): MyCalendarFragment

    @ContributesAndroidInjector
    abstract fun contributeFriendListFragment(): FriendsListFragment

    @ContributesAndroidInjector
    abstract fun contributeShareFragment(): ShareFragment

    @ContributesAndroidInjector
    abstract fun contributeBlackListFragment(): BlackListFragment
}