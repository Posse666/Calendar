package com.posse.kotlin1.calendar.viewModel

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.posse.kotlin1.calendar.common.data.model.Contact
import com.posse.kotlin1.calendar.common.domain.model.Friend
import com.posse.kotlin1.calendar.common.data.model.User
import com.posse.kotlin1.calendar.common.data.model.Documents
import com.posse.kotlin1.calendar.model.repository.Repository
import com.posse.kotlin1.calendar.model.repository.RepositoryFirestoreImpl.Companion.COLLECTION_USERS
import com.posse.kotlin1.calendar.common.domain.utils.NetworkStatus
import com.posse.kotlin1.calendar.utils.nickName
import com.posse.kotlin1.calendar.common.data.utils.toDataClass
import com.posse.kotlin1.calendar.common.domain.repository.Messenger
import javax.inject.Inject

class ContactsViewModel @Inject constructor(
    private val repository: Repository,
    private val messenger: Messenger,
    private val sharedPreferences: SharedPreferences,
    private val networkStatus: NetworkStatus
) : ViewModel() {

    private val sharedData: MutableSet<Contact> = mutableSetOf()
    private lateinit var email: String
    private val liveDataToObserve: MutableLiveData<Pair<Boolean, Set<Contact>>> =
        MutableLiveData(Pair(false, emptySet()))

    fun getLiveData(): LiveData<Pair<Boolean, Set<Contact>>> = liveDataToObserve

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
//                        contact.notInContacts = !sharedData.contains(contact)
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
                                names = mutableListOf(user.nickname),
                                email = user.email,
                                notInContacts = true,
                                notInBase = false
                            )
                        sharedData.add(contact)
                        sharedData.forEach {
//                            if (it.email == contact.email) it.notInBase = false
                        }
                    }
                    liveDataToObserve.value = Pair(true, sharedData)
                    if (isOffline) callback(ContactStatus.Offline)
                } catch (e: Exception) {
                    callback(ContactStatus.Error)
                }
            }
        }
    }

    fun contactClicked(contact: Contact, callback: (ContactStatus) -> Unit) {
        repository.getData(
            Documents.Friends,
            contact.email
        ) { contactFriendsCollection, isOffline ->
            repository.getData(Documents.Users, COLLECTION_USERS) { usersMap, _ ->
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
                                        isSelected = false,
                                        isBlocked = false,
//                                        contactFriendsCollection?.size ?: Int.MAX_VALUE
                                    )
//                            if (youInContactFriends.blocked) callback(ContactStatus.Blocked)
//                            else {
                                @Suppress("UNCHECKED_CAST")
                                (usersMap?.get(newContact.email) as Map<String, Any>).toDataClass<User>()
//                                newContact.selected = !newContact.selected
                                if (newContact.selected) {
                                    repository.saveItem(Documents.Share, email, newContact)
                                    repository.saveItem(
                                        Documents.Friends,
                                        newContact.email,
                                        youInContactFriends
                                    )
//                                    sendNotification(newContact, ADDED_YOU)
                                } else {
                                    repository.removeItem(Documents.Share, email, newContact)
                                    repository.removeItem(
                                        Documents.Friends,
                                        newContact.email,
                                        youInContactFriends
                                    )
//                                    sendNotification(newContact, REMOVED_YOU)
                                }
//                            }
                        } catch (e: Exception) {
                            callback.invoke(ContactStatus.Error)
                        }
                    }
                }
                sharedData.remove(newContact)
                sharedData.add(newContact)
                liveDataToObserve.value = Pair(true, sharedData)
                if (isOffline) callback(ContactStatus.Offline)
            }
        }
    }

    private fun sendNotification(contact: Contact, message: Long) {
        if (networkStatus.isNetworkOnline()) {
            repository.getData(Documents.Users, COLLECTION_USERS) { users, _ ->
                users?.forEach { userMap ->
                    @Suppress("UNCHECKED_CAST")
                    val user = (userMap.value as Map<String, Any>).toDataClass<User>()
                    if (user.email == contact.email){}
//                        messenger.sendPush(
//                            sharedPreferences.nickName!!,
//                            message.toString(),
//                            user.token
//                        )
                }
            }
        }
    }

    enum class ContactStatus {
        Blocked,
        Offline,
        Error
    }
}