package com.example.composemvvm.ui.theme

data class WeatherUiModel (
    var city: String = "",
    var weather: String = "",
    var forecastForWeek: List<String> = listOf()
)