package com.example.composemvvm.ui.theme

data class WeatherUiModel(
    var city: String = "",
    var weather: String = "",
    var forecastForWeek: List<WeatherForWeekItem> = listOf()
)

data class WeatherForWeekItem(
    val day: String,
    val dayTemp: String,
    val nightTemp: String
)