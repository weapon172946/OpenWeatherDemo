package com.devakash.weathercompose.misc

import java.text.SimpleDateFormat
import java.util.*

object Utils {
    const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    const val API_KEY = "a5eeb9e8d254e764d67b7cedc6fe5f5e"


    fun getFormatTimeWithTZ(timestamp: String):String {
        val timeZoneDate = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
        return timeZoneDate.format(Date(timestamp.toLong()*1000))
    }

}