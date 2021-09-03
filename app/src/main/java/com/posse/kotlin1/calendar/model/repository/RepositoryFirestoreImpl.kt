package com.posse.kotlin1.calendar.model.repository

import android.util.Log
import com.google.firebase.firestore.*
import com.posse.kotlin1.calendar.model.Person
import com.posse.kotlin1.calendar.utils.convertLocalDateToLong
import com.posse.kotlin1.calendar.utils.convertLongToLocalDale
import com.posse.kotlin1.calendar.utils.isNetworkOnline
import java.time.LocalDate

const val COLLECTION_USERS = "Collection_of_all_users"

class RepositoryFirestoreImpl private constructor() : Repository {

    override fun mergeDates(oldEmail: String, newMail: String, nickName: String) {
        saveNickname(newMail, nickName)
        val oldUserDocument =
            FirebaseFirestore.getInstance().collection(oldEmail).document(DOCUMENTS.DATES.value)
        oldUserDocument.get()
            .addOnSuccessListener {
                onDatesFetchComplete(it, newMail)
                saveNickname(newMail, nickName)
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
        callback: (Map<String, Any>?, Boolean) -> Unit
    ) {
        if (!isNetworkOnline()) {
            FirebaseFirestore.getInstance().disableNetwork().addOnCompleteListener {
                getDataFromDB(collection, document, callback, true)
                FirebaseFirestore.getInstance().enableNetwork()
            }
        } else getDataFromDB(collection, document, callback, false)
    }

    private fun getDataFromDB(
        collection: String,
        document: DOCUMENTS,
        callback: (Map<String, Any>?, Boolean) -> Unit,
        isOffline: Boolean
    ) {
        val documentToFetch =
            FirebaseFirestore.getInstance().collection(collection).document(document.value)
        documentToFetch.get()
            .addOnSuccessListener { callback.invoke(it.data, isOffline) }
            .addOnFailureListener { Log.e("Firestore", it.toString()) }
    }

    override fun <T> saveItem(document: DOCUMENTS, collection: String, data: T) =
        changeItem(document, collection, data, false)

    override fun <T> removeItem(document: DOCUMENTS, collection: String, data: T) =
        changeItem(document, collection, data, true)

    override fun saveNickname(email: String, nickName: String) {
        val users = FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
            .document(DOCUMENTS.USERS.value)
        users.set(hashMapOf(email to nickName), SetOptions.merge())
    }

    override fun getNicknames(callback: (Map<String, Any>?) -> Unit) {
        if (isNetworkOnline()) {
            FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                .document(DOCUMENTS.USERS.value).get()
                .addOnSuccessListener { callback.invoke(it.data) }
        } else callback.invoke(null)
    }

    private fun <T> changeItem(document: DOCUMENTS, collection: String, data: T, delete: Boolean) {
        val documentToChange =
            FirebaseFirestore.getInstance().collection(collection).document(document.value)
        val value: Any = if (delete) FieldValue.delete()
        else when (data) {
            is Person -> data
            is String -> data
            is LocalDate -> convertLocalDateToLong(data)
            else -> throw RuntimeException("unexpected data Type. data: " + data.toString())
        }
        documentToChange.set(mapOf(Pair(data.toString(), value)), SetOptions.merge())
    }

    companion object {

        init {
            FirebaseFirestore.getInstance().firestoreSettings = FirebaseFirestoreSettings
                .Builder()
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build()
        }

        @JvmStatic
        fun newInstance() = RepositoryFirestoreImpl()
    }
}

enum class DOCUMENTS(val value: String) {
    DATES("Dates"),
    FRIENDS("Friends_List"),
    SHARE("Share_List"),
    USERS("Users")
}