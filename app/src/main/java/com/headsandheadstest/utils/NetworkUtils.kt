package com.headsandheadstest.utils

import com.headsandheadstest.entities.WeatherAnswer
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query

const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
const val API_KEY = "d35ae8bae04069cf269e971bcc9c1a58"
const val LAT = 59.902611
const val LON = 30.271544
const val UNITS = "metric"

fun getNetworkService(): NetworkService {
    val logger = HttpLoggingInterceptor()
    logger.level = HttpLoggingInterceptor.Level.HEADERS
    val okHttpClient = OkHttpClient.Builder().addInterceptor(logger).build()
    val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()
    return retrofit.create()
}

interface NetworkService {
    @GET("weather/")
    suspend fun sendAuth(
        @Query("lon") lon: Double = LON,
        @Query("lat") lat: Double = LAT,
        @Query("appid") appid: String = API_KEY,
        @Query("units") units: String = UNITS
    ): Response<WeatherAnswer>
}