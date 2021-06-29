package com.posse.kotlin1.calendar.model.repository

import android.content.Context
import com.posse.kotlin1.calendar.viewModel.SettingsState

interface SettingsRepo {

    fun getSettingsState(): SettingsState
}