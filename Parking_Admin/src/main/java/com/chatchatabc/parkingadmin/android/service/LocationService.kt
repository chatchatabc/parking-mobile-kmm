package com.chatchatabc.parkingadmin.android.service

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.chatchatabc.parkingadmin.android.core.activity.LocationActivity
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.tasks.await


@SuppressLint("MissingPermission") // LocationActivity handles this for us
class LocationService(locationActivity: LocationActivity) {
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
    fun stopListening() {
        locationClient.removeLocationUpdates(locationCallback)
    }

    fun startListening() {
        locationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    suspend fun getLastLocation(): LatLng {
        return locationClient.getCurrentLocation(CurrentLocationRequest.Builder().apply {
            this.setDurationMillis(2000)
            this.setGranularity(Granularity.GRANULARITY_FINE)
        }.build(), null).await().let {
            LatLng(it.latitude, it.longitude)
        }
    }
}