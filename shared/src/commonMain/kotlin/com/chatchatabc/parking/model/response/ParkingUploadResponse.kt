package com.chatchatabc.parking.model.response

import kotlinx.serialization.Serializable

@Serializable
data class ParkingUploadResponse(
    val id: String,
    val fileOrder: Int
)