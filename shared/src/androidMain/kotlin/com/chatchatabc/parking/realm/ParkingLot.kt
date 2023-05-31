package com.chatchatabc.parking.realm

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class ParkingLotRealmObject: RealmObject {
    @PrimaryKey
    var id: String = ""
    var name: String = ""
    var latitude: Double? = null
    var longitude: Double? = null
}