package com.headsandheadstest

import androidx.lifecycle.ViewModel
import com.headsandheadstest.entities.WeatherMain
import com.headsandheadstest.utils.createPublishFlow
import com.headsandheadstest.utils.getCoroutineScope
import com.headsandheadstest.utils.getNetworkService
import com.headsandheadstest.utils.validateEmail
import com.headsandheadstest.utils.validatePassword
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val weatherFlow = createPublishFlow<WeatherMain>()
    private val weatherErrorFlow = createPublishFlow<Unit>()

    var emailErrorStatus: Boolean = false
    var passwordErrorStatus: Boolean = false

    private var scope = getCoroutineScope()

    fun login() {
        scope.launch { requestCurrentWeather() }
    }

    fun initScope() {
        scope = getCoroutineScope()
    }

    fun cancelScope() {
        scope.cancel()
    }

    fun subscribeOnWeather(): Flow<WeatherMain> {
        return weatherFlow
    }

    fun subscribeOnWeatherError(): Flow<Unit> {
        return weatherErrorFlow
    }

    fun validateLoginData(email: String, password: String): Boolean {
        var validateResult = true
        if (!email.validateEmail()) {
            emailErrorStatus = true
            validateResult = false
        }

        if (!password.validatePassword()) {
            passwordErrorStatus = true
            validateResult = false
        }
        return validateResult
    }

    private suspend fun requestCurrentWeather() {
        try {
            val response = getNetworkService().sendAuth()
            if (response.isSuccessful) {
                val body = response.body() ?: run {
                    weatherErrorFlow.tryEmit(Unit)
                    return
                }
                weatherFlow.tryEmit(body.main)
            } else {
                weatherErrorFlow.tryEmit(Unit)
            }
        } catch (e: Exception) {
            weatherErrorFlow.tryEmit(Unit)
        }
    }
}