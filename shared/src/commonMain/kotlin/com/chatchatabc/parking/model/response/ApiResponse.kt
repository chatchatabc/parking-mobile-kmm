package com.chatchatabc.parking.model.response

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val data: T? = null,
    val errors: List<ErrorElement> = emptyList()
)

@Serializable
data class ErrorElement(
    val title: String?,
    val message: String?
)