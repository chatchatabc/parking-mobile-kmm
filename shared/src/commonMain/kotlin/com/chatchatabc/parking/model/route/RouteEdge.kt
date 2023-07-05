package com.chatchatabc.parking.model.route

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class RouteEdge(
    val id: Long,
    val routeId: Long,
    val nodeFrom: Long,
    val nodeTo: Long,
    val distance: Double,
    val createdAt: LocalDateTime
)