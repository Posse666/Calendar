package com.posse.kotlin1.calendar.common.domain.model

sealed class Response<T> (val data: T? = null, val message: String? = null){
    class Success<T>(data: T) : Response<T>(data)
    class Error<T>(message: String?, data: T? = null): Response<T>(data,message)
    class Loading<T> (data: T? = null): Response<T>(data)
}
