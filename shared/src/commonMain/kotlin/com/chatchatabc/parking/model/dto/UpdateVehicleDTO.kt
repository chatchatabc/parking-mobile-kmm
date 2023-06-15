package com.chatchatabc.parking.model.dto

import kotlinx.serialization.Serializable

@Serializable
class UpdateVehicleDTO(
    val name: String,
    val plateNumber: String,
    val type: Int
)