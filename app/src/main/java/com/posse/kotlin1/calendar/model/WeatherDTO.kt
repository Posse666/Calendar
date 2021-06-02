package com.posse.kotlin1.calendar.model

data class WeatherDTO(
    val main: FactDTO?
)

data class FactDTO(
    val temp: Double?
)