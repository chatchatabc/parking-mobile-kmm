package com.chatchatabc.parkingclient.android.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.chatchatabc.parking.model.ParkingLot
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class ParkingLotDBObject(
    @PrimaryKey
    var parkingLotUuid: String,
    var name: String?,
    var address: String?,
    var latitude: Double?,
    var longitude: Double?,
    var description: String?,
    var capacity: Int?,
    var availableSlots: Int?,
    var businessHoursStart: String?,
    var businessHoursEnd: String?,
    var openDaysFlag: Int?,
    var status: Int,
    var verifiedAt: String?,
    var createdAt: String,
    var updatedAt: String?
)

fun ParkingLotDBObject.toParkingLot() = ParkingLot(
    parkingLotUuid = parkingLotUuid,
    name = name,
    address = address,
    latitude = latitude,
    longitude = longitude,
    description = description,
    capacity = capacity,
    availableSlots = availableSlots,
    businessHoursStart = businessHoursStart,
    businessHoursEnd = businessHoursEnd,
    openDaysFlag = openDaysFlag,
    status = status,
    verifiedAt = verifiedAt,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun ParkingLot.toDBObject() = ParkingLotDBObject(
    parkingLotUuid = parkingLotUuid,
    name = name,
    address = address,
    latitude = latitude,
    longitude = longitude,
    description = description,
    capacity = capacity,
    availableSlots = availableSlots,
    businessHoursStart = businessHoursStart,
    businessHoursEnd = businessHoursEnd,
    openDaysFlag = openDaysFlag,
    status = status,
    verifiedAt = verifiedAt,
    createdAt = createdAt,
    updatedAt = updatedAt
)