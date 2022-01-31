package com.posse.kotlin1.calendar.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.posse.kotlin1.calendar.model.Contact
import com.posse.kotlin1.calendar.model.Friend
import com.posse.kotlin1.calendar.model.repository.Documents
import com.posse.kotlin1.calendar.model.repository.Repository
import com.posse.kotlin1.calendar.utils.toDataClass
import java.util.*
import javax.inject.Inject

class FriendsViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    private val friendsData: HashSet<Friend> = hashSetOf()
    private lateinit var email: String
    private val liveDataToObserve: MutableLiveData<Pair<Boolean, Set<Friend>>> =
        MutableLiveData(Pair(false, hashSetOf()))

    fun getLiveData() = liveDataToObserve

    fun refreshLiveData(email: String, callback: ((Boolean?) -> Unit)) {
        this.email = email
        liveDataToObserve.value = Pair(false, emptySet())
//        repository.getData(Documents.Friends, email) { friends, isOffline ->
            friendsData.clear()
//            try {
//                friends?.values?.forEach { friendMap ->
//                    @Suppress("UNCHECKED_CAST")
//                    val friend = (friendMap as Map<String, Any>).toDataClass<Friend>()
//                    if (!friend.blocked) friendsData.add(friend)
//                }
//            } catch (e: Exception) {
//                callback.invoke(null)
//            }
//        friendsData.add(Friend("Best Friend", "bestfriend@gmail.com", false, false, 1))
//        friendsData.add(Friend("Brother", "brother@gmail.com", false, false, 2))
//        friendsData.add(Friend("Neighbor", "neighbor@gmail.com", false, false, 3))
//        friendsData.add(Friend("Familiar", "familiar@gmail.com", false, false, 4))
//        friendsData.add(Friend("RoomMate", "roommate@gmail.com", false, false, 5))
//        friendsData.add(Friend("Some random guy", "somerandomguy@gmail.com", false, false, 6))
//        friendsData.add(Friend("Girlfriend", "girlfriend@gmail.com", false, false, 7))
        friendsData.add(Friend("Дружище", "bestfriend@gmail.com", false, false, 1))
        friendsData.add(Friend("Братюня", "brother@gmail.com", false, false, 2))
        friendsData.add(Friend("Сосед", "neighbor@gmail.com", false, false, 3))
        friendsData.add(Friend("Родственник", "familiar@gmail.com", false, false, 4))
        friendsData.add(Friend("Кореш с общаги", "roommate@gmail.com", false, false, 5))
        friendsData.add(Friend("Какой-то случайный чувак", "somerandomguy@gmail.com", false, false, 6))
        friendsData.add(Friend("Моя подруга", "girlfriend@gmail.com", false, false, 7))
            sortPositions(friendsData.toList().sortedBy { it.position })
            liveDataToObserve.value = Pair(true, friendsData)
//            if (isOffline) callback.invoke(isOffline)
//        }
    }

    private fun sortPositions(list: List<Friend>) {
        for (i in list.indices) {
            if (list[i].position != i) {
                list[i].position = i
//                repository.saveItem(Documents.Friends, email, list[i])
            }
        }
        friendsData.clear()
        friendsData.addAll(list)
    }

    fun friendSelected(friend: Friend) {
        var update = false
        var noOneIsSelected = true
        friendsData.forEach {
            if (it.selected) noOneIsSelected = false
            if (it.email == friend.email) {
                if (it.selected == friend.selected) update = true
                it.name = friend.name
                it.selected = friend.selected
//                repository.saveItem(Documents.Friends, email, it)
            } else if (friend.selected && it.selected) {
                it.selected = false
//                repository.saveItem(Documents.Friends, email, it)
                update = true
            }
        }
        if (update || noOneIsSelected) liveDataToObserve.value = Pair(true, friendsData)
    }

    fun itemMoved(fromPosition: Int, toPosition: Int) {
        friendsData.forEach {
            if (it.position == fromPosition) {
                it.position = toPosition
//                repository.saveItem(Documents.Friends, email, it)
            } else if (it.position == toPosition) {
                it.position = fromPosition
//                repository.saveItem(Documents.Friends, email, it)
            }
        }
    }

    fun changeName(friend: Friend) = Unit
//        repository.saveItem(Documents.Friends, email, friend)

    fun deleteFriend(friend: Friend, callback: ((Boolean?) -> Unit)) {
//        if (friend.blocked) {
//            repository.saveItem(Documents.Friends, email, friend)
//        } else repository.removeItem(Documents.Friends, email, friend)
//        repository.removeItem(Documents.Share, friend.email, Contact(mutableListOf(), email))
        refreshLiveData(email, callback)
    }
}