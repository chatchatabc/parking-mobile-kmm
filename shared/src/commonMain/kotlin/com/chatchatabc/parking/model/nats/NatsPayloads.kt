package com.chatchatabc.parking.model.nats

import kotlinx.serialization.Serializable

@Serializable
data class InvoicePayload(
    val parkingLotUuid: String,
    val vehicleUuid: String,
    val invoiceUuid: String
)