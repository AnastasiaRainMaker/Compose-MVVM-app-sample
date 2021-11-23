package com.example.composemvvm.ui.theme

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composemvvm.R
import com.example.composemvvm.datasource.WeatherRepository
import com.example.composemvvm.datasource.networking.CoroutineDispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.util.*
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class MainViewModel @Inject constructor(
    private val repository: WeatherRepository,
    @ApplicationContext private val applicationContext: Context,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider
) : ViewModel() {

    private var city: String = ""
    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Empty)
    val uiState: StateFlow<WeatherUiState> = _uiState

    fun fetchWeather(zipCode: String) {
        _uiState.value = WeatherUiState.Loading
        val longLat = parseAddressFromZipcode(zipCode)
        if (longLat.first != null && longLat.second != null) {
            viewModelScope.launch(coroutineDispatcherProvider.IO()) {
                try {
                    val response = repository.fetchWeather(long = longLat.first!!, lat = longLat.second!!)
                    _uiState.value = WeatherUiState.Loaded(
                        WeatherUiModel(
                            city = city,
                            weather = "${response.current.temp}°F",
                            conditions = response.current.weather[0].main
                        )
                    )
                } catch (ex: Exception) {
                    if (ex is HttpException && ex.code() == 429) {
                        onQueryLimitReached()
                    } else {
                        onErrorOccurred()
                    }
                }
            }
        } else {
            onErrorOccurred()
        }
    }

    private fun onQueryLimitReached() {
        _uiState.value = WeatherUiState.Error(
            applicationContext.getString(R.string.query_limit_reached)
        )
    }

    private fun onErrorOccurred() {
        _uiState.value = WeatherUiState.Error(
            applicationContext.getString(R.string.something_went_wrong)
        )
    }

    private fun parseAddressFromZipcode(zipCode: String): Pair<Double?, Double?> {
        val address = Geocoder(applicationContext, Locale.getDefault()).getFromLocationName(
            zipCode,
            1
        ).takeIf { it.isNotEmpty() }?.get(0)
        city = address?.getAddressLine(2).orEmpty()
        return Pair(address?.longitude, address?.latitude)
    }

    sealed class WeatherUiState {
        object Empty : WeatherUiState()
        object Loading : WeatherUiState()
        class Loaded(val data: WeatherUiModel) : WeatherUiState()
        class Error(val message: String) : WeatherUiState()
    }
}