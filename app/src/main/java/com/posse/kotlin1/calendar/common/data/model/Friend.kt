package com.posse.kotlin1.calendar.common.data.model

data class Friend(
    val name: String,
    override val email: String,
    override val selected: Boolean,
    override val blocked: Boolean,
    val position: Int
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