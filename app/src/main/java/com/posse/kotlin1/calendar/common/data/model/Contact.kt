package com.posse.kotlin1.calendar.common.data.model

data class Contact(
    val names: MutableList<String>,
    val notInContacts: Boolean = false,
    val notInBase: Boolean = true,
    override val email: String,
    override val selected: Boolean = false,
    override val blocked: Boolean = false
) : Person {
    override fun toString(): String = email

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Contact

        if (email != other.email) return false

        return true
    }

    override fun hashCode(): Int {
        return email.hashCode()
    }
}