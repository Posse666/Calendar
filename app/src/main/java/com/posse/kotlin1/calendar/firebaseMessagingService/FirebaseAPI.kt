package com.posse.kotlin1.calendar.firebaseMessagingService

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface FirebaseAPI {
    @POST("/fcm/send")
    fun sendMessage(@Body message: Message): Call<Message>
}