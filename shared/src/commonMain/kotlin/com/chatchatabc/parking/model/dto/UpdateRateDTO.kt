package com.chatchatabc.parking.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateRateDTO(
    val type: Int,
    val interval: Int,
    val freeHours: Int,
    val payForFreeHoursWhenExceeding: Boolean,
    val startingRate: Double,
    val rate: Double,
)