package com.chatchatabc.parkingadmin.android.service

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.chatchatabc.parking.activity.LocationActivity
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.LocationSource
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.tasks.await


@SuppressLint("MissingPermission") // LocationActivity handles this for us
class LocationService(locationActivity: LocationActivity): LocationSource {
    val currentLocation: MutableLiveData<LatLng?> = MutableLiveData(null)

    private val locationClient = LocationServices.getFusedLocationProviderClient(locationActivity)
    private val locationRequest = LocationRequest.Builder(LocationRequest.PRIORITY_HIGH_ACCURACY, 1000).build()
    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
            locationResult.lastLocation?.let {
                Log.d("LOCATION", "onLocationResult: $it")
                currentLocation.value = LatLng(it.latitude, it.longitude)
            }
        }
    }

    private val callbacks: MutableMap<String, LocationCallback> = mutableMapOf()

    fun stopListening(key: String = "default") {
        callbacks[key]?.let {
            locationClient.removeLocationUpdates(it)
        }
    }

    fun startListening() {
        callbacks["default"] = locationCallback
        locationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    fun startListening(callback: LocationSource.OnLocationChangedListener) {
        val _callback = object : LocationCallback() {
            override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                locationResult.lastLocation?.let {
                    Log.d("LOCATION", "onLocationResult: $it")
                    callback.onLocationChanged(it)
                }
            }
        }
        callbacks["locationSource"] = _callback
        locationClient.requestLocationUpdates(locationRequest, _callback, null)
    }

    suspend fun getLastLocation(): LatLng {
        return locationClient.getCurrentLocation(CurrentLocationRequest.Builder().apply {
            this.setDurationMillis(2000)
            this.setGranularity(Granularity.GRANULARITY_FINE)
        }.build(), null).await().let {
            LatLng(it.latitude, it.longitude)
        }
    }

    override fun activate(p0: LocationSource.OnLocationChangedListener) {
        startListening(p0)
    }

    override fun deactivate() {
        stopListening("locationSource")
    }
}