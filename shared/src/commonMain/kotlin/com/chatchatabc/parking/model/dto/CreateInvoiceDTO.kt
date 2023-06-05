package com.chatchatabc.parking.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateInvoiceDTO(
    val estimatedParkingDurationInHours: Int
)