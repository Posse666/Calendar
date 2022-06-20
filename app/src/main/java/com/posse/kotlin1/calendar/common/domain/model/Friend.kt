package com.posse.kotlin1.calendar.common.domain.model

data class Friend(
    val name: String,
    val email: String,
    val isSelected: Boolean,
    val isBlocked: Boolean,
    val isEditable: Boolean = false
)