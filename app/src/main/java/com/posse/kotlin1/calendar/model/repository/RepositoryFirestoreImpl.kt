package com.posse.kotlin1.calendar.model.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.*
import com.posse.kotlin1.calendar.model.Contact
import com.posse.kotlin1.calendar.model.Friend
import com.posse.kotlin1.calendar.utils.convertLocalDateToLong
import com.posse.kotlin1.calendar.utils.convertLongToLocalDale
import com.posse.kotlin1.calendar.utils.toDataClass
import java.time.LocalDate
import java.util.*

private const val DOCUMENT_DATES = "Dates"
private const val DOCUMENT_FRIENDS = "Friends_List"
private const val DOCUMENT_SHARE = "Share_List"

private const val COLLECTION_USERS = "Collection_of_all_users"
private const val DOCUMENT_USERS = "Users"

object RepositoryFirestoreImpl : Repository {
    private val datesData: HashSet<LocalDate> = hashSetOf()
    private val friendsData: HashSet<Friend> = hashSetOf()
    private val sharedData: HashSet<Contact> = hashSetOf()
    private lateinit var userEmail: String
    private var datesDocument: DocumentReference? = null
    private var friendsDocument: DocumentReference? = null
    private var shareDocument: DocumentReference? = null
    private var usersDocument: DocumentReference? = null
    private val datesLiveData: MutableLiveData<Pair<Boolean, Set<LocalDate>>> =
        MutableLiveData(Pair(false, emptySet()))
    private val friendsLiveData: MutableLiveData<Pair<Boolean, Set<Friend>>> =
        MutableLiveData(Pair(false, emptySet()))
    private val sharedLiveData: MutableLiveData<Pair<Boolean, Set<Contact>>> =
        MutableLiveData(Pair(false, emptySet()))

    init {
        FirebaseFirestore.getInstance().firestoreSettings = FirebaseFirestoreSettings
            .Builder()
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build()
        usersDocument =
            FirebaseFirestore.getInstance().collection(COLLECTION_USERS).document(DOCUMENT_USERS)
    }

    override fun getDatesLiveData() = datesLiveData

    override fun mergeDates(newMail: String, nickName: String) {
        usersDocument?.set(hashMapOf(newMail to nickName), SetOptions.merge())
        updateDB(newMail, true)
    }

    override fun switchCollection(email: String) {
        userEmail = email
        updateDB(email, false)
    }

    private fun updateDB(userEmail: String, merge: Boolean) {
        datesLiveData.value = Pair(false, emptySet())
        val collection = FirebaseFirestore.getInstance().collection(userEmail)
        friendsDocument = collection.document(DOCUMENT_FRIENDS)
        datesDocument = collection.document(DOCUMENT_DATES)
        shareDocument = collection.document(DOCUMENT_SHARE)
        datesDocument?.let { document ->
            document.get()
                .addOnSuccessListener { onDatesFetchComplete(it, merge) }
                .addOnFailureListener { Log.e("Firestore", it.toString()) }
        }
    }

    private fun onDatesFetchComplete(documentSnapshot: DocumentSnapshot, merge: Boolean) {
        val dates: HashSet<LocalDate> = hashSetOf()
        documentSnapshot.data?.forEach { dates.add(convertLongToLocalDale(it.value as Long)) }
        if (merge) {
            datesData.forEach {
                datesDocument?.set(
                    hashMapOf(it.toString() to convertLocalDateToLong(it)),
                    SetOptions.merge()
                )
            }
            dates.addAll(datesData)
            FirebaseFirestore.getInstance().collection(userEmail).document(DOCUMENT_DATES).delete()
        }
        datesData.clear()
        datesData.addAll(dates)
        datesLiveData.value = Pair(true, datesData)
    }

    override fun changeState(date: LocalDate) {
        if (!checkDate(date)) {
            addDate(date)
        } else {
            deleteDate(date)
        }
    }

    private fun addDate(date: LocalDate) {
        datesData.add(date)
        datesDocument?.set(
            hashMapOf(date.toString() to convertLocalDateToLong(date)),
            SetOptions.merge()
        )
        datesLiveData.value = Pair(true, datesData)
    }

    private fun deleteDate(date: LocalDate) {
        datesData.remove(date)
        datesDocument?.update(hashMapOf<String, Any>(date.toString() to FieldValue.delete()))
        datesLiveData.value = Pair(true, datesData)
    }

    private fun checkDate(date: LocalDate): Boolean = datesData.contains(date)

    override fun getFriendsLiveData() = friendsLiveData

