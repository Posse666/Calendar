package com.posse.kotlin1.calendar.model.repository

import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.posse.kotlin1.calendar.app.App
import com.posse.kotlin1.calendar.model.CalendarDayData
import com.posse.kotlin1.calendar.model.DATE
import com.posse.kotlin1.calendar.utils.convertLocalDateToLong
import com.posse.kotlin1.calendar.utils.convertLongToLocalDale
import java.time.LocalDate
import java.util.*

private const val COLLECTION = "Dates"
private const val FIELD_USER_EMAIL = "UserEmail"

class LocalRepositoryFirestoreImpl(userEmail: String, private val listener: RepositoryListener) :
    LocalRepository {

    private var collection: CollectionReference? = null
    private val data: HashSet<CalendarDayData> = hashSetOf()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val user = auth.currentUser

    @Volatile
    private var deleteAll = false

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
        task: Task<QuerySnapshot>,

        ) {
        if (task.isSuccessful) {
            val tempData: LinkedList<CalendarDayData> = LinkedList<CalendarDayData>()
            task.result?.forEach { document ->
                tempData.add(
                    CalendarDayData(
                        document.id,
                        document.getLong(DATE) ?: 0,
                    )
                )
            }
            data.clear()
            data.addAll(tempData)
            tempData.clear()
            listener.onFetchComplete(getAll())
        }
    }

    private fun getAll(): Set<LocalDate> {
        val resultSet: HashSet<LocalDate> = hashSetOf()
        data.forEach {
            resultSet.add(convertLongToLocalDale(it.date))
        }
        return Collections.unmodifiableSet(resultSet)
    }

    override fun saveDate(date: LocalDate) {
        collection?.let {
            it.add(getFields(getCalendarDayData(date)))
                .addOnSuccessListener { documentReference: DocumentReference ->
                    data.add(CalendarDayData(documentReference.id, convertLocalDateToLong(date)))
                    listener.onFetchComplete(getAll())
                }
        }
    }

    private fun getCalendarDayData(date: LocalDate): CalendarDayData {
        return CalendarDayData("", convertLocalDateToLong(date))
    }

    private fun getFields(calendarDayData: CalendarDayData): Map<String, Any> {
        val fields = HashMap<String, Any>()
        fields[DATE] = calendarDayData.date
        return Collections.unmodifiableMap(fields)
    }

    override fun deleteDate(date: LocalDate) {
        val calendarDayData = data.find { it.date == convertLocalDateToLong(date) }
        calendarDayData?.let {
            collection?.document(it.id)?.delete()
            data.remove(it)
            if (!deleteAll) listener.onFetchComplete(getAll())
        }
    }

    override fun deleteAll() {
        deleteAll = true
        for (date in data) {
            deleteDate(convertLongToLocalDale(date.date))
        }
        deleteAll = false
    }

    override fun checkDate(date: LocalDate): Boolean {
        for (calendarDayData in data) {
            if (calendarDayData.date == convertLocalDateToLong(date)) {
                return true
            }
        }
        return false
    }

    override fun getDate(date: LocalDate): CalendarDayData? {
        return data.find { it.date == convertLocalDateToLong(date) }
    }
}