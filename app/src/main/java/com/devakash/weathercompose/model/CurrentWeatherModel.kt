package com.devakash.weathercompose.model


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CurrentWeatherModel(
    @SerializedName("base")
    val base: String, // stations
    @SerializedName("clouds")
    val clouds: Clouds,
    @SerializedName("cod")
    val cod: Int, // 200
    @SerializedName("coord")
    val coord: Coord,
    @SerializedName("dt")
    val dt: String, // 1672922255
    @SerializedName("id")
    val id: Int, // 1260107
    @SerializedName("main")
    val main: Main,
    @SerializedName("name")
    val name: String, // Patiāla
    @SerializedName("sys")
    val sys: Sys,
    @SerializedName("timezone")
    val timezone: Int, // 19800
    @SerializedName("visibility")
    val visibility: Int, // 10000
    @SerializedName("weather")
    val weather: List<Weather>,
    @SerializedName("wind")
    val wind: Wind
) {
    @Keep
    data class Clouds(
        @SerializedName("all")
        val all: Int // 0
    )

    @Keep
    data class Coord(
        @SerializedName("lat")
        val lat: Double, // 30.3398
        @SerializedName("lon")
        val lon: Double // 76.3869
    )

    @Keep
    data class Main(
        @SerializedName("feels_like")
        val feelsLike: Double, // 282.82
        @SerializedName("grnd_level")
        val grndLevel: Int, // 992
        @SerializedName("humidity")
        val humidity: Int, // 39
        @SerializedName("pressure")
        val pressure: Int, // 1022
        @SerializedName("sea_level")
        val seaLevel: Int, // 1022
        @SerializedName("temp")
        val temp: Double, // 284.6
        @SerializedName("temp_max")
        val tempMax: Double, // 284.6
        @SerializedName("temp_min")
        val tempMin: Double // 284.6
    )

    @Keep
    data class Sys(
        @SerializedName("country")
        val country: String, // IN
        @SerializedName("sunrise")
        val sunrise: Int, // 1672883501
        @SerializedName("sunset")
        val sunset: Int // 1672920425
    )

    @Keep
    data class Weather(
        @SerializedName("description")
        val description: String, // clear sky
        @SerializedName("icon")
        val icon: String, // 01n
        @SerializedName("id")
        val id: Int, // 800
        @SerializedName("main")
        val main: String // Clear
    )

    @Keep
    data class Wind(
        @SerializedName("deg")
        val deg: Int, // 303
        @SerializedName("gust")
        val gust: Double, // 2.59
        @SerializedName("speed")
        val speed: Double // 2.55
    )
}