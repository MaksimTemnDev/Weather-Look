package com.example.weatherlook.fragment

data class DayItem(
    val city: String,
    val time: String,
    val condition: String,
    val imageUrl: String,
    val curTemp: String,
    val maxTemp: String,
    val mintemp: String,
    val hours: String
)
