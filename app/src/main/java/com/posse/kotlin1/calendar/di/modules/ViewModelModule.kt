package com.posse.kotlin1.calendar.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.posse.kotlin1.calendar.viewModel.*
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import kotlin.reflect.KClass

@Module
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @ViewModelKey(BlackListViewModel::class)
    @IntoMap
    protected abstract fun blackListViewModel(blackListViewModel: BlackListViewModel): ViewModel

    @Binds
    @ViewModelKey(CalendarViewModel::class)
    @IntoMap
    protected abstract fun calendarViewModel(calendarViewModel: CalendarViewModel): ViewModel

    @Binds
    @ViewModelKey(ContactsViewModel::class)
    @IntoMap
    protected abstract fun contactsViewModel(contactsViewModel: ContactsViewModel): ViewModel

    @Binds
    @ViewModelKey(FriendsViewModel::class)
    @IntoMap
    protected abstract fun friendsViewModel(friendsViewModel: FriendsViewModel): ViewModel

    @Binds
    @ViewModelKey(SettingsViewModel::class)
    @IntoMap
    protected abstract fun settingsViewModel(settingsViewModel: SettingsViewModel): ViewModel
}

@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)