package com.posse.kotlin1.calendar.common.domain.use_case

import com.posse.kotlin1.calendar.common.domain.repository.AccountRepository

class GetMyMail(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(): String?{
        return accountRepository.getMyMail()
    }
}