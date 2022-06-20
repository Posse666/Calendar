package com.posse.kotlin1.calendar.common.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.posse.kotlin1.calendar.common.data.model.Documents
import com.posse.kotlin1.calendar.common.data.utils.toDataClass
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class PeopleRepository @Inject constructor(
    val firestore: FirebaseFirestore
) {
    inline fun <reified T> getPeople(
        collection: String,
        document: Documents
    ): Flow<List<T>> = callbackFlow {
        val snapshot = firestore
            .collection(collection)
            .document(document.value)

        val subscription = snapshot.addSnapshotListener { value, _ ->
            val result = if (value?.exists() == true) {
                try {
                    value.data?.values?.map {
                        @Suppress("UNCHECKED_CAST")
                        (it as Map<String, Any>).toDataClass<T>()
                    } ?: emptyList()
                } catch (e: Exception) {
                    e.printStackTrace()
                    emptyList()
                }
            } else emptyList()

            trySend(result)
        }

        awaitClose { subscription.remove() }
    }
}