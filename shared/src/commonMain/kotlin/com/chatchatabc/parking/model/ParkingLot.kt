package com.chatchatabc.parking.model

import kotlinx.serialization.Serializable

@Serializable
data class ParkingLot(
    val parkingLotUuid: String,
    val rate: Rate?,
    val name: String?,
    val address: String?,
    val latitude: Double?,
    val longitude: Double?,
    val description: String?,
    val capacity: Int?,
    val availableSlots: Int?,
    val businessHoursStart: String?,
    val businessHoursEnd: String?,
    val openDaysFlag: Int?,
    val status: Int,
    val verifiedAt: String?,
    val createdAt: String,
    val updatedAt: String?,
) {
    companion object {
        val DELETED = -1
        val DRAFT = 0
        val PENDING_VERIFICATION = 1
        val REJECTED = 2
        val VERIFIED = 3
    }
}

fun List<Int>.toIntFlag(): Int {
    var flag = 0
    for (index in this) {
        flag = flag or (1 shl index)
    }
    return flag
}

fun Int.getFlags(): List<Int> {
    val indexList = mutableListOf<Int>()
    for (i in 0..6) {
        if ((this and (1 shl i)) != 0) {
            indexList.add(i)
        }
    }
    return indexList
}

@Serializable
data class Rate(
    val id: String,
    val type: Int,
    val interval: Int,
    val freeHours: Int,
    val payForFreeHoursWhenExceeding: Boolean,
    val startingRate: Double,
    val rate: Double,
    val createdAt: String,
    val updatedAt: String
)
