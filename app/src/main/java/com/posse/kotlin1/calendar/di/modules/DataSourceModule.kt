package com.posse.kotlin1.calendar.di.modules

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import com.posse.kotlin1.calendar.common.domain.utils.DispatcherProvider
import com.posse.kotlin1.calendar.feature_calendar.data.repository.DatesRepositoryImpl
import com.posse.kotlin1.calendar.feature_calendar.domain.repository.DatesRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Suppress("FunctionName")
@Module
class DataSourceModule {

    @Provides
    @Singleton
    fun provideDatesRepository(
        firestore: FirebaseFirestore,
        dispatcherProvider: DispatcherProvider
    ): DatesRepository {
        return DatesRepositoryImpl(firestore, dispatcherProvider)
    }

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return Firebase.firestore.apply {
            firestoreSettings = firestoreSettings {
                cacheSizeBytes = FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED
            }
        }
    }
}