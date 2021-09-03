package com.posse.kotlin1.calendar.model.repository

interface Repository {
    fun getData(
        document: DOCUMENTS,
        collection: String,
        callback: (Map<String, Any>?, Boolean) -> Unit
    )

    fun mergeDates(oldEmail: String, newMail: String, nickName: String)
    fun <T> saveItem(document: DOCUMENTS, collection: String, data: T)
    fun <T> removeItem(document: DOCUMENTS, collection: String, data: T)
    fun saveNickname(email: String, nickName: String)
    fun getNicknames(callback: (Map<String, Any>?) -> Unit)
}