package com.example.composemvvm.datasource.networking.models
import com.google.gson.annotations.SerializedName

data class WeatherApiResponse(
    @SerializedName("current")
    val current: Current
)

data class Current(
    @SerializedName("temp")
    val temp: Double,
    @SerializedName("weather")
    val weather: List<Weather>
)

data class Weather(
    @SerializedName("main")
    val main: String
)