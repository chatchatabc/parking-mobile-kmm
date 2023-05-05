package com.chatchatabc.parkingadmin.model.response

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val phone: String,
    val username: String?
)