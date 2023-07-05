package com.chatchatabc.parkingclient.android.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.chatchatabc.parking.model.Rate
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class RateDBObject(
    @PrimaryKey
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


val RateDBObject.fromObject: Rate
    get() = Rate(
        id = id,
        type = type,
        interval = interval,
        freeHours = freeHours,
        payForFreeHoursWhenExceeding = payForFreeHoursWhenExceeding,
        startingRate = startingRate,
        rate = rate,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

val Rate.dbObject: RateDBObject
    get() = RateDBObject(
        id = id,
        type = type,
        interval = interval,
        freeHours = freeHours,
        payForFreeHoursWhenExceeding = payForFreeHoursWhenExceeding,
        startingRate = startingRate,
        rate = rate,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
