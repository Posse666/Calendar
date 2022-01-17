package com.posse.kotlin1.calendar.model.repository

import com.posse.kotlin1.calendar.model.User

interface Repository {
    fun getData(
        document: Documents,
        collection: String,
        callback: (Map<String, Any>?, Boolean) -> Unit
    )

    fun mergeDates(oldEmail: String, newMail: String, nickName: String)
    fun <T> saveItem(document: Documents, collection: String, data: T)
    fun <T> removeItem(document: Documents, collection: String, data: T)
    fun saveUser(user: User)
}