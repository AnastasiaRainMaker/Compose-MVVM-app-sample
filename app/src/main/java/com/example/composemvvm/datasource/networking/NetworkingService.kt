package com.example.composemvvm.datasource.networking

import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkingService {

    @GET("data/2.5/onecall")
    suspend fun fetchWeather(
        @Query("lat") lat: Long,
        @Query("lon") lon: Long,
        @Query("appid") appid: String = "4596df18d7f3e4389a40371e52e0ef9c"
    ): Any

}