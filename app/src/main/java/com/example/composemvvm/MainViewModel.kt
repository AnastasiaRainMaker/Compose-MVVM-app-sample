package com.example.composemvvm

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composemvvm.datasource.WeatherRepository
import com.example.composemvvm.datasource.networking.CoroutineDispatcherProvider
import com.example.composemvvm.ui.theme.WeatherForWeekItem
import com.example.composemvvm.ui.theme.WeatherUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.util.*
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class MainViewModel @Inject constructor(
    private val repository: WeatherRepository,
    @ApplicationContext private val applicationContext: Context,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider
) : ViewModel() {

    private var city: String = "Austin, TX"
    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Empty)
    val uiState: StateFlow<WeatherUiState> = _uiState

    init {
        fetchWeather()
    }

    private fun fetchWeather() {
        _uiState.value = WeatherUiState.Loading
        viewModelScope.launch(coroutineDispatcherProvider.IO()) {
            try {
                val response = repository.fetchWeather(long = AUSTIN_LONG, lat = AUSTIN_LAT)
                var dateCounter = -1
                _uiState.value = WeatherUiState.Loaded(
                    WeatherUiModel(
                        city = city,
                        weather = "${response.current.temp.roundToInt()}°F",
                        forecastForWeek = response.daily.map {
                            dateCounter++
                            WeatherForWeekItem(
                                day = Calendar.getInstance()
                                    .also { cal -> cal.add(Calendar.DATE, dateCounter) }
                                    .getDisplayName(
                                        Calendar.DAY_OF_WEEK,
                                        Calendar.LONG,
                                        Locale.getDefault()
                                    ).orEmpty(),
                                dayTemp = "${it.temp.day}°F",
                                nightTemp = "${it.temp.night}°F"
                            )
                        }
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

    sealed class WeatherUiState {
        object Empty : WeatherUiState()
        object Loading : WeatherUiState()
        class Loaded(val data: WeatherUiModel) : WeatherUiState()
        class Error(val message: String) : WeatherUiState()
    }

    companion object {
        const val AUSTIN_LONG = "-97.733330"
        const val AUSTIN_LAT = "30.266666"
    }
}