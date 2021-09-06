package com.posse.kotlin1.calendar.model

data class User(val email: String, val nickname: String, val token: String) {
    override fun toString(): String = email

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (email != other.email) return false

        return true
    }

    override fun hashCode(): Int {
        return email.hashCode()
    }
}