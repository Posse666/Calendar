package com.posse.kotlin1.calendar.model

data class Friend(
    var name: String,
    val email: String,
    @field:JvmField
    var isSelected: Boolean,
    @field:JvmField
    var isBlocked: Boolean,
    var position: Int = -1
)
