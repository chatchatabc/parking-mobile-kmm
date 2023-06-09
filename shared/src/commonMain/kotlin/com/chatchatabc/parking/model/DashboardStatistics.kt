package com.chatchatabc.parking.model

import kotlinx.serialization.Serializable

@Serializable
data class DashboardStatistics(
    val availableParkingCapacity: Int,
    val leavingSoon: Long,
    val occupiedParkingCapacity: Long,
    val traffic: Long,
    val profit: Double,
)