package com.posse.kotlin1.calendar.model

data class Friend(
    var name: String,
    override val email: String,
    override var selected: Boolean,
    override var blocked: Boolean,
    var position: Int
) : Person {
    override fun toString(): String = email
}
