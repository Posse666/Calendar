package com.posse.kotlin1.calendar.model.repository

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.posse.kotlin1.calendar.app.App
import com.posse.kotlin1.calendar.model.CalendarDayData
import com.posse.kotlin1.calendar.utils.convertLocalDateToLong
import com.posse.kotlin1.calendar.utils.convertLongToLocalDale
import java.time.LocalDate
import java.util.*

private const val DATE = "Date"
private const val COLLECTION = "Dates"
private const val FIELD_USER_EMAIL = "UserEmail"

class RepositoryFirestoreImpl(private var userEmail: String) : Repository {

    private val liveDataToObserve: MutableLiveData<HashMap<LocalDate, CalendarDayData>> =
        MutableLiveData()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var collection: CollectionReference? = null
    private val data: HashMap<LocalDate, CalendarDayData> = hashMapOf()
    private val auth = FirebaseAuth.getInstance()
    private val user = auth.currentUser

    override fun getLiveData(): LiveData<HashMap<LocalDate, CalendarDayData>> = liveDataToObserve

    init {
        firestore.firestoreSettings = FirebaseFirestoreSettings
            .Builder()
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build()
        collection = firestore.collection(COLLECTION)

        if (user == null) {
            auth.signInAnonymously().addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("TAG", "signInAnonymously:success")
                    Toast.makeText(
                        App.appInstance,
                        "Authentication successful.",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    updateBD(userEmail)
                } else {
                    Log.w("TAG", "signInAnonymously:failure", it.exception)
                    Toast.makeText(App.appInstance, "Authentication failed.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        } else {
            user.email?.let { updateBD(it) }
        }
    }

    private fun updateBD(userEmail: String) {
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
                data[date] = (CalendarDayData(document.id, userEmail, dateLong))
            }
            liveDataToObserve.value = data
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
