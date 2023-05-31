package com.chatchatabc.parking.viewModel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatchatabc.parking.api.LoginAPI
import com.chatchatabc.parking.model.Member
import com.chatchatabc.parking.model.dto.LoginDTO
import com.chatchatabc.parking.model.dto.OTPLoginDTO
import com.chatchatabc.parking.model.response.ApiResponse
import io.ktor.client.call.body
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

// TODO: Better error handling

enum class LoginType {
    ADMIN,
    MEMBER
}

class LoginViewModel(
    val api: LoginAPI,
    val sharedPreferences: SharedPreferences
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
    var errors: MutableStateFlow<Map<String, String>> = MutableStateFlow(mapOf())

    fun validateAndSubmitPhone(loginType: LoginType) {
        errors.value = mapOf()
        if (phone.value.length < 10) errors.value = mapOf("phone" to "Invalid phone number.")
        if (loginType == LoginType.MEMBER && username.value.length < 8) errors.value = mapOf("username" to "Invalid username.")
        if (!tos.value) errors.value = mapOf("tos" to "Please accept the terms of service before continuing")
        if (errors.value.isNotEmpty()) return
        viewModelScope.launch {
            isLoading.value = true
            try {
                with (api.login(LoginDTO(phone.value, username.value))) {
                    if (!error) uiState.value = LoginState.OTP
                    else errors.value = mapOf("phone" to "Something went wrong. Please try again.")
                }
            } catch (e: Exception) {
                Log.d("ERROR", "Failed: ${e.message}")
                errors.value = mapOf("phone" to "Something went wrong. Please try again.")
            }
            isLoading.value = false
        }
    }

    fun validateAndSubmitOTP() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                with(api.verifyOTP(OTPLoginDTO(phone.value, otp.value))) {
                    if (status.isSuccess()) {
                        body<ApiResponse<Member>>().let { reponse ->
                            if (!reponse.error) {
                                headers["X-Access-Token"]?.let {
                                    sharedPreferences.edit().putString("authToken", it).apply()
                                    Log.d("TOKEN", it)
                                }
                                isLoggedIn.value = true
                                hasUserDetails = body<ApiResponse<Member>>().data!!.firstName != null
                            } else {
                                errors.value = mapOf("otp" to "Invalid OTP. Please try again.")
                            }
                        }
                    } else errors.value = mapOf("otp" to "Invalid OTP. Please try again.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("ERROR", "Failed: ${e.message}")
                errors.value = mapOf("otp" to "Something went wrong. Please try again.")
            }
            isLoading.value = false
        }
    }
}

enum class LoginState {
    PHONE,
    OTP
}