    override fun saveFriend(friend: Friend) {
        var update = false
        var noOneIsSelected = true
        friendsData.forEach {
            if (it.isSelected) noOneIsSelected = false
            if (it.email == friend.email) {
                if (it.isSelected == friend.isSelected) update = true
                it.name = friend.name
                it.isSelected = friend.isSelected
                saveChanges(it)
            } else if (friend.isSelected && it.isSelected) {
                it.isSelected = false
                saveChanges(it)
                update = true
            }
        }
        if (update || noOneIsSelected) friendsLiveData.value = Pair(true, friendsData)
    }

    override fun changeFriendPosition(fromPosition: Int, toPosition: Int) {
        friendsData.forEach {
            if (it.position == fromPosition) {
                it.position = toPosition
                saveChanges(it)
            } else if (it.position == toPosition) {
                it.position = fromPosition
                saveChanges(it)
            }
        }
    }

    override fun deleteFriend(friend: Friend) {
        if (friend.isBlocked) saveChanges(friend)
        else removeItem(friend)
        updateFriendsData()
    }

    private fun removeItem(friend: Friend) {
        friendsDocument?.set(mapOf(Pair(friend.email, FieldValue.delete())), SetOptions.merge())
    }

    private fun saveChanges(friend: Friend) {
        friendsDocument?.set(mapOf(Pair(friend.email, friend)), SetOptions.merge())
    }

    override fun updateFriendsData() {
//        testingDELETE()
        friendsLiveData.value = Pair(false, emptySet())
        friendsDocument?.let { document ->
            document.get()
                .addOnSuccessListener { onFriendsFetchComplete(it) }
                .addOnFailureListener { Log.e("Firestore", it.toString()) }
        }
    }

    private fun testingDELETE() {
        var friend: Friend
        for (i in 1..22) {
            friend = Friend("Друг_$i", "koresh_$i@mail.ru", false, false, i - 1)
            friendsDocument?.set(mapOf(Pair(friend.email, friend)), SetOptions.merge())
        }
    }

    private fun onFriendsFetchComplete(documentSnapshot: DocumentSnapshot) {
        friendsData.clear()
        val values = documentSnapshot.data?.values
        values?.forEach { friendMap ->
            val friend = (friendMap as Map<String, Any>).toDataClass<Friend>()
            if (!friend.isBlocked) friendsData.add(friend)
        }
        sortPositions(friendsData.toList().sortedBy { it.position })
        friendsLiveData.value = Pair(true, friendsData)
    }

    private fun sortPositions(list: List<Friend>) {
        for (i in list.indices) {
            if (list[i].position != i) {
                list[i].position = i
                saveChanges(list[i])
            }
        }
        friendsData.clear()
        friendsData.addAll(list)
    }

    override fun getSharedLiveData(): LiveData<Pair<Boolean, Set<Contact>>> = sharedLiveData

    override fun setContacts(contacts: List<Contact>) {
        sharedLiveData.value = Pair(false, emptySet())
        sharedData.clear()
        sharedData.addAll(contacts)
        shareDocument?.let { document ->
            document.get()
                .addOnSuccessListener { onSharedFetchComplete(it) }
                .addOnFailureListener { Log.e("Firestore", it.toString()) }
        }
    }

    override fun contactClicked(contact: Contact) {

    }

    private fun onSharedFetchComplete(sharedSnapshot: DocumentSnapshot) {
        usersDocument?.let { document ->
            document.get()
                .addOnSuccessListener { onUsersFetchComplete(it, sharedSnapshot) }
                .addOnFailureListener { Log.e("Firestore", it.toString()) }
        }
    }

    private fun onUsersFetchComplete(
        usersSnapshot: DocumentSnapshot,
        sharedSnapshot: DocumentSnapshot
    ) {
        val sharedContacts = sharedSnapshot.data?.values as Collection<Map<String, Any>>?
        sharedContacts?.forEach { contactMap ->
            val contact = contactMap.toDataClass<Contact>()
            contact.notInContacts = !sharedData.contains(contact)
            if (!sharedData.add(contact)) {
                sharedData.remove(contact)
                sharedData.add(contact)
            }
        }
        val users = usersSnapshot.data as Map<String, String>?
        users?.forEach { contactMap ->
            val contact = Contact(arrayOf(contactMap.value), contactMap.key,true, false)
            sharedData.add(contact)
            sharedData.forEach {
                if (it.email == contact.email) it.notInBase = false
            }
        }
        sharedLiveData.value = Pair(true, sharedData)
    }
}