package com.posse.kotlin1.calendar.di.modules

import android.content.Context
import com.posse.kotlin1.calendar.BuildConfig
import com.posse.kotlin1.calendar.firebaseMessagingService.FirebaseAPI
import com.posse.kotlin1.calendar.utils.NetworkStatus
import com.posse.kotlin1.calendar.utils.NetworkStatusImpl
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
class NetworkModule {

    @Provides
    fun getNetworkStatus(context: Context): NetworkStatus = NetworkStatusImpl(context)

    @Provides
    fun getHttpClient(): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        httpClient.addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
            val key = BuildConfig.LEGACY_SERVER_KEY
            if (key.isNotEmpty()) {
                request.header("Authorization", key)
            }
            request.header("Content-Type", "application/json")
            val builder = request.method(original.method, original.body)
                .build()

            chain.proceed(builder)
        }
        return httpClient.build()
    }

    @Provides
    fun getRetrofit(httpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    fun getApiService(retrofit: Retrofit): FirebaseAPI = retrofit.create(FirebaseAPI::class.java)

    companion object {
        private const val BASE_URL = "https://fcm.googleapis.com"
    }
}