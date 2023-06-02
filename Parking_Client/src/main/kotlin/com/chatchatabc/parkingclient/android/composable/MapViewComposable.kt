package com.chatchatabc.parkingclient.android.composable

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.chatchatabc.parking.realm.ParkingLotRealmObject
import com.chatchatabc.parkingclient.android.toLatLng
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MapViewComposable(
    modifier: Modifier = Modifier,
    pins: List<ParkingLotRealmObject>,
    onMapLoaded: () -> Unit,
    onMapMoved: (LatLngBounds) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val camera = rememberCameraPositionState()

    LaunchedEffect(camera.isMoving) {
        if (!camera.isMoving) {
            camera.projection?.let {
                onMapMoved(it.visibleRegion.latLngBounds)
            }
        }
    }

    Box(modifier) {
        GoogleMap(
            cameraPositionState = camera,
            properties = MapProperties(
                isBuildingEnabled = true,
                isIndoorEnabled = false,
                isMyLocationEnabled = true,
                isTrafficEnabled = false,
                mapType = MapType.NORMAL
            ),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = false,
                zoomControlsEnabled = false
            ),
            onMyLocationClick = { location ->
                coroutineScope.launch {
                    camera.animate(
                        CameraUpdateFactory.newCameraPosition(
                            CameraPosition.builder(camera.position)
                                .target(location.toLatLng())
                                .zoom(if (camera.position.zoom < 15) 15f else camera.position.zoom)
                                .build()
                        ), 1500
                    )
                }
            },
            onMapLoaded = {
                onMapLoaded()
                camera.projection?.let {
                    onMapMoved(it.visibleRegion.latLngBounds)
                }
            }
        ) {
            MarkerContainerComposable(pins = pins)
        }
    }
}