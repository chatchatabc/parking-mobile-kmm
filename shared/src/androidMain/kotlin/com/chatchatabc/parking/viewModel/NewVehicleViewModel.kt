package com.chatchatabc.parking.viewModel

import kotlinx.coroutines.flow.MutableStateFlow

enum class VehicleType {
    CAR,
    MOTORCYCLE,
    NONE
}

class NewVehicleViewModel {
    var page = MutableStateFlow(0)
    var name = MutableStateFlow("")
    val platenumber = MutableStateFlow("")
    val type = MutableStateFlow(VehicleType.NONE)

    val errors = MutableStateFlow(mapOf<String, String>())

    fun validateAndSubmitVehicle() {

    }
}