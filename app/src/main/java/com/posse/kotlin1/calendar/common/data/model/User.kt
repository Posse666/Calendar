package com.posse.kotlin1.calendar.common.data.model

data class User(
    val email: String,
    val nickname: String,
    val locale: String = "En",
    val token: String
)