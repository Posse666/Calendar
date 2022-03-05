package com.posse.kotlin1.calendar.di.modules

import com.posse.kotlin1.calendar.model.repository.Repository
import com.posse.kotlin1.calendar.model.repository.RepositoryFirestoreImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Suppress("FunctionName")
@Module
interface DataSourceModule {

    @Binds
    @Singleton
    fun bindRemoteDataSourceImpl_to_Repository(repositoryFirestoreImpl: RepositoryFirestoreImpl): Repository
}