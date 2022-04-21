package com.posse.kotlin1.calendar.common.domain.use_case

import javax.inject.Inject

data class AccountUseCases @Inject constructor(
    val getMyMail: GetMyMail
)