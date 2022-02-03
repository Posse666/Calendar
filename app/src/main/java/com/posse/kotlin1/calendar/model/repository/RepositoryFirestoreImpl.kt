package com.posse.kotlin1.calendar.model.repository

import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.firestore.*
import com.google.firebase.messaging.FirebaseMessaging
import com.posse.kotlin1.calendar.model.User
import com.posse.kotlin1.calendar.utils.NetworkStatus
import com.posse.kotlin1.calendar.utils.convertLongToLocalDale
import com.posse.kotlin1.calendar.utils.token
import javax.inject.Inject

class RepositoryFirestoreImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val networkStatus: NetworkStatus,
) : Repository {

    init {
        FirebaseFirestore.getInstance().firestoreSettings = FirebaseFirestoreSettings
            .Builder()
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build()
    }

    override fun mergeDates(oldEmail: String, newMail: String, nickName: String) {
        val oldUserDocument =
            FirebaseFirestore.getInstance().collection(oldEmail).document(Documents.Dates.value)
        oldUserDocument.get()
            .addOnSuccessListener {
                FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                    sharedPreferences.token = token
                    saveUser(User(newMail, nickName, token))
                    onDatesFetchComplete(it, newMail)
                    oldUserDocument.delete()
                }
            }
            .addOnFailureListener { Log.e("Firestore", it.toString()) }
    }

    private fun onDatesFetchComplete(documentSnapshot: DocumentSnapshot, newMail: String) {
        documentSnapshot.data?.forEach {
            saveItem(Documents.Dates, newMail, convertLongToLocalDale(it.value as Long))
        }
    }

    override fun getData(
        document: Documents,
        collection: String,
        callback: (Map<String, Any>?, Boolean) -> Unit
    ) {
        if (networkStatus.isNetworkOnline()) getDataFromDB(collection, document, callback, false)
        else FirebaseFirestore.getInstance().disableNetwork().addOnCompleteListener {
            getDataFromDB(collection, document, callback, true)
            FirebaseFirestore.getInstance().enableNetwork()
        }
    }

    private fun getDataFromDB(
        collection: String,
        document: Documents,
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

    override fun <T> saveItem(document: Documents, collection: String, data: T) =
        changeItem(document, collection, data, false)

    override fun <T> removeItem(document: Documents, collection: String, data: T) =
        changeItem(document, collection, data, true)

    override fun saveUser(user: User) {
        changeItem(Documents.Users, COLLECTION_USERS, user, false)
    }

    private fun <T> changeItem(document: Documents, collection: String, data: T, delete: Boolean) {
        val documentToChange =
            FirebaseFirestore.getInstance().collection(collection).document(document.value)
        val value: Any? = if (delete) FieldValue.delete()
        else data
        documentToChange.set(mapOf(Pair(data.toString(), value)), SetOptions.merge())
    }

    companion object {
        const val COLLECTION_USERS = "Collection_of_all_users"
    }
}

