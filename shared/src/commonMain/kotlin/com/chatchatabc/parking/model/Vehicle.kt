package com.chatchatabc.parking.model

import kotlinx.serialization.Serializable

@Serializable
data class Vehicle(
    val vehicleUuid: String,
    val name: String,
    val plateNumber: String,
    val type: Int,
    val createdAt: String,
    val updatedAt: String
)
