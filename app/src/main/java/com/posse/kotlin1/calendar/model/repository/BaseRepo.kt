package com.posse.kotlin1.calendar.model.repository

import androidx.lifecycle.LiveData

interface BaseRepo {

    fun isDataReady(): LiveData<Boolean>

    fun switchCollection(email: String)
}