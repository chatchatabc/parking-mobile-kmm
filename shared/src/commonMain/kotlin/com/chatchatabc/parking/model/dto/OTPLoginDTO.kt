package com.chatchatabc.parking.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class OTPLoginDTO(
    val phone: String,
    val otp: String
)