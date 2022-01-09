package com.example.composemvvm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composemvvm.ui.theme.ComposeMVVMTheme
import com.example.composemvvm.ui.theme.WeatherForWeekItem
import com.example.composemvvm.ui.theme.WeatherUiModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeMVVMTheme {
                CurrentWeatherHeader()
            }
        }
    }
}

@Composable
fun CurrentWeatherHeader(mainViewModel: MainViewModel = viewModel()) {
    val heightInPx = with(LocalDensity.current) { LocalConfiguration.current
        .screenHeightDp.dp.toPx()
    }
    Column(
        Modifier
            .background(
            Brush.verticalGradient(
                listOf(Color.Transparent, Color.Gray, Color.Black),
                0f,
                heightInPx * 1.1f
            )
        )
    ) {
        when (val state = mainViewModel.uiState.collectAsState().value) {
            is MainViewModel.WeatherUiState.Empty -> Text(
                text = stringResource(R.string.no_data_available),
                modifier = Modifier.padding(16.dp)
            )
            is MainViewModel.WeatherUiState.Loading ->
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            is MainViewModel.WeatherUiState.Error -> ErrorDialog(state.message)
            is MainViewModel.WeatherUiState.Loaded -> WeatherLoadedScreen(state.data)
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WeatherLoadedScreen(data: WeatherUiModel) {
    Text(
        text = data.city,
        modifier = Modifier
            .padding(start = 24.dp, top = 50.dp, end = 24.dp, bottom = 50.dp),
        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp)
    )
    Text(
        text = data.weather,
        modifier = Modifier.padding(start = 24.dp, bottom = 100.dp),
        style = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 50.sp,
            color = MaterialTheme.colors.secondary
        )
    )
    LazyVerticalGrid(
        cells = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(start = 12.dp)
    ) {
        items(items = data.forecastForWeek, itemContent = { card ->
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = card.day,
                    style = TextStyle(
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp
                    ),
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                )
                Row {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_day),
                        contentDescription = stringResource(R.string.day_temp),
                        tint = MaterialTheme.colors.secondary,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(24.dp)
                    )
                    Text(
                        text = card.dayTemp,
                        style = TextStyle(
                            fontSize = 16.sp
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Row {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_night),
                        contentDescription = stringResource(R.string.day_temp),
                        tint = MaterialTheme.colors.secondary,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(24.dp)
                    )
                    Text(
                        text = card.nightTemp,
                        style = TextStyle(
                            fontSize = 16.sp
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Divider()
            }
        })
    }
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
        Column(
            Modifier
                .height(1000.dp)
                .width(400.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Gray, Color.Black)
                    )
                )
        ) {
            WeatherLoadedScreen(
                WeatherUiModel(
                    "Austin, TX",
                    weather = "75F",
                    forecastForWeek = arrayListOf(
                        WeatherForWeekItem("Monday", "65", "40"),
                        WeatherForWeekItem("Tuesday", "70", "20"),
                        WeatherForWeekItem("Monday", "65", "40"),
                        WeatherForWeekItem("Tuesday", "70", "20"),
                        WeatherForWeekItem("Monday", "65", "40"),
                        WeatherForWeekItem("Tuesday", "70", "20")
                    )
                )
            )
        }
    }
}