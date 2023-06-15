package com.chatchatabc.parking.viewModel

import android.content.SharedPreferences
import androidx.lifecycle.viewModelScope
import com.chatchatabc.parking.api.DashboardAPI
import com.chatchatabc.parking.api.ParkingAPI
import com.chatchatabc.parking.api.ProfileAPI
import com.chatchatabc.parking.api.UserAPI
import com.chatchatabc.parking.model.DashboardStatistics
import com.chatchatabc.parking.model.ParkingLot
import com.chatchatabc.parking.model.response.ApiResponse
import com.chatchatabc.parking.model.response.FlowCall
import com.chatchatabc.parking.model.response.flowCall
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
    val parkingLot: MutableStateFlow<FlowCall<ApiResponse<ParkingLot>>> = MutableStateFlow(FlowCall.nothing())

    val dashboardStats = MutableStateFlow<DashboardStatistics?>(null)
    val isDashboardRefreshing = MutableStateFlow(false)

    val scheduledThreadPoolExecutor = ScheduledThreadPoolExecutor(1)

    fun startParkingLotUpdate() {
        scheduledThreadPoolExecutor.scheduleAtFixedRate(
            {
                Timber.d("Trying to update dashboard...")
                updateDashboard()
            },
            0,
            2, // TODO: Change to larger number in the future. For testing purposes only.
            TimeUnit.SECONDS
        )
    }

    fun getParkingLot() = flow {
        emit(FlowCall.loading())
        emit(parkingAPI.getParkingLot().flowCall)
    }.onEach {
        parkingLot.value = it
        Timber.d("Parking lot: $it")
        if (it.response?.data?.status == ParkingLot.VERIFIED) startParkingLotUpdate()
    }.launchIn(viewModelScope)

    fun clearAuthToken() = load {
        // Change activity to loginActivity
        parkingLot.value = FlowCall.nothing()
        sharedPreferences.edit().remove("authToken").apply()
        profileAPI.logout()
    }

    fun updateDashboard() = flow {
        isDashboardRefreshing.value = true
        delay(1000)
        emit(dashbaordAPI.getDashboardStatistics().flowCall)
        isDashboardRefreshing.value = false
    }.onEach {
        dashboardStats.value = it.response?.data
    }.launchIn(viewModelScope)
}