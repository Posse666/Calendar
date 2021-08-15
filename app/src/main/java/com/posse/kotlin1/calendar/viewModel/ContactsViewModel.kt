package com.posse.kotlin1.calendar.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.posse.kotlin1.calendar.model.repository.Repository
import com.posse.kotlin1.calendar.model.repository.RepositoryFirestoreImpl
import com.posse.kotlin1.calendar.model.Contact

class ContactsViewModel : ViewModel() {
    private val repository: Repository = RepositoryFirestoreImpl
    private val liveDataToObserve: LiveData<Pair<Boolean, Set<Contact>>> =
        Transformations.map(repository.getSharedLiveData()) { it }

    fun getLiveData() = liveDataToObserve

    fun setContacts(contacts: List<Contact>) = repository.setContacts(contacts)

    fun contactClicked(contact: Contact) = repository.contactClicked(contact)
}