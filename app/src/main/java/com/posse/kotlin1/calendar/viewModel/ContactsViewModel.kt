package com.posse.kotlin1.calendar.viewModel

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.posse.kotlin1.calendar.model.Contact
import com.posse.kotlin1.calendar.model.Friend
import com.posse.kotlin1.calendar.model.User
import com.posse.kotlin1.calendar.model.repository.Documents
import com.posse.kotlin1.calendar.model.repository.Repository
import com.posse.kotlin1.calendar.model.repository.RepositoryFirestoreImpl.Companion.COLLECTION_USERS
import com.posse.kotlin1.calendar.utils.nickName
import com.posse.kotlin1.calendar.utils.toDataClass
import javax.inject.Inject

class ContactsViewModel @Inject constructor(
    private val repository: Repository,
    private val sharedPreferences: SharedPreferences,
) : ViewModel() {

    private val sharedData: HashSet<Contact> = hashSetOf()
    private lateinit var email: String
    private val liveDataToObserve: MutableLiveData<Pair<Boolean, Set<Contact>>> =
        MutableLiveData(Pair(false, emptySet()))

    fun getLiveData() = liveDataToObserve

    fun setContacts(
        email: String,
        contacts: List<Contact>,
        callback: (ContactStatus) -> Unit
    ) {
        this.email = email
        liveDataToObserve.value = Pair(false, emptySet())
        sharedData.clear()
        sharedData.addAll(contacts)
        repository.getData(Documents.Share, email) { contactsCollection, _ ->
            repository.getData(Documents.Users, COLLECTION_USERS) { usersCollection, isOffline ->
                try {
                    contactsCollection?.values?.forEach { contactMap ->
                        @Suppress("UNCHECKED_CAST")
                        val contact = (contactMap as Map<String, Any>).toDataClass<Contact>()
                        contact.notInContacts = !sharedData.contains(contact)
                        if (!sharedData.add(contact)) {
                            sharedData.remove(contact)
                            sharedData.add(contact)
                        }
                    }
                    usersCollection?.forEach { userMap ->
                        @Suppress("UNCHECKED_CAST")
                        val user = (userMap.value as Map<String, Any>).toDataClass<User>()
                        val contact =
                            Contact(
                                mutableListOf(user.nickname),
                                user.email,
                                notInContacts = true,
                                notInBase = false
                            )
                        sharedData.add(contact)
                        sharedData.forEach {
                            if (it.email == contact.email) it.notInBase = false
                        }
                    }
                    liveDataToObserve.value = Pair(true, sharedData)
                    if (isOffline) callback.invoke(ContactStatus.Offline)
                } catch (e: Exception) {
                    callback.invoke(ContactStatus.Error)
                }
            }
        }
    }

    fun contactClicked(contact: Contact, callback: (ContactStatus) -> Unit) {
        repository.getData(
            Documents.Friends,
            contact.email
        ) { contactFriendsCollection, isOffline ->
            var newContact: Contact = contact
            sharedData.forEach { sharedContact ->
                if (sharedContact.email == contact.email) {
                    newContact = sharedContact.copy()
                    try {
                        @Suppress("UNCHECKED_CAST")
                        val youInContactFriends =
                            (contactFriendsCollection?.get(email) as Map<String, Any>?)?.toDataClass<Friend>()
                                ?: Friend(
                                    sharedPreferences.nickName ?: email,
                                    email,
                                    selected = false,
                                    blocked = false,
                                    contactFriendsCollection?.size ?: Int.MAX_VALUE
                                )
                        if (youInContactFriends.blocked) callback.invoke(ContactStatus.Blocked)
                        else {
                            @Suppress("UNCHECKED_CAST")
                            newContact.selected = !newContact.selected
                            if (newContact.selected) {
                                repository.saveItem(Documents.Share, email, newContact)
                                repository.saveItem(
                                    Documents.Friends,
                                    newContact.email,
                                    youInContactFriends
                                )
                            } else {
                                repository.removeItem(Documents.Share, email, newContact)
                                repository.removeItem(
                                    Documents.Friends,
                                    newContact.email,
                                    youInContactFriends
                                )
                            }
                        }
                    } catch (e: Exception) {
                        callback.invoke(ContactStatus.Error)
                    }
                }
            }
            sharedData.remove(newContact)
            sharedData.add(newContact)
            liveDataToObserve.value = Pair(true, sharedData)
            if (isOffline) callback.invoke(ContactStatus.Offline)
        }
    }

    enum class ContactStatus {
        Blocked,
        Offline,
        Error
    }
}