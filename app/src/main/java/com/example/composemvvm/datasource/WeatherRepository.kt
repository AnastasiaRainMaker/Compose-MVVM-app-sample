package com.example.composemvvm.datasource

import com.example.composemvvm.datasource.networking.NetworkingService
import javax.inject.Inject

class WeatherRepository @Inject constructor(private val networkingService: NetworkingService) {

    suspend fun fetchWeather(long: Double, lat: Double) =
        networkingService.fetchWeather(lon = long, lat = lat)

}