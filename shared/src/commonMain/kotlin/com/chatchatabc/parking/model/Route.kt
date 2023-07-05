package com.chatchatabc.parking.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Route(
    val routeUuid: String,
    val name: String,
    val description: String,
    val status: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val slug: String,
) {
    companion object {
        val INACTIVE = -1
        val DRAFT = 0
        val ACTIVE = 1
    }
}