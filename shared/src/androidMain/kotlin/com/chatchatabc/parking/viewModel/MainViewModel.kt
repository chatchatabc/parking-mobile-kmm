package com.chatchatabc.parking.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatchatabc.parking.api.ParkingAPI
import com.chatchatabc.parking.api.UserAPI
import com.chatchatabc.parking.model.ParkingLot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainViewModel(val userAPI: UserAPI, val parkingAPI: ParkingAPI): ViewModel() {
    val parkingLot: MutableStateFlow<ParkingLot?> = MutableStateFlow(null)

    val isLoading = MutableStateFlow(true)

    fun getParkingLot() {
        isLoading.value = true
        println("Checking parking lots")
        viewModelScope.launch {
            parkingAPI.getParkingLot().let {
                if (!it.error) {
                    parkingLot.value = it.data
                } else {
                    println("Error: ${it.message}")
                }
            }
        }
        isLoading.value = false
    }
}