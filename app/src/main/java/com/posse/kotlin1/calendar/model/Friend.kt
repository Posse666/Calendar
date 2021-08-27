package com.posse.kotlin1.calendar.model

data class Friend(
    var name: String,
    override val email: String,
    override var selected: Boolean,
    override var blocked: Boolean,
    var position: Int
) : Person {
    override fun toString(): String = email

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Friend

        if (email != other.email) return false

        return true
    }

    override fun hashCode(): Int {
        return email.hashCode()
    }
}