package com.posse.kotlin1.calendar.common.data.model

data class Contact(
    val names: MutableList<String>,
    val notInContacts: Boolean = false,
    val notInBase: Boolean = true,
     val email: String,
     val selected: Boolean = false,
     val blocked: Boolean = false
)  {
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