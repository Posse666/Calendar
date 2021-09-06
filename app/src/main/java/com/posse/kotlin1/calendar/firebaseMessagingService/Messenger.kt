package com.posse.kotlin1.calendar.firebaseMessagingService

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Messenger {

    private val firebaseAPI = Retrofit.Builder()
        .baseUrl("https://fcm.googleapis.com")
        .client(OkHttpClient.Builder().build())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(FirebaseAPI::class.java)

    fun sendPush(title: String, id: String) {

        val notifyData = NotifyData(title, "")
        firebaseAPI.sendMessage(Message(id, notifyData)).execute()
    }
}