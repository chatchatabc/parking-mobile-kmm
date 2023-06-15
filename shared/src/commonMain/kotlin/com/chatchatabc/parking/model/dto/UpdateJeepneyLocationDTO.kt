package com.chatchatabc.parking.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateJeepneyLocationDTO(
    val latitude: Double,
    val longitude: Double
)