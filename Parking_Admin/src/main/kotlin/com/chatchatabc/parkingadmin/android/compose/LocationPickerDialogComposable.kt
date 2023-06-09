package com.chatchatabc.parkingadmin.android.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.chatchatabc.parkingadmin.android.service.LocationService
import com.google.android.gms.maps.model.LatLng

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationPickerDialogComposable(
    locationService: LocationService,
    initialLocation: LatLng?,
    onDismissRequest: () -> Unit,
    onLocationSelected: (LatLng) -> Unit,
) {
    val realtimeLocation by locationService.currentLocation.observeAsState()

    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = true
        )
    ) {
        (LocalView.current.parent as DialogWindowProvider).window.setDimAmount(0.25f)

        Box(
            Modifier.fillMaxSize()
        ) {
            GoogleMapsLocationPickerComposable(
                realtimeLocation = realtimeLocation,
                initialLocation = initialLocation,
                onLocationSet = { onLocationSelected(it) },
                onCancel = { onDismissRequest() }
            )
        }
    }
}