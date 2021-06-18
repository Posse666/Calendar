package com.posse.kotlin1.calendar.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.posse.kotlin1.calendar.model.repository.SettingsRepo
import com.posse.kotlin1.calendar.model.repository.SettingsRepoImpl

class SettingsViewModel : ViewModel() {
    private val liveData: MutableLiveData<SettingsState> = MutableLiveData()
    private val settingsRepo: SettingsRepo = SettingsRepoImpl()

    fun getLiveData() = liveData

    fun getSettingsState() {
        liveData.value = settingsRepo.getSettingsState()
    }
}