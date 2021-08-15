package com.posse.kotlin1.calendar.model

data class Friend(
    var name: String,
    override val email: String,
    override var isSelected: Boolean,
    @field:JvmField
    var isBlocked: Boolean,
    var position: Int = -1
) : Person
