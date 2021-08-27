package com.posse.kotlin1.calendar.model

data class Contact(
    val names: MutableList<String>,
    override val email: String,
    var notInContacts: Boolean = false,
    var notInBase: Boolean = true,
    override var selected: Boolean = false,
    override var blocked: Boolean = false
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