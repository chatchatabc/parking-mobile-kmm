package com.chatchatabc.parking.viewModel

import android.content.SharedPreferences
import androidx.lifecycle.viewModelScope
import com.chatchatabc.parking.api.ParkingAPI
import com.chatchatabc.parking.api.ProfileAPI
import com.chatchatabc.parking.model.Vehicle
import com.chatchatabc.parking.realm.ParkingLotRealmObject
import com.google.android.gms.maps.model.LatLngBounds
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ClientMainViewModel(
    val parkingAPI: ParkingAPI,
    val profileAPI: ProfileAPI,
    val parkingRealm: Realm,
    val sharedPreferences: SharedPreferences
): BaseViewModel() {
    val logoutPopupOpened = MutableStateFlow(false)
    val parkingLots = MutableStateFlow(listOf<ParkingLotRealmObject>())
    val visibleParkingLots = MutableStateFlow(listOf<ParkingLotRealmObject>())

    val isSelectingVehicle = MutableStateFlow(false)
    val selectedVehicle: MutableStateFlow<Vehicle?> = MutableStateFlow(null)

    fun syncParkingLots() {
        load {
            parkingAPI.getAllParkingLots().let { response ->
                parkingRealm.writeBlocking {
                    if (response.errors.isNullOrEmpty()) response.data?.forEach { parkingLot ->
                        copyToRealm(
                            instance = parkingLot.let {
                                ParkingLotRealmObject().apply {
                                    id = it.parkingLotUuid
                                    name = it.name!!
                                    latitude = it.latitude
                                    longitude = it.longitude
                                }},
                            updatePolicy = UpdatePolicy.ALL
                        )
                    }
                }
                parkingLots.value = parkingRealm.query<ParkingLotRealmObject>().find()
            }
        }
    }

    fun getParkingLotsInRange(bounds: LatLngBounds) {
        println("Getting parking lots in range")
        load {
            parkingRealm.query<ParkingLotRealmObject>(
                "latitude >= ${bounds.southwest.latitude} AND latitude <= ${bounds.northeast.latitude} AND " +
                        "longitude >= ${bounds.southwest.longitude} AND longitude <= ${bounds.northeast.longitude}"
            ).find().let {
                visibleParkingLots.value = it
                it.forEach {
                    println("Parking lot: ${it.name} (${it.latitude}, ${it.longitude})")
                }
            }
        }
    }

    fun openVehicleSelector() {
        isSelectingVehicle.value = true
        selectedVehicle.value = null
    }

    fun clearAuthToken() {
        viewModelScope.launch {
            // Change activity to loginActivity
            sharedPreferences.edit().remove("authToken").apply()
            profileAPI.logout()
        }
    }
}