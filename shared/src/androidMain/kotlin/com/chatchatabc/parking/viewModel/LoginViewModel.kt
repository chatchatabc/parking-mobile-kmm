package com.chatchatabc.parking.viewModel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatchatabc.parking.api.LoginAPI
import com.chatchatabc.parking.api.UserAPI
import com.chatchatabc.parking.model.dto.LoginDTO
import com.chatchatabc.parking.model.dto.OTPLoginDTO
import com.chatchatabc.parking.model.response.FlowCall
import com.chatchatabc.parking.model.response.flowCall
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

// TODO: Better error handling
class LoginViewModel(
    val loginAPI: LoginAPI,
    val sharedPreferences: SharedPreferences,
): ViewModel() {
    val isLoggedIn = MutableStateFlow(false)
    val hasUserDetails = MutableStateFlow(false)

    val phone = MutableStateFlow("")
    val username = MutableStateFlow("")
    val tos = MutableStateFlow(false)
    val otp = MutableStateFlow("")

    val timer = MutableStateFlow(0)

    val requiresUsername = MutableStateFlow(false)

    val requestState = MutableStateFlow(FlowCall.State.NOTHING)

    fun startTimer() {
        timer.value = 60

        viewModelScope.launch {
            while (timer.value > 0) {
                timer.value -= 1
                delay(1000)
            }
        }
    }

    var uiState = MutableStateFlow(LoginState.PHONE)
    var appErrors: MutableStateFlow<Map<String, String>> = MutableStateFlow(mapOf())

    fun validateAndSubmitPhone(loginType: LoginType) {
        appErrors.value = mapOf()
        if (phone.value.length < 8) appErrors.value += mapOf("phone" to "Invalid phone number.")
        if (loginType == LoginType.MEMBER && username.value.length < 8) appErrors.value += mapOf("username" to "Invalid username.")
        if (!tos.value) appErrors.value += mapOf("tos" to "Please accept the terms of service before continuing")
        if (appErrors.value.isNotEmpty()) return else submitPhone()
    }

    fun submitPhone() = flow {
        emit(FlowCall.loading())
        delay(1000)
        emit(loginAPI.login(LoginDTO("+63${phone.value}", username.value)).flowCall)
    }.onEach {
        requestState.value = it.state
        if (it.isSuccess) uiState.value = LoginState.OTP
        if (it.isError) appErrors.value = mapOf("phone" to "Something went wrong. Please try again.")
    }.launchIn(viewModelScope)

    fun validateAndSubmitOTP() {
        appErrors.value = mapOf()
        if (otp.value.length < 6) appErrors.value += mapOf("otp" to "Invalid OTP.")
        if (appErrors.value.isNotEmpty()) return else submitOTP()
    }

    fun submitOTP() = flow<FlowCall<HttpResponse>> {
        emit(FlowCall.loading())
        delay(1000)
        loginAPI.verifyOTP(OTPLoginDTO("+63${phone.value}", otp.value)).let {

            if (it.status == HttpStatusCode.OK) {
                val token = it.headers["X-Access-Token"]

                if (token == null) emit(FlowCall.error(message = "The server did not return a token. Please try again."))
                else {
                    sharedPreferences.edit().putString("authToken", token).apply()

                    UserAPI(loginAPI.httpClient).apply {
                        setToken(token)
                    }.getUser().let { (data, errors) ->
                        if (errors.isEmpty()) {
                            hasUserDetails.value = data?.firstName != null
                            isLoggedIn.value = true
                            emit(FlowCall.success())
                        } else {
                            emit(FlowCall.error(message = "The server did not recognize the token. Please try again."))
                        }
                    }
                    emit(FlowCall.success(null))
                }
            } else emit(FlowCall.error(message = "Invalid OTP. Please try again."))
        }
    }.onEach {
        requestState.value = it.state
    }.launchIn(viewModelScope)
}

enum class LoginType {
    ADMIN,
    MEMBER
}

enum class LoginState {
    PHONE,
    OTP
}