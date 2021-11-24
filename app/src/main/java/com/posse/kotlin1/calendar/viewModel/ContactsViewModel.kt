package com.posse.kotlin1.calendar.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.posse.kotlin1.calendar.app.App
import com.posse.kotlin1.calendar.firebaseMessagingService.ADDED_YOU
import com.posse.kotlin1.calendar.firebaseMessagingService.Messenger
import com.posse.kotlin1.calendar.firebaseMessagingService.REMOVED_YOU
import com.posse.kotlin1.calendar.model.Contact
import com.posse.kotlin1.calendar.model.Friend
import com.posse.kotlin1.calendar.model.User
import com.posse.kotlin1.calendar.model.repository.COLLECTION_USERS
import com.posse.kotlin1.calendar.model.repository.DOCUMENTS
import com.posse.kotlin1.calendar.model.repository.Repository
import com.posse.kotlin1.calendar.model.repository.RepositoryFirestoreImpl
import com.posse.kotlin1.calendar.utils.getLocale
import com.posse.kotlin1.calendar.utils.isNetworkOnline
import com.posse.kotlin1.calendar.utils.nickName
import com.posse.kotlin1.calendar.utils.toDataClass
import java.util.*

class ContactsViewModel : ViewModel() {
//    private val repository: Repository = RepositoryFirestoreImpl.newInstance()
    private val messenger: Messenger = Messenger()
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
        sharedData.add(Contact(mutableListOf("Boss"), "boss@gmail.com"))
        sharedData.add(Contact(mutableListOf("Mother"), "mother@gmail.com"))
        sharedData.add(Contact(mutableListOf("Father"), "father@gmail.com"))
        sharedData.add(Contact(mutableListOf("Brother"), "brother@gmail.com", false, false))
        sharedData.add(Contact(mutableListOf("Best Friend"), "bestfriend@gmail.com", false, false))
        sharedData.add(Contact(mutableListOf("Girlfriend"), "girlfriend@gmail.com", false, false))
        sharedData.add(Contact(mutableListOf("Room Mate"), "roommate@gmail.com", false, false))
//        sharedData.addAll(contacts)
//        repository.getData(DOCUMENTS.SHARE, email) { contactsCollection, _ ->
//            repository.getData(DOCUMENTS.USERS, COLLECTION_USERS) { usersCollection, isOffline ->
//                try {
//                    contactsCollection?.values?.forEach { contactMap ->
//                        val contact = (contactMap as Map<String, Any>).toDataClass<Contact>()
//                        contact.notInContacts = !sharedData.contains(contact)
//                        if (!sharedData.add(contact)) {
//                            sharedData.remove(contact)
//                            sharedData.add(contact)
//                        }
//                    }
//                    usersCollection?.forEach { userMap ->
//                        val user = (userMap.value as Map<String, Any>).toDataClass<User>()
//                        val contact =
//                            Contact(mutableListOf(user.nickname),
//                                user.email,
//                                notInContacts = true,
//                                notInBase = false
//                            )
//                        sharedData.add(contact)
//                        sharedData.forEach {
//                            if (it.email == contact.email) it.notInBase = false
//                        }
//                    }
                    liveDataToObserve.value = Pair(true, sharedData)
//                    if (isOffline) callback.invoke(ContactStatus.Offline)
//                } catch (e: Exception) {
//                    callback.invoke(ContactStatus.Error)
//                }
//            }
//        }
    }

    fun contactClicked(contact: Contact, callback: (ContactStatus) -> Unit) {
//        repository.getData(
//            DOCUMENTS.FRIENDS,
//            contact.email
//        ) { contactFriendsCollection, isOffline ->
//            repository.getData(DOCUMENTS.USERS, COLLECTION_USERS) { usersMap, _ ->
                var newContact: Contact = contact
                sharedData.forEach { sharedContact ->
                    if (sharedContact.email == contact.email) {
                        newContact = sharedContact.copy()
//                        try {
//                            val youInContactFriends =
//                                (contactFriendsCollection?.get(email) as Map<String, Any>?)?.toDataClass<Friend>()
//                                    ?: Friend(
//                                        App.sharedPreferences.nickName ?: email,
//                                        email,
//                                        selected = false,
//                                        blocked = false,
//                                        contactFriendsCollection?.size ?: Int.MAX_VALUE
//                                    )
//                            if (youInContactFriends.blocked) callback.invoke(ContactStatus.Blocked)
//                            else {
//                                val user =
//                                    (usersMap?.get(newContact.email) as Map<String, Any>).toDataClass<User>()
//                                val locale = getLocale(user.locale)
                                newContact.selected = !newContact.selected
//                                if (newContact.selected) {
//                                    repository.saveItem(DOCUMENTS.SHARE, email, newContact)
//                                    repository.saveItem(
//                                        DOCUMENTS.FRIENDS,
//                                        newContact.email,
//                                        youInContactFriends
//                                    )
//                                    sendNotification(newContact, ADDED_YOU, locale)
//                                } else {
//                                    repository.removeItem(DOCUMENTS.SHARE, email, newContact)
//                                    repository.removeItem(
//                                        DOCUMENTS.FRIENDS,
//                                        newContact.email,
//                                        youInContactFriends
//                                    )
//                                    sendNotification(newContact, REMOVED_YOU, locale)
//                                }
//                            }
//                        } catch (e: Exception) {
//                            callback.invoke(ContactStatus.Error)
//                        }
                    }
                }
                sharedData.remove(newContact)
                sharedData.add(newContact)
                liveDataToObserve.value = Pair(true, sharedData)
//                if (isOffline) callback.invoke(ContactStatus.Offline)
//            }
//        }
    }

//    private fun sendNotification(contact: Contact, message: Long, locale: Locale) {
//        if (isNetworkOnline()) {
//            repository.getData(DOCUMENTS.USERS, COLLECTION_USERS) { users, _ ->
//                users?.forEach { userMap ->
//                    val user = (userMap.value as Map<String, Any>).toDataClass<User>()
//                    if (user.email == contact.email) {
//                        Thread {
//                            try {
//                                messenger.sendPush(
//                                    App.sharedPreferences.nickName!!,
//                                    message.toString(),
//                                    user.token,
//                                    locale
//                                )
//                            } catch (e: Exception) {
//                            }
//                        }.start()
//                    }
//                }
//            }
//        }
//    }
}

enum class ContactStatus {
    Blocked,
    Offline,
    Error
}