package com.example.weatherlook.APIwork

import java.util.SplittableRandom
import java.util.concurrent.locks.Condition

data class WeatherModel(
    val city: String,
    val time: String,
    val condition: String,
    val currentTemp: String,
    val maxTemp: String,
    val minTemp: String,
    val imageUrl: String,
    val hours: String
)
