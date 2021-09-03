package com.posse.kotlin1.calendar.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.posse.kotlin1.calendar.model.Friend
import com.posse.kotlin1.calendar.model.repository.DOCUMENTS
import com.posse.kotlin1.calendar.model.repository.Repository
import com.posse.kotlin1.calendar.model.repository.RepositoryFirestoreImpl
import com.posse.kotlin1.calendar.utils.toDataClass
import java.util.*

class BlackListViewModel : ViewModel() {
    private val repository: Repository = RepositoryFirestoreImpl.newInstance()
    private val friendsData: HashSet<Friend> = hashSetOf()
    private lateinit var email: String
    private val liveDataToObserve: MutableLiveData<Pair<Boolean, Set<Friend>>> =
        MutableLiveData(Pair(false, hashSetOf()))

    fun getLiveData() = liveDataToObserve

    fun refreshLiveData(email: String, error: () -> Unit, callback: () -> Unit) {
        this.email = email
        liveDataToObserve.value = Pair(false, emptySet())
        repository.getData(DOCUMENTS.FRIENDS, email) { friends, isOffline ->
            friendsData.clear()
            friends?.values?.forEach { friendMap ->
                try {
                    val friend = (friendMap as Map<String, Any>).toDataClass<Friend>()
                    if (friend.blocked) friendsData.add(friend)
                } catch (e: Exception) { error.invoke() }
            }
            liveDataToObserve.value = Pair(true, friendsData)
            if (isOffline) callback.invoke()
        }
    }

    fun personSelected(person: Friend) {
        friendsData.remove(person)
        repository.removeItem(DOCUMENTS.FRIENDS, email, person)
        liveDataToObserve.value = Pair(true, friendsData)
    }
}