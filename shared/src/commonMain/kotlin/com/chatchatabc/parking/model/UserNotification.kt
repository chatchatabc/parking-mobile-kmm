package com.chatchatabc.parking.model

import kotlinx.serialization.Serializable

@Serializable
data class UserNotification(
    val notificationId: String,
)