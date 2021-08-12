package com.posse.kotlin1.calendar.model.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.posse.kotlin1.calendar.model.Friend
import com.posse.kotlin1.calendar.utils.toDataClass
import java.util.*

private const val DOCUMENT = "Friends_List"

class FriendsRepoImpl : BaseRepoImpl(), FriendsRepo {
    private val data: HashSet<Friend> = hashSetOf()
    private val liveDataToObserve: MutableLiveData<HashSet<Friend>> = MutableLiveData()

    override fun getLiveData() = liveDataToObserve

    override fun refreshData() {
        liveDataToObserve.value = (data.map { it.copy() }).toHashSet()
    }

    override fun saveFriend(friend: Friend) {
        var update = false
        var falseCount = 0
        data.forEach {
            if (!it.isSelected) falseCount++
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
        if (update || falseCount == data.size) liveDataToObserve.value = data
    }

    override fun changePosition(fromPosition: Int, toPosition: Int) {
        data.forEach {
            if (it.position == fromPosition) {
                it.position = toPosition
                saveChanges(it)
            } else if (it.position == toPosition) {
                it.position = fromPosition
                saveChanges(it)
            }
        }
    }

    override fun deleteItem(friend: Friend) {
        if (friend.isBlocked) saveChanges(friend)
        else removeItem(friend)
        updateDB()
    }

    private fun removeItem(friend: Friend) {
        document?.set(mapOf(Pair(friend.email, FieldValue.delete())), SetOptions.merge())
    }

    private fun saveChanges(friend: Friend) {
        document?.set(mapOf(Pair(friend.email, friend)), SetOptions.merge())
    }

    override fun switchCollection(email: String) {
        document = FirebaseFirestore.getInstance().collection(email).document(DOCUMENT)
        testingDELETE()
        updateDB()
    }

    private fun testingDELETE() {
        var friend: Friend
        for (i in 1..22) {
            friend = Friend("Друг_$i", "koresh_$i@mail.ru", false, false, i - 1)
            document?.set(mapOf(Pair(friend.email, friend)), SetOptions.merge())
        }
    }

    private fun updateDB() {
        readyData.value = false
        document?.let { document ->
            document
                .get()
                .addOnSuccessListener {
                    onFetchComplete(it)
                }
                .addOnFailureListener { Log.e("Firestore", it.toString()) }
        }
    }

    private fun onFetchComplete(documentSnapshot: DocumentSnapshot) {
        data.clear()
        val values = documentSnapshot.data?.values
        values?.forEach { friendMap ->
            val friend = (friendMap as Map<String, Any>).toDataClass<Friend>()
            if (!friend.isBlocked) data.add(friend)
        }
        sortPositions(data.toList().sortedBy { it.position })
        liveDataToObserve.value = data
        readyData.value = true
    }

    private fun sortPositions(list: List<Friend>) {
        for (i in list.indices) {
            if (list[i].position != i) {
                list[i].position = i
                saveChanges(list[i])
            }
        }
        data.clear()
        data.addAll(list)
    }
}