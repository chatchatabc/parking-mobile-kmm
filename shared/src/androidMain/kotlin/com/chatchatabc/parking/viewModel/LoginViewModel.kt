package com.chatchatabc.parking.viewModel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatchatabc.parking.api.LoginAPI
import com.chatchatabc.parking.api.UserAPI
import com.chatchatabc.parking.model.dto.LoginDTO
import com.chatchatabc.parking.model.dto.OTPLoginDTO
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

// TODO: Better error handling
class LoginViewModel(
    val loginAPI: LoginAPI,
    val sharedPreferences: SharedPreferences,
): ViewModel() {
    val phone = MutableStateFlow("")
    val username = MutableStateFlow("")
    val tos = MutableStateFlow(false)
    val otp = MutableStateFlow("")

    val timer = MutableStateFlow(0)

    val requiresUsername = MutableStateFlow(false)

    fun startTimer() {
        timer.value = 60
        viewModelScope.launch {
            while (timer.value > 0) {
                timer.value -= 1
                kotlinx.coroutines.delay(1000)
            }
        }
    }

    val isLoading = MutableStateFlow(false)
    val isLoggedIn = MutableStateFlow(false)

    var hasUserDetails = false

    var uiState = MutableStateFlow(LoginState.PHONE)
    var appErrors: MutableStateFlow<Map<String, String>> = MutableStateFlow(mapOf())

    fun validateAndSubmitPhone(loginType: LoginType) {
        appErrors.value = mapOf()
        if (phone.value.length < 10) appErrors.value += mapOf("phone" to "Invalid phone number.")
        if (loginType == LoginType.MEMBER && username.value.length < 8) appErrors.value += mapOf("username" to "Invalid username.")
        if (!tos.value) appErrors.value += mapOf("tos" to "Please accept the terms of service before continuing")
        if (appErrors.value.isNotEmpty()) return
        viewModelScope.launch {
            isLoading.value = true
            try {
                with (loginAPI.login(LoginDTO(phone.value, username.value))) {
                    if (errors.isNullOrEmpty()) uiState.value = LoginState.OTP
                    else appErrors.value = mapOf("phone" to "Something went wrong. Please try again.")
                }
            } catch (e: Exception) {
                Log.d("ERROR", "Failed: ${e.message}")
                appErrors.value = mapOf("phone" to "Something went wrong. Please try again.")
            }
            isLoading.value = false
        }
    }

    fun validateAndSubmitOTP() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                with(loginAPI.verifyOTP(OTPLoginDTO(phone.value, otp.value))) {
                    if (status.isSuccess()) {
                        val token = headers["X-Access-Token"]?.also {
                            Log.d("TOKEN", it)
                            sharedPreferences.edit().putString("authToken", it).apply()
                        }

                        if (token == null) {
                            appErrors.value = mapOf("otp" to "Invalid OTP. Please try again.")
                            return@launch
                        }

                        UserAPI(loginAPI.httpClient).apply {
                            setToken(token)
                        }.getUser().let {
                            if (it.errors.isEmpty()) {
                                hasUserDetails = it.data!!.firstName != null
                                isLoggedIn.value = true
                            } else {
                                appErrors.value = mapOf("phone" to "Something went wrong. Please try again.")
                                uiState.value = LoginState.PHONE
                            }
                        }
                    } else appErrors.value = mapOf("otp" to "Invalid OTP. Please try again.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("ERROR", "Failed: ${e.message}")
                appErrors.value = mapOf("phone" to "Something went wrong. Please try again.")
                uiState.value = LoginState.PHONE
            }
            isLoading.value = false
        }
    }
}

enum class LoginType {
    ADMIN,
    MEMBER
}

enum class LoginState {
    PHONE,
    OTP
}