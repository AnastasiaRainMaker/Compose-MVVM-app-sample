package com.example.composemvvm.datasource.networking

import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkingService {

    @GET("data/2.5/onecall?exclude=minutely,hourly,alerts&lang=en")
    suspend fun fetchWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appid: String = "4596df18d7f3e4389a40371e52e0ef9c"
    ): Any

}