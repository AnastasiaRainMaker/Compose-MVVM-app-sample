package com.example.composemvvm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composemvvm.ui.theme.ComposeMVVMTheme
import com.example.composemvvm.ui.theme.MainViewModel
import com.example.composemvvm.ui.theme.WeatherForWeekItem
import com.example.composemvvm.ui.theme.WeatherUiModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeMVVMTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    CurrentWeatherHeader()
                }
            }
        }
    }
}

@Composable
fun CurrentWeatherHeader(mainViewModel: MainViewModel = viewModel()) {
    Column {
        when (val state = mainViewModel.uiState.collectAsState().value) {
            is MainViewModel.WeatherUiState.Empty -> Text(stringResource(R.string.no_data_available))
            is MainViewModel.WeatherUiState.Loading -> CircularProgressIndicator()
            is MainViewModel.WeatherUiState.Error -> ErrorDialog(state.message)
            is MainViewModel.WeatherUiState.Loaded -> WeatherLoadedScreen(state.data)
        }
    }

}

@Composable
fun WeatherLoadedScreen(data: WeatherUiModel) {
    Text(
        text = data.city,
        modifier = Modifier
            .size(18.dp)
            .padding(24.dp),
        style = TextStyle(color = Color.Black, fontWeight = FontWeight.Bold)
    )
    Text(
        text = data.weather,
        modifier = Modifier
            .size(16.dp)
            .padding(24.dp),
        style = TextStyle(color = Color.DarkGray, fontWeight = FontWeight.Bold)
    )
    Spacer(modifier = Modifier.size(24.dp))
    LazyRow(modifier = Modifier.fillMaxWidth()) {
        items(items = data.forecastForWeek, itemContent = { card ->
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = card.day,
                    style = TextStyle(
                        color = Color.DarkGray, fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .size(14.dp)
                        .padding(8.dp)
                )
                Text(
                    text = card.temperature,
                    style = TextStyle(
                        color = Color.DarkGray, fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .size(14.dp)
                        .padding(8.dp)
                )
            }
        })
    }
    Spacer(modifier = Modifier.size(24.dp))
}

@Composable
fun ErrorDialog(message: String) {
    val openDialog = remember { mutableStateOf(true) }
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Text(text = stringResource(R.string.problem_occurred))
            },
            text = {
                Text(message)
            },
            confirmButton = {
                openDialog.value = false
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeMVVMTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Column {
                WeatherLoadedScreen(
                    WeatherUiModel(
                        "Austin, TX",
                        weather = "75",
                        conditions = "Clear",
                        forecastForWeek = arrayListOf(
                            WeatherForWeekItem("Monday", "65"),
                            WeatherForWeekItem("Tuesday", "70")
                        )
                    )
                )
            }
        }
    }
}