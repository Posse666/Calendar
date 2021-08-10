package com.posse.kotlin1.calendar.model.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.posse.kotlin1.calendar.model.CalendarDayData
import com.posse.kotlin1.calendar.utils.convertLocalDateToLong
import com.posse.kotlin1.calendar.utils.convertLongToLocalDale
import java.time.LocalDate
import java.util.*

private const val DATE = "Date"
private const val COLLECTION = "Dates"
private const val FIELD_USER_EMAIL = "UserEmail"

object RepositoryFirestoreImpl : Repository {

    private val liveDataToObserve: MutableLiveData<HashMap<LocalDate, CalendarDayData>> =
        MutableLiveData()
    private val readyData: MutableLiveData<Boolean> = MutableLiveData(false)
    private var collection: CollectionReference? = null
    private val data: HashMap<LocalDate, CalendarDayData> = hashMapOf()
    private lateinit var userEmail: String

    init {
        FirebaseFirestore.getInstance().firestoreSettings = FirebaseFirestoreSettings
            .Builder()
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build()
        collection = FirebaseFirestore.getInstance().collection(COLLECTION)
    }

    override fun getLiveData(): LiveData<HashMap<LocalDate, CalendarDayData>> = liveDataToObserve

    override fun isDataReady(): LiveData<Boolean> = readyData

    override fun changeEmail(oldMail: String, newMail: String) {
        collection?.let { collection ->
            collection.whereEqualTo(FIELD_USER_EMAIL, oldMail)
                .orderBy(DATE, Query.Direction.ASCENDING).get()
                .addOnCompleteListener {
                    onFetchComplete(it, newMail)
                }
                .addOnFailureListener {
                    onFetchFailed(it)
                }
        }
    }

    private fun onFetchComplete(
        task: Task<QuerySnapshot>,
        newMail: String
    ) {
        if (task.isSuccessful) {
            task.result?.forEach { document ->
                document.reference.update(FIELD_USER_EMAIL, newMail)
            }
            updateEmail(newMail)
        }
    }

    override fun updateEmail(email: String) {
        readyData.value = false
        data.clear()
        userEmail = email
        updateDB(email)
    }

    private fun updateDB(userEmail: String) {
        collection?.let { collection ->
            collection.whereEqualTo(FIELD_USER_EMAIL, userEmail)
                .orderBy(DATE, Query.Direction.ASCENDING).get()
                .addOnCompleteListener {
                    onFetchComplete(it)
                }
                .addOnFailureListener {
                    onFetchFailed(it)
                }
        }
    }

    private fun onFetchFailed(exception: Exception) {
        Log.e("Firestore", exception.toString())
    }

    private fun onFetchComplete(
        task: Task<QuerySnapshot>
    ) {
        if (task.isSuccessful) {
            task.result?.forEach { document ->
                val dateLong = document.getLong(DATE) ?: return@forEach
                val date = convertLongToLocalDale(dateLong)
                if (data.containsKey(date)) deleteDate(date)
                data[date] = (CalendarDayData(document.id, userEmail, dateLong))
            }
            liveDataToObserve.value = data
            readyData.value = true
        }
    }

    override fun changeState(date: LocalDate) {
        if (!checkDate(date))
            collection?.let {
                it.add(getFields(getCalendarDayData(date)))
                data[date] = (CalendarDayData(it.id, userEmail, convertLocalDateToLong(date)))
                liveDataToObserve.value = data
            }
        else {
            deleteDate(date)
        }
    }

    private fun deleteDate(date: LocalDate) {
        data[date]?.let {
            collection?.document(it.id)?.delete()
            data.remove(date)
            liveDataToObserve.value = data
        }
    }

    private fun getCalendarDayData(date: LocalDate): CalendarDayData {
        return CalendarDayData("", userEmail, convertLocalDateToLong(date))
    }

    private fun getFields(calendarDayData: CalendarDayData): Map<String, Any> {
        val fields = HashMap<String, Any>()
        fields[DATE] = calendarDayData.date
        fields[FIELD_USER_EMAIL] = calendarDayData.email
        return Collections.unmodifiableMap(fields)
    }

    private fun checkDate(date: LocalDate): Boolean {
        return data.containsKey(date)
    }
}
