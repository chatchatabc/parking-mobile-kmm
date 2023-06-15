package com.chatchatabc.parking.model

import kotlinx.serialization.Serializable

@Serializable
data class Jeepney(
    val jeepneyUuid: String,
    val name: String?,
    val plateNumber: String,
    val routeUuid: String?,
    val capacity: Int,
    val availableSlots: Int,
    val latitude: Double,
    val longitude: Double,
    val status: Int,
    val createdAt: String,
    val updatedAt: String?,
) {
    companion object {
        val INACTIVE = -1
        val DRAFT = 0
        val ACTIVE = 1
    }
}