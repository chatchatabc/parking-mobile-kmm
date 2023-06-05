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

enum class VehicleType {
    CAR,
    MOTORCYCLE,
    NONE
}

inline fun <reified T : Enum<T>> Int.toEnum(): T {
    return enumValues<T>()[this]
}