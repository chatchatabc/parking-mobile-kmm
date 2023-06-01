package com.chatchatabc.parking.api

import kotlinx.serialization.Serializable

@Serializable
data class NewVehicleDTO(
    val name: String,
    val plateNumber: String,
    val type: Int
)