package com.posse.kotlin1.calendar.feature_calendar.data.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.posse.kotlin1.calendar.common.data.model.Documents
import com.posse.kotlin1.calendar.common.data.repository.FirestoreRepository
import com.posse.kotlin1.calendar.common.data.utils.toDataClass
import com.posse.kotlin1.calendar.common.domain.model.Response
import com.posse.kotlin1.calendar.common.domain.utils.DispatcherProvider
import com.posse.kotlin1.calendar.feature_calendar.domain.model.DayData
import com.posse.kotlin1.calendar.feature_calendar.domain.repository.DatesRepository
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DatesRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firestoreRepository: FirestoreRepository,
    private val dispatcherProvider: DispatcherProvider
) : DatesRepository {
    override fun getDates(userMail: String) = flow<Response<List<DayData>>> {

        emit(Response.Loading(null))

        val snapshot = getSnapshot(userMail)

        try {
            val result = getResult(snapshot)
            emit(Response.Success(result.toList()))
        } catch (e: Exception) {
            emit(Response.Error("Class cast Exception", null))
        }
    }.flowOn(dispatcherProvider.io)

    override suspend fun changeDate(
        userMail: String,
        day: DayData,
        shouldDelete: Boolean
    ): Boolean {
        return firestoreRepository
            .changeItem(
                collection = userMail,
                document = Documents.Dates,
                data = day,
                delete = shouldDelete
            )
    }

    private suspend fun getSnapshot(userMail: String): DocumentSnapshot {
        return firestore
            .collection(userMail)
            .document(Documents.Dates.value)
            .get()
            .await()
    }

    private fun getResult(snapshot: DocumentSnapshot): List<DayData> {
        return snapshot.data?.values?.map {
            @Suppress("UNCHECKED_CAST")
            (it as Map<String, Any>).toDataClass()
        } ?: emptyList()
    }
}