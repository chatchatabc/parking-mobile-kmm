package com.chatchatabc.parking.api

data class NewVehicleDTO(
    val name: String,
    val plateNumber: String,
    val type: Int
)