package com.posse.kotlin1.calendar.model.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.posse.kotlin1.calendar.utils.convertLocalDateToLong
import com.posse.kotlin1.calendar.utils.convertLongToLocalDale
import java.time.LocalDate
import java.util.*

private const val DATES = "Dates"

object RepositoryFirestoreImpl : BaseRepoImpl(), Repository {
    private val data: HashSet<LocalDate> = hashSetOf()
    private lateinit var oldEmail: String
    private val liveDataToObserve: MutableLiveData<HashSet<LocalDate>> = MutableLiveData()

    override fun getLiveData() = liveDataToObserve

    override fun mergeData(newMail: String) {
        updateDB(newMail, true)
    }

    override fun switchCollection(email: String) {
        oldEmail = email
        updateDB(email, false)
    }

    private fun updateDB(userEmail: String, merge: Boolean) {
        readyData.value = false
        document = FirebaseFirestore.getInstance().collection(userEmail).document(DATES)
        document?.let { document ->
            document
                .get()
                .addOnSuccessListener {
                    onFetchComplete(it, merge)
                }
                .addOnFailureListener {
                    Log.e("Firestore", it.toString())
                }
        }
    }

    private fun onFetchComplete(documentSnapshot: DocumentSnapshot, merge: Boolean) {
        val dates: HashSet<LocalDate> = hashSetOf()
        documentSnapshot.data?.forEach {
            dates.add(convertLongToLocalDale(it.value as Long))
        }
        if (merge) {
            data.forEach {
                document?.set(
                    hashMapOf(it.toString() to convertLocalDateToLong(it)),
                    SetOptions.merge()
                )
            }
            dates.addAll(data)
            FirebaseFirestore.getInstance().collection(oldEmail).document(DATES).delete()
        }
        data.clear()
        data.addAll(dates)
        liveDataToObserve.value = data
        readyData.value = true
    }

    override fun changeState(date: LocalDate) {
        if (!checkDate(date)) {
            addDate(date)
        } else {
            deleteDate(date)
        }
    }

    private fun addDate(date: LocalDate) {
        data.add(date)
        document?.set(
            hashMapOf(date.toString() to convertLocalDateToLong(date)),
            SetOptions.merge()
        )
        liveDataToObserve.value = data
    }

    private fun deleteDate(date: LocalDate) {
        data.remove(date)
        document?.update(hashMapOf<String, Any>(date.toString() to FieldValue.delete()))
        liveDataToObserve.value = data
    }

    private fun checkDate(date: LocalDate): Boolean {
        return data.contains(date)
    }
}