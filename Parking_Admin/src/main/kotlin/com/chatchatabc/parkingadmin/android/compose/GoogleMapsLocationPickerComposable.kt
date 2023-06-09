package com.chatchatabc.parkingadmin.android.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.chatchatabc.parkingadmin.android.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraMoveStartedReason
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun GoogleMapsLocationPickerComposable(
    initialLocation: LatLng?,
    realtimeLocation: LatLng?,
    onLocationSet: (location: LatLng) -> Unit,
    onCancel: () -> Unit
) {
    val camera = rememberCameraPositionState()
    var hasLocationLock by remember { mutableStateOf(false) }

    val moveCamera = { location: LatLng ->
        CoroutineScope(Dispatchers.Main).launch {
            camera.animate(
                update = CameraUpdateFactory.newCameraPosition(
                    CameraPosition(
                        location,
                        if (camera.position.zoom.toInt() < 17) 17f else camera.position.zoom,
                        camera.position.tilt,
                        camera.position.bearing
                    )
                ),
                durationMs = 500
            )
        }
    }

    LaunchedEffect(camera.isMoving) {
        if (camera.isMoving && hasLocationLock) {
            if (camera.cameraMoveStartedReason != CameraMoveStartedReason.DEVELOPER_ANIMATION) {
                hasLocationLock = false
            }
        }
    }

    LaunchedEffect(Unit) {
        moveCamera(initialLocation ?: realtimeLocation ?: LatLng(7.110192, 125.635160))
    }

    LaunchedEffect(realtimeLocation) {
        if (hasLocationLock) realtimeLocation?.let {
            camera.animate(CameraUpdateFactory.newLatLng(it))
        }

    }

    Box(Modifier.fillMaxSize()) {
        val context = LocalContext.current
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = camera,
            properties = MapProperties(
                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.neutral),
            ),
            onMyLocationClick = { moveCamera(LatLng(it.latitude, it.longitude)) },
            onPOIClick = { moveCamera(it.latLng) },
        )
        Image(
            painter = painterResource(id = R.drawable.parking_icon),
            contentDescription = "Parking Lot Pin",
            modifier = Modifier.align(Alignment.Center)
        )

        FilledTonalIconButton(onClick = { onCancel() }, modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(32.dp)) {
            Icon(Icons.Filled.Close, contentDescription = "Close")
        }

        AnimatedVisibility(visible = !camera.isMoving, enter = fadeIn(), exit = fadeOut(), modifier = Modifier.align(Alignment.BottomCenter)) {
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(32.dp)) {
                FloatingActionButton(
                    onClick = {
                        realtimeLocation?.let {
                            hasLocationLock = true
                            moveCamera(it)
                        }
                    },
                    containerColor = if (!hasLocationLock) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.tertiary,
                    contentColor = if (!hasLocationLock) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onTertiary,
                ) {
                    Icon(Icons.Filled.MyLocation, contentDescription = "My Location")
                }
                Button(onClick = {
                    Timber.d("Location set: ${camera.position.target}")
                    onLocationSet(camera.position.target)
                }, modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Select this location")
                }
            }
        }
    }
}
