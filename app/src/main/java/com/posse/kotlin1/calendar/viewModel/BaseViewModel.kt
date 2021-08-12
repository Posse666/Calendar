package com.posse.kotlin1.calendar.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.posse.kotlin1.calendar.model.repository.BaseRepo

abstract class BaseViewModel : ViewModel() {
    protected abstract val repository: BaseRepo
    private val readyData: LiveData<Boolean>
        get() = Transformations.map(repository.isDataReady()) { it }

    fun isDataReady() = readyData

    fun setEmail(email: String) {
        repository.switchCollection(email)
    }
}