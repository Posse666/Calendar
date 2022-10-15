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
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
interface RepositoryModule {

    @Binds
    @ViewModelScoped
    fun bindDatesRepository(datesRepositoryImpl: DatesRepositoryImpl): DatesRepository

    @Binds
    @ViewModelScoped
    fun bindAccountRepository(accountRepositoryImpl: AccountRepositoryImpl): AccountRepository

    @Binds
    @ViewModelScoped
    fun bindUserRepository(usersRepositoryImpl: UsersRepositoryImpl): UsersRepository

    @Binds
    @ViewModelScoped
    fun bindFriendsRepository(friendsRepositoryImpl: FriendsRepositoryImpl): FriendsRepository

}