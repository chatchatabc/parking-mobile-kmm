package com.chatchatabc.parkingadmin.android.compose

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
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
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun PositionCardComposable(location: LatLng?, isError: Boolean, onCardPressed: () -> Unit) {
    Log.d("PositionCard", "Location: $location")
    val context = LocalContext.current

    Card(
        shape = MaterialTheme.shapes.extraSmall,
        modifier = Modifier
            .border(
                1.dp,
                color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.extraSmall
            )
    ) {
        val camera = rememberCameraPositionState()
        LaunchedEffect(location) {
            location?.let {
                Log.d("PositionCard", "Moving camera to $it")
                camera.move(CameraUpdateFactory.newCameraPosition(CameraPosition(it, 13f, 0f, 0f)))
            }
        }

        Box {
            GoogleMap(
                cameraPositionState = camera,
                uiSettings = MapUiSettings(
                    compassEnabled = false,
                    myLocationButtonEnabled = false,
                    mapToolbarEnabled = false,
                    scrollGesturesEnabled = false,
                    tiltGesturesEnabled = false,
                    zoomControlsEnabled = false,
                    zoomGesturesEnabled = false,
                ),
                properties = MapProperties(
                    mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.neutral),
                ),
                modifier = Modifier
                    .height(100.dp)
                    .blur(if (location == null) 10.dp else 0.dp)
            )
            IconButton(
                onClick = { onCardPressed() }, modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp), colors = IconButtonDefaults.filledIconButtonColors()
            ) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit Location", Modifier.size(16.dp))
            }
            if (location != null) {
                Image(
                    painterResource(
                        id = R.drawable.parking_icon
                    ),
                    contentDescription = "Pin",
                    modifier = Modifier
                        .wrapContentSize(Alignment.Center)
                        .align(Alignment.Center)
                )
            }
        }
    }
}