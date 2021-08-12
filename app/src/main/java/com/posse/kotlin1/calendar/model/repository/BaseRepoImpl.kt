package com.posse.kotlin1.calendar.model.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

abstract class BaseRepoImpl : BaseRepo {
    protected var document: DocumentReference? = null
    protected val readyData: MutableLiveData<Boolean> = MutableLiveData(false)

    init {
        FirebaseFirestore.getInstance().firestoreSettings = FirebaseFirestoreSettings
            .Builder()
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build()
    }

    override fun isDataReady(): LiveData<Boolean> = readyData
}