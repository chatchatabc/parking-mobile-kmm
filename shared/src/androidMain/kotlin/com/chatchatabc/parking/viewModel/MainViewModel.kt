package com.chatchatabc.parking.viewModel

import android.content.SharedPreferences
import androidx.lifecycle.viewModelScope
import com.chatchatabc.parking.api.DashboardAPI
import com.chatchatabc.parking.api.ParkingAPI
import com.chatchatabc.parking.api.ProfileAPI
import com.chatchatabc.parking.api.UserAPI
import com.chatchatabc.parking.model.DashboardStatistics
import com.chatchatabc.parking.model.ParkingLot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class MainViewModel(
    val userAPI: UserAPI,
    val parkingAPI: ParkingAPI,
    val profileAPI: ProfileAPI,
    val dashbaordAPI: DashboardAPI,
    val sharedPreferences: SharedPreferences
) : BaseViewModel(parkingAPI, profileAPI, dashbaordAPI) {

    val vehicleSearchOpened: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val logoutPopupOpened = MutableStateFlow(false)
    val parkingLot: MutableStateFlow<ParkingLot?> = MutableStateFlow(null)

    val dashboardStats = MutableStateFlow<DashboardStatistics?>(null)

    val scheduledThreadPoolExecutor = ScheduledThreadPoolExecutor(1)

    fun startParkingLotUpdate() {
        scheduledThreadPoolExecutor.scheduleAtFixedRate(
            {
                Timber.d("Typing to update dashboard...")
                updateDashboard()
            },
            2,
            2,
            TimeUnit.SECONDS
        )
    }

    fun getParkingLot() {
        load {
            Timber.d("Checking parking lots")
            viewModelScope.launch {
                parkingAPI.getParkingLot().let {
                    if (it.errors.isEmpty()) {
                        parkingLot.value = it.data
                        if (it.data?.status == ParkingLot.VERIFIED) startParkingLotUpdate()
                    } else {
                        it.errors?.forEach { error ->
                            println("Error: ${error.message}")
                        }
                    }
                }
            }
        }
    }

    fun clearAuthToken() {
        viewModelScope.launch {
            // Change activity to loginActivity
            parkingLot.value = null
            sharedPreferences.edit().remove("authToken").apply()
            profileAPI.logout()
        }
    }

    fun updateDashboard() {
        viewModelScope.launch {
            dashbaordAPI.getDashboardStatistics().let {
                if (it.errors.isEmpty()) {
                    dashboardStats.value = it.data
                }
            }
        }
    }
}