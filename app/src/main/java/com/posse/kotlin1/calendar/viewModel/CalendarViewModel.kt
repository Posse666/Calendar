package com.posse.kotlin1.calendar.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.posse.kotlin1.calendar.firebaseMessagingService.Messenger
import com.posse.kotlin1.calendar.model.DataModel
import com.posse.kotlin1.calendar.model.Friend
import com.posse.kotlin1.calendar.model.User
import com.posse.kotlin1.calendar.model.repository.Documents
import com.posse.kotlin1.calendar.model.repository.Repository
import com.posse.kotlin1.calendar.model.repository.RepositoryFirestoreImpl.Companion.COLLECTION_USERS
import com.posse.kotlin1.calendar.utils.NetworkStatus
import com.posse.kotlin1.calendar.utils.convertLongToLocalDale
import com.posse.kotlin1.calendar.utils.toDataClass
import com.posse.kotlin1.calendar.view.calendar.Result
import java.time.LocalDate
import java.time.Year
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class CalendarViewModel @Inject constructor(
    private val repository: Repository,
    private val messenger: Messenger,
    private val networkStatus: NetworkStatus
) : ViewModel() {
    private val datesData: MutableSet<DataModel> = mutableSetOf()
    private lateinit var email: String
    private val liveDataToObserve: MutableLiveData<Pair<Boolean, Set<DataModel>>> =
        MutableLiveData(Pair(false, emptySet()))
    private val liveStatisticToObserve: MutableLiveData<Map<STATISTIC, Set<LocalDate>>> =
        MutableLiveData()

    fun getLiveData() = liveDataToObserve

    fun getLiveStats() = liveStatisticToObserve

    fun refreshLiveData(email: String, callback: (Result) -> Unit) {
        this.email = email
        liveDataToObserve.value = Pair(false, emptySet())
        repository.getData(Documents.Dates, email) { dates, isOffline ->
            datesData.clear()
            dates?.forEach {
                try {
                    @Suppress("UNCHECKED_CAST")
                    val day = it.value as Map<String, Any>
                    val date = day.toDataClass<DataModel>()
                    datesData.add(date)
                } catch (e: Exception) {
                    callback(Result.Error)
                }
            }
            liveDataToObserve.value = Pair(true, datesData)
            liveStatisticToObserve.value =
                getStats(datesData.map { convertLongToLocalDale(it.date) }.toSet())
            if (isOffline) callback(Result.Offline(null))
            else callback(Result.Success(null))
        }
    }

    private fun getStats(dates: Set<LocalDate>?): Map<STATISTIC, Set<LocalDate>> {
        val result = HashMap<STATISTIC, Set<LocalDate>>()
        result[STATISTIC.DRINK_DAYS_THIS_YEAR] = getDrankDaysQuantity(dates)
        result[STATISTIC.DRINK_MAX_ROW_THIS_YEAR] = getDrinkMarathon(dates, THIS_YEAR)
        result[STATISTIC.DRINK_MAX_ROW_TOTAL] = getDrinkMarathon(dates, ALL_TIME)
        result[STATISTIC.NOT_DRINK_MAX_ROW_THIS_YEAR] =
            getFreshMarathon(dates?.toHashSet(), THIS_YEAR)
        result[STATISTIC.NOT_DRINK_MAX_ROW_TOTAL] = getFreshMarathon(dates?.toHashSet(), ALL_TIME)
        return result
    }

    fun dayClicked(date: DataModel, update: () -> Unit) {
        if (date.drinkType != null) {
            datesData.add(date)
            repository.saveItem(Documents.Dates, email, date)
            if (networkStatus.isNetworkOnline())
                try {
                    sendNotification(date)
                } catch (e: Exception) {
                    update()
                }
        } else {
            datesData.remove(date)
            repository.removeItem(Documents.Dates, email, date)
        }
        liveDataToObserve.value = Pair(true, datesData)
        liveStatisticToObserve.value =
            getStats(datesData.map { convertLongToLocalDale(it.date) }.toSet())
    }

    private fun sendNotification(date: DataModel) {
        repository.getData(Documents.Share, email) { contactsCollection, _ ->
            repository.getData(Documents.Users, COLLECTION_USERS) { usersCollection, _ ->
                contactsCollection?.forEach { contactMap ->
                    repository.getData(Documents.Friends, contactMap.key) { friendsCollection, _ ->
                        val friendMap = friendsCollection?.get(email)
                        friendMap?.let {
                            @Suppress("UNCHECKED_CAST")
                            val friend = (it as Map<String, Any>).toDataClass<Friend>()
                            @Suppress("UNCHECKED_CAST")
                            val user =
                                (usersCollection?.get(contactMap.key) as Map<String, Any>).toDataClass<User>()
                            Thread {
                                try {
                                    messenger.sendPush(
                                        friend.name,
                                        date.date.toString(),
                                        user.token,
                                        date.drinkType
                                    )
                                } catch (e: Exception) {
                                }
                            }.start()
                        }
                    }
                }
            }
        }
    }

    private fun getDrankDaysQuantity(dates: Set<LocalDate>?): Set<LocalDate> {
        val result = HashSet<LocalDate>()
        val currentYear: LocalDate = LocalDate.ofYearDay(Year.now().value, 1)
        dates?.forEach {
            if (!it.isBefore(currentYear)) result.add(it)
        }
        return result
    }

    private fun getDrinkMarathon(dates: Set<LocalDate>?, isThisYear: Boolean): Set<LocalDate> {
        val days: ArrayList<LocalDate> = arrayListOf()
        val maxDays: ArrayList<LocalDate> = arrayListOf()
        val currentYear: LocalDate = LocalDate.ofYearDay(Year.now().value, 1)
        dates?.sorted()?.forEach {
            days.add(it)
            if (!days.contains(it.minusDays(1))) {
                if (isThisYear && (it.isBefore(currentYear) || days[0].isBefore(currentYear))) {
                    maxDays.clear()
                    val daysToDelete = mutableSetOf<LocalDate>()
                    for (i in 0 until days.size) {
                        if (days[i].isBefore(currentYear)) {
                            daysToDelete.add(days[i])
                        }
                    }
                    days.removeAll(daysToDelete)
                }
                if (days.size > 1) days.removeAt(days.size - 1)
                if (maxDays.size <= days.size) {
                    maxDays.clear()
                    maxDays.addAll(days)
                }
                days.clear()
                days.add(it)
                if (isThisYear && it.isBefore(currentYear)) days.clear()
            }
        }
        return if (maxDays.size > days.size) {
            maxDays.toSet()
        } else {
            days.toSet()
        }
    }

    private fun getFreshMarathon(dates: HashSet<LocalDate>?, isThisYear: Boolean): Set<LocalDate> {
        val days: ArrayList<LocalDate> = arrayListOf()
        val maxDays: ArrayList<LocalDate> = arrayListOf()
        var lastDate: LocalDate? = null
        val currentYear: LocalDate = LocalDate.ofYearDay(Year.now().value, 1)
        dates?.add(LocalDate.now())
        dates?.sorted()?.forEach sortedDates@{
            if (isThisYear) {
                if (!it.isBefore(currentYear)) {
                    if (lastDate?.isBefore(currentYear) == true) lastDate = currentYear.minusDays(1)
                } else {
                    lastDate = it
                    return@sortedDates
                }
            }
            lastDate?.let { lastDate ->
                val period = ChronoUnit.DAYS.between(lastDate, it) - 1
                days.clear()
                for (i in 0 until period) {
                    days.add(lastDate.plusDays(i + 1))
                }
            }
            lastDate = it
            if (maxDays.size <= days.size) {
                maxDays.clear()
                maxDays.addAll(days)
            }
        }
        return maxDays.toSet()
    }

    companion object {
        private const val THIS_YEAR = true
        private const val ALL_TIME = false
    }
}

enum class STATISTIC {
    DRINK_DAYS_THIS_YEAR,
    DRINK_MAX_ROW_THIS_YEAR,
    DRINK_MAX_ROW_TOTAL,
    NOT_DRINK_MAX_ROW_THIS_YEAR,
    NOT_DRINK_MAX_ROW_TOTAL
}