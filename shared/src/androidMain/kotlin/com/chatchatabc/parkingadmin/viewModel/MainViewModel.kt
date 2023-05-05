package com.chatchatabc.parkingadmin.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatchatabc.parkingadmin.api.ParkingAPI
import com.chatchatabc.parkingadmin.api.UserAPI
import com.chatchatabc.parkingadmin.model.ParkingLot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainViewModel(val userAPI: UserAPI, val parkingAPI: ParkingAPI): ViewModel() {
    val parkingLots: MutableStateFlow<List<ParkingLot>> = MutableStateFlow(listOf())

    val isLoading = MutableStateFlow(true)

    fun getParkingLots() {
        isLoading.value = true
        println("Checking parking lots")
        viewModelScope.launch {
            parkingAPI.getParkingLots().let {
                if (!it.error) {
                    parkingLots.value = it.data?.content ?: listOf()
                }
                println("ERROR? ${it.error}")
                println("ERROR? ${it.message}")
            }
        }
        isLoading.value = false
    }
}