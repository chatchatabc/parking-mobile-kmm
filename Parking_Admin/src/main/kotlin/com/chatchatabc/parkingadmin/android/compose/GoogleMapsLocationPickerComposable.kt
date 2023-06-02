package com.chatchatabc.parkingadmin.android.compose

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.chatchatabc.parkingadmin.android.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun GoogleMapsLocationPickerComposable(
    initialLocation: LatLng?,
    onLocationSet: (location: LatLng) -> Unit = { },
) {
    val camera = rememberCameraPositionState()
    var hasLocationLock by remember { mutableStateOf(false) }

    LaunchedEffect(hasLocationLock) {
        if (!hasLocationLock) {
            hasLocationLock = true
            camera.move(
                CameraUpdateFactory.newLatLng(
                    initialLocation ?: LatLng(
                        7.110192,
                        125.635160
                    )
                )
            )
        }
    }

    val moveCamera = { location: LatLng ->
        CoroutineScope(Dispatchers.Main).launch {
            camera.animate(
                update = CameraUpdateFactory.newCameraPosition(
                    CameraPosition(
                        location,
                        if (camera.position.zoom.toInt() != 15) 15f else camera.position.zoom,
                        camera.position.tilt,
                        camera.position.bearing
                    )
                ),
                durationMs = 500
            )
        }
    }

    Box(
        modifier = Modifier.clip(RoundedCornerShape(32.dp))
    ) {
        val context = LocalContext.current
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = camera,
            properties = MapProperties(
                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.neutral),
            ),
            onMyLocationClick = { moveCamera(LatLng(it.latitude, it.longitude)) },
            onPOIClick = { moveCamera(it.latLng) }
        )
        Image(
            painter = painterResource(id = R.drawable.parking_icon),
            contentDescription = "Parking Lot",
            modifier = Modifier.align(Alignment.Center)
        )
        Button(onClick = {
            Log.d("GoogleMapsLocationPicker", "Location set: ${camera.position.target}")
            onLocationSet(camera.position.target)
        }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Select this location")
        }
    }
}