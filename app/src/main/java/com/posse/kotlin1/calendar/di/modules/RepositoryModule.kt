package com.posse.kotlin1.calendar.di.modules

import com.posse.kotlin1.calendar.common.data.repository.AccountRepositoryImpl
import com.posse.kotlin1.calendar.common.data.repository.FriendsRepositoryImpl
import com.posse.kotlin1.calendar.common.data.repository.UsersRepositoryImpl
import com.posse.kotlin1.calendar.common.domain.repository.AccountRepository
import com.posse.kotlin1.calendar.common.domain.repository.FriendsRepository
import com.posse.kotlin1.calendar.common.domain.repository.UsersRepository
import com.posse.kotlin1.calendar.feature_calendar.data.repository.DatesRepositoryImpl
import com.posse.kotlin1.calendar.feature_calendar.domain.repository.DatesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    @Singleton
    fun bindDatesRepository(datesRepositoryImpl: DatesRepositoryImpl): DatesRepository

    @Binds
    @Singleton
    fun bindAccountRepository(accountRepositoryImpl: AccountRepositoryImpl): AccountRepository

    @Binds
    @Singleton
    fun bindUserRepository(usersRepositoryImpl: UsersRepositoryImpl): UsersRepository

    @Binds
    @Singleton
    fun bindFriendsRepository(friendsRepositoryImpl: FriendsRepositoryImpl): FriendsRepository

}