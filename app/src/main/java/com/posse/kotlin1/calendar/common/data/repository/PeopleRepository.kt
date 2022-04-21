package com.posse.kotlin1.calendar.common.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.posse.kotlin1.calendar.common.data.model.Documents
import com.posse.kotlin1.calendar.common.data.utils.toDataClass
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PeopleRepository @Inject constructor(
    val firestore: FirebaseFirestore
) {
    suspend inline fun <reified T> getPeople(collection: String, document: Documents): List<T> {
        val snapshot = firestore
            .collection(collection)
            .document(document.value)
            .get()
            .await()

        return try {
            snapshot.data?.values?.map {
                @Suppress("UNCHECKED_CAST")
                (it as Map<String, Any>).toDataClass()
            } ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}