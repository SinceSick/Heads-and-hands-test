package com.headsandheadstest.entities

import com.google.gson.annotations.SerializedName

data class WeatherAnswer(
    val main: WeatherMain
)

data class WeatherMain(
    @SerializedName("temp")
    val temperature: Double = 0.0,
    @SerializedName("feels_like")
    val feelsLike: Double = 0.0,
)