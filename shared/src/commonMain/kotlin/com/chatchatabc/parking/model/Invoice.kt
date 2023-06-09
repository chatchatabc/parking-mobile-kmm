package com.chatchatabc.parking.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Invoice(
    var invoiceUuid: String,
    var estimatedParkingDurationInHours: Int,
    var total: Double?,
    var paidAt: LocalDateTime?,
    var startAt: LocalDateTime,
    var endAt: LocalDateTime?,
    var createdAt: LocalDateTime,
    var updatedAt: LocalDateTime
)
