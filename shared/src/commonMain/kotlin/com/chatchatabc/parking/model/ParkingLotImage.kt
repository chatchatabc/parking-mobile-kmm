package com.chatchatabc.parking.model

import kotlinx.serialization.Serializable

@Serializable
data class ParkingLotImage(
    val id: String,
    val fileOrder: Int
)

