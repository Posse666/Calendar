package com.posse.kotlin1.calendar.firebaseMessagingService

import com.posse.kotlin1.calendar.BuildConfig
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface FirebaseAPI {
    @Headers("Content-Type: application/json", "Authorization: key=${BuildConfig.LEGACY_SERVER_KEY}")
    @POST("/fcm/send")
    fun sendMessage(@Body message: Message): Call<Message>
}