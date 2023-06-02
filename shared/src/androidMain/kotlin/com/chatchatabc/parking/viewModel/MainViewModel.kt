package com.chatchatabc.parking.viewModel

import android.content.SharedPreferences
import androidx.lifecycle.viewModelScope
import com.chatchatabc.parking.api.ParkingAPI
import com.chatchatabc.parking.api.ProfileAPI
import com.chatchatabc.parking.api.UserAPI
import com.chatchatabc.parking.model.ParkingLot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    val userAPI: UserAPI,
    val parkingAPI: ParkingAPI,
    val profileAPI: ProfileAPI,
    val sharedPreferences: SharedPreferences
) : BaseViewModel() {
    init {
        setToken(parkingAPI, profileAPI)
    }

    val logoutPopupOpened = MutableStateFlow(false)
    val parkingLot: MutableStateFlow<ParkingLot?> = MutableStateFlow(null)

    fun getParkingLot() {
        isLoading.value = true
        println("Checking parking lots")
        viewModelScope.launch {
            parkingAPI.getParkingLot().let {
                if (it.errors.isNullOrEmpty()) {
                    parkingLot.value = it.data
                } else {
                    it.errors?.forEach { error ->
                        println("Error: ${error.message}")
                    }
                }
            }
        }
        isLoading.value = false
    }

    fun clearAuthToken() {
        viewModelScope.launch {
            // Change activity to loginActivity
            parkingLot.value = null
            sharedPreferences.edit().remove("authToken").apply()
            profileAPI.logout()
        }
    }
}