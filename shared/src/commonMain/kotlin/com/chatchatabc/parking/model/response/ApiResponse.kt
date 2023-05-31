package com.chatchatabc.parking.model.response

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    var error: Boolean = false,
    var message: String,
    var data: T? = null,
    var code: Int = 0
)