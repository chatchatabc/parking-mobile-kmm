package com.chatchatabc.parkingclient.android.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.chatchatabc.parkingclient.android.db.entity.ParkingLotDBObject
import com.mapbox.maps.CoordinateBounds

@Dao
interface ParkingLotDAO {
    @Transaction
    @Query("SELECT * FROM parkinglotdbobject")
    fun getAllParkingLots(): List<ParkingLotDBObject>

    @Query("SELECT * FROM parkinglotdbobject WHERE parkingLotUuid = :parkingLotUuid")
    fun getParkingLotById(parkingLotUuid: String): ParkingLotDBObject

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertParkingLot(parkingLot: ParkingLotDBObject)

    @Transaction
    fun addAll(parkingLots: List<ParkingLotDBObject>) {
        parkingLots.forEach { insertParkingLot(it) }
    }

    @Delete
    fun deleteParkingLot(parkingLot: ParkingLotDBObject)

    @Query(""" 
    SELECT * FROM parkinglotdbobject WHERE latitude >= :swLat AND latitude <= :neLat 
    AND 
    (
        (:neLong < :swLong AND (longitude >= :swLong OR longitude <= :neLong))
        OR
        (longitude >= :swLong AND longitude <= :neLong)
    )""")
    fun getParkingLotsWithinBounds(swLat: Double, neLat: Double, swLong: Double, neLong: Double): List<ParkingLotDBObject>

    fun getParkingLotsWithinCoordinateBounds(boundary: CoordinateBounds) =
        getParkingLotsWithinBounds(
            boundary.southwest.latitude(),
            boundary.northeast.latitude(),
            boundary.southwest.longitude(),
            boundary.northeast.longitude()
        )
}