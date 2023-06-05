package com.chatchatabc.parking.model

import kotlinx.serialization.Serializable

@Serializable
data class Invoice(
    var id: String,
    var estimatedParkingDurationInHours: Int,
    var total: Double?,
    var paidAt: String?,
    var startAt: String,
    var endAt: String?,
    var createdAt: String,
    var updatedAt: String
)
