package com.posse.kotlin1.calendar.model.repository

import android.util.Log
import com.google.firebase.firestore.*
import com.google.firebase.messaging.FirebaseMessaging
import com.posse.kotlin1.calendar.app.App
import com.posse.kotlin1.calendar.model.Person
import com.posse.kotlin1.calendar.model.User
import com.posse.kotlin1.calendar.utils.*
import java.time.LocalDate

const val COLLECTION_USERS = "Collection_of_all_users"

class RepositoryFirestoreImpl private constructor() : Repository {

    override fun mergeDates(oldEmail: String, newMail: String, nickName: String) {
        val oldUserDocument =
            FirebaseFirestore.getInstance().collection(oldEmail).document(DOCUMENTS.DATES.value)
        oldUserDocument.get()
            .addOnSuccessListener {
                FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                    App.sharedPreferences.token = token
                    saveUser(User(newMail, nickName, getStringLocale(), token))
                    onDatesFetchComplete(it, newMail)
                    oldUserDocument.delete()
                }
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
            .addOnFailureListener {
                callback.invoke(null, isOffline)
                Log.e("Firestore", it.toString())
            }
    }

    override fun <T> saveItem(document: DOCUMENTS, collection: String, data: T) =
        changeItem(document, collection, data, false)

    override fun <T> removeItem(document: DOCUMENTS, collection: String, data: T) =
        changeItem(document, collection, data, true)

    override fun saveUser(user: User) {
        changeItem(DOCUMENTS.USERS, COLLECTION_USERS, user, false)
    }

    private fun <T> changeItem(document: DOCUMENTS, collection: String, data: T, delete: Boolean) {
        val documentToChange =
            FirebaseFirestore.getInstance().collection(collection).document(document.value)
        val value: Any = if (delete) FieldValue.delete()
        else when (data) {
            is Person -> data
            is String -> data
            is User -> data
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