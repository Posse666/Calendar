package com.posse.kotlin1.calendar.model.repository

import android.util.Log
import com.google.firebase.firestore.*
import com.posse.kotlin1.calendar.model.Person
import com.posse.kotlin1.calendar.utils.convertLocalDateToLong
import com.posse.kotlin1.calendar.utils.convertLongToLocalDale
import java.time.LocalDate

const val COLLECTION_USERS = "Collection_of_all_users"

class RepositoryFirestoreImpl : Repository {

    init {
        FirebaseFirestore.getInstance().firestoreSettings = FirebaseFirestoreSettings
            .Builder()
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build()
    }

    override fun mergeDates(oldEmail: String, newMail: String, nickName: String) {
        val users = FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
            .document(DOCUMENTS.USERS.value)
        users.set(hashMapOf(newMail to nickName), SetOptions.merge())
        val oldUserDocument =
            FirebaseFirestore.getInstance().collection(oldEmail).document(DOCUMENTS.DATES.value)
        oldUserDocument.get()
            .addOnSuccessListener {
                onDatesFetchComplete(it, newMail)
                oldUserDocument.delete()
            }
            .addOnFailureListener { Log.e("Firestore", it.toString()) }
    }

    private fun onDatesFetchComplete(documentSnapshot: DocumentSnapshot, newMail: String) {
        documentSnapshot.data?.forEach {
            saveItem(DOCUMENTS.DATES, newMail, convertLongToLocalDale(it.value as Long))
        }
    }

    override fun getData(
        document: DOCUMENTS,
        collection: String,
        callback: (Map<String, Any>?) -> Unit
    ) {
        val documentToFetch =
            FirebaseFirestore.getInstance().collection(collection).document(document.value)
        documentToFetch.get()
            .addOnSuccessListener { callback.invoke(it.data) }
            .addOnFailureListener { Log.e("Firestore", it.toString()) }
    }

    override fun <T> saveItem(document: DOCUMENTS, collection: String, data: T) =
        changeItem(document, collection, data, false)

    override fun <T> removeItem(document: DOCUMENTS, collection: String, data: T) =
        changeItem(document, collection, data, true)

    private fun <T> changeItem(document: DOCUMENTS, collection: String, data: T, delete: Boolean) {
        val documentToChange =
            FirebaseFirestore.getInstance().collection(collection).document(document.value)
        val value: Any = if (delete) FieldValue.delete()
        else when (data) {
            is Person -> {
                data
            }
            is LocalDate -> {
                convertLocalDateToLong(data)
            }
            else -> throw RuntimeException("unexpected data Type. data: " + data.toString())
        }
        documentToChange.set(mapOf(Pair(data.toString(), value)), SetOptions.merge())
    }
}

enum class DOCUMENTS(val value: String) {
    DATES("Dates"),
    FRIENDS("Friends_List"),
    SHARE("Share_List"),
    USERS("Users")
}