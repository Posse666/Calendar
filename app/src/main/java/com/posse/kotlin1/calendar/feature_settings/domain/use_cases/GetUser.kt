package com.posse.kotlin1.calendar.feature_settings.domain.use_cases

import com.posse.kotlin1.calendar.common.domain.model.User
import com.posse.kotlin1.calendar.common.domain.repository.AccountRepository
import javax.inject.Inject

class GetUser @Inject constructor(
    private val accountRepository: AccountRepository
){
    operator fun invoke(): User?{
        return accountRepository.getCurrentUser()
    }
}