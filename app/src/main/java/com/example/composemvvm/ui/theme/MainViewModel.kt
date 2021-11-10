package com.example.composemvvm.ui.theme

import android.app.Application
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composemvvm.datasource.WeatherRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val repository: WeatherRepository,
    @ApplicationContext private val applicationContext: Application
) : ViewModel() {

    //val weatherState = MutableStateFlow<WeatherUiModel>()

    fun fetchWeather(zipCode: String) {
        val longLat = convertToLongLat(zipCode)
        if (longLat.first != null && longLat.second != null) {
            viewModelScope.launch {
                repository.fetchWeather(long = longLat.first!!, lat = longLat.second!!)
            }
        }
    }

    private fun convertToLongLat(zipCode: String): Pair<Double?, Double?> {
        val address = Geocoder(applicationContext, Locale.getDefault()).getFromLocationName(
            zipCode,
            1
        ).takeIf { it.isNotEmpty() }?.get(0)
        return Pair(address?.longitude, address?.latitude)
    }

    sealed class WeatherUiState {
        object Loading : WeatherUiState()
        class Loaded(val data: WeatherUiModel) : WeatherUiState()
        class Error(val message: String) : WeatherUiState()
    }
}