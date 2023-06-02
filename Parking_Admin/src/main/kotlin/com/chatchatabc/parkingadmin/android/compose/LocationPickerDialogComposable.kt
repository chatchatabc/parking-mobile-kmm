package com.chatchatabc.parkingadmin.android.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.google.android.gms.maps.model.LatLng

@Composable
fun LocationPickerDialogComposable(
    realtimeLocation: LatLng?,
    onDismissRequest: () -> Unit,
    onLocationSelected: (LatLng) -> Unit
) {
    Dialog(
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
            Modifier
                .background(Color.Black.copy(alpha = 0.5f))
                .fillMaxSize()
                .blur(30.dp)
        )
        Card(
            Modifier
                .fillMaxWidth()
                .aspectRatio(3f / 4f)
                .padding(32.dp)
        ) {
            GoogleMapsLocationPickerComposable(
                initialLocation = realtimeLocation,
                onLocationSet = { onLocationSelected(it) }
            )
        }
    }
}