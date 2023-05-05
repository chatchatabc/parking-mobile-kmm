package com.chatchatabc.parkingadmin.viewModel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatchatabc.parkingadmin.api.LoginAPI
import com.chatchatabc.parkingadmin.model.User
import com.chatchatabc.parkingadmin.model.dto.LoginDTO
import com.chatchatabc.parkingadmin.model.dto.OTPLoginDTO
import com.chatchatabc.parkingadmin.model.response.ApiResponse
import io.ktor.client.call.body
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

// TODO: Better error handling

class LoginViewModel(
    val api: LoginAPI,
    val sharedPreferences: SharedPreferences
): ViewModel() {
    val phone = MutableStateFlow("")
    // TODO: Link tos to checkbox
    val tos = MutableStateFlow(false)
    val otp = MutableStateFlow("")

    val isLoading = MutableStateFlow(false)
    val isLoggedIn = MutableStateFlow(false)

    var hasUserDetails = false

    var uiState = MutableStateFlow(LoginState.PHONE)
    var errors: MutableStateFlow<Map<String, String>> = MutableStateFlow(mapOf())

    fun validateAndSubmitPhone() {
        errors.value = mapOf()
        if (phone.value.length < 10) errors.value = mapOf("phone" to "Phone number must be 10 digits.")
        if (errors.value.isNotEmpty()) return

        viewModelScope.launch {
            isLoading.value = true
            try {
                with (api.login(LoginDTO(phone.value))) {
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

    fun validateAndSumbitOTP() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                with(api.verifyOPT(OTPLoginDTO(phone.value, otp.value))) {
                    if (status.isSuccess()) {
                        body<ApiResponse<User>>().let { reponse ->
                            if (!reponse.error) {
                                headers["X-Access-Token"]?.let {
                                    sharedPreferences.edit().putString("authToken", it).apply()
                                    Log.d("TOKEN", it)
                                }
                                isLoggedIn.value = true
                                hasUserDetails = body<ApiResponse<User>>().data!!.firstName != null
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