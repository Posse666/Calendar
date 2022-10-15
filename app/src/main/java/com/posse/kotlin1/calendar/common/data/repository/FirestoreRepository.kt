package com.posse.kotlin1.calendar.common.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.posse.kotlin1.calendar.common.data.model.Documents
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun <T> changeItem(collection: String, document: Documents, data: T, delete: Boolean): Boolean {
        return try {
            val documentToChange = firestore
                .collection(collection)
                .document(document.value)

            val value: Any? = if (delete) FieldValue.delete() else data

            documentToChange.set(mapOf(Pair(data.toString(), value)), SetOptions.merge()).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}