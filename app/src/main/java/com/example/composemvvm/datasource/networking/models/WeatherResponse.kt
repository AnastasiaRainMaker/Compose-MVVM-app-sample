package com.example.composemvvm.datasource.networking.models

import com.google.gson.annotations.SerializedName

data class WeatherApiResponse(
    @SerializedName("current")
    val current: Current,
    @SerializedName("daily")
    val daily: List<Daily>
)

data class Daily(
    @SerializedName("temp")
    val temp: Temp
)

data class Temp(
    @SerializedName("day")
    val day: Double,
    @SerializedName("eve")
    val eve: Double,
    @SerializedName("max")
    val max: Double,
    @SerializedName("min")
    val min: Double,
    @SerializedName("morn")
    val morn: Double,
    @SerializedName("night")
    val night: Double
)

data class Current(
    @SerializedName("temp")
    val temp: Double
)