package com.chatchatabc.parkingclient.android.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.chatchatabc.parkingclient.android.db.entity.Converters
import com.chatchatabc.parkingclient.android.db.entity.LocalDateTimeTypeConverter
import com.chatchatabc.parkingclient.android.db.entity.ParkingLotDBObject
import com.chatchatabc.parkingclient.android.db.entity.RateDBObject
import com.chatchatabc.parkingclient.android.db.entity.RouteDBObject
import com.chatchatabc.parkingclient.android.db.entity.RouteNodesAndEdgesDBObject

@Database(
    entities = [
        ParkingLotDBObject::class,
        RateDBObject::class,
        RouteDBObject::class,
        RouteNodesAndEdgesDBObject::class],
    version = 2,
)
@TypeConverters(Converters::class, LocalDateTimeTypeConverter::class)
abstract class AppDB : RoomDatabase() {
    abstract fun parkingLotDAO(): ParkingLotDAO
    abstract fun rateDAO(): RateDAO
    abstract fun routeDAO(): RouteDAO
}