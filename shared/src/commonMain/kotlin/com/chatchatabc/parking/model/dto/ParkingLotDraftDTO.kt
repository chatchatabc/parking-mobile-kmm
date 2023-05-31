package com.chatchatabc.parking.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class ParkingLotDraftDTO(
    val name: String?,
    val latitude: Double?,
    val longitude: Double?,
    val address: String?,
    val description: String?,
    val capacity: Int?,
    val businessHoursStart: String?,
    val businessHoursEnd: String?,
    val openDaysFlag: Int?
)
