package com.chatchatabc.parking.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginDTO(
    val phone: String,
    val username: String? = null
)