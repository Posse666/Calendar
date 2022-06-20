package com.posse.kotlin1.calendar.feature_settings.domain.use_cases

import javax.inject.Inject

data class SettingsUseCases @Inject constructor(
     val getUser: GetUser
)