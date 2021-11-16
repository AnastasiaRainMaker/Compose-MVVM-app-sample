package com.example.composemvvm.ui.theme

data class WeatherUiModel (
    var city: String = "",
    var weather: String = "",
    var conditions: String = "",
    var forecastForWeek: List<WeatherForWeekItem> = listOf()
)

data class WeatherForWeekItem(val day: String, val temperature: String)