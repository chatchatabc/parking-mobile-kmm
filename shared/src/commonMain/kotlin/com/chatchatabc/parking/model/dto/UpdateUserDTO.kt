package com.chatchatabc.parking.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserDTO(
    val username: String?,
    val email: String?,
    val firstName: String,
    val lastName: String
)