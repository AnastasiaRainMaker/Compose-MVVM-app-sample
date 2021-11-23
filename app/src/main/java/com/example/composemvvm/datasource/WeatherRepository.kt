package com.example.composemvvm.datasource

import com.example.composemvvm.datasource.networking.NetworkingService
import com.example.composemvvm.datasource.networking.models.WeatherApiResponse
import javax.inject.Inject

class WeatherRepository @Inject constructor(private val networkingService: NetworkingService) {

    suspend fun fetchWeather(long: String, lat: String): WeatherApiResponse =
        networkingService.fetchWeather(lon = long, lat = lat)

}