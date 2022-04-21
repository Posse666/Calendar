package com.posse.kotlin1.calendar.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.posse.kotlin1.calendar.viewModel.*
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import kotlin.reflect.KClass

@Module(
    includes = [
        DatesModule::class,
        MessengerModule::class
    ]
)
interface ViewModelModule {

    @Binds
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @ViewModelKey(BlackListViewModel::class)
    @IntoMap
    fun blackListViewModel(blackListViewModel: BlackListViewModel): ViewModel

    @Binds
    @ViewModelKey(CalendarViewModel::class)
    @IntoMap
    fun calendarViewModel(calendarViewModel: CalendarViewModel): ViewModel

    @Binds
    @ViewModelKey(ContactsViewModel::class)
    @IntoMap
    fun contactsViewModel(contactsViewModel: ContactsViewModel): ViewModel

    @Binds
    @ViewModelKey(FriendsViewModel::class)
    @IntoMap
    fun friendsViewModel(friendsViewModel: FriendsViewModel): ViewModel

    @Binds
    @ViewModelKey(SettingsViewModel::class)
    @IntoMap
    fun settingsViewModel(settingsViewModel: SettingsViewModel): ViewModel
}

@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)