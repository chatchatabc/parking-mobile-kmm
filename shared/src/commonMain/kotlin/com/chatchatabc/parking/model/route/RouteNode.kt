package com.chatchatabc.parking.model.route

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class RouteNode(
    val id: Long,
    val latitude: Double,
    val longitude: Double,
    val poi: String?,
    val createdAt: LocalDateTime
)