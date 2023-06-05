package com.chatchatabc.parkingclient.android.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GenericMenuItemComposable(
    label: String,
    content: @Composable () -> Unit,
    onClick: (() -> Unit) = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Row(Modifier.padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                label,
                color = Color.Black,
                // Add padding left
                modifier = Modifier.padding(start = 24.dp)
            )
            Spacer(Modifier.weight(1f))
            content()
            IconButton(onClick = onClick) {
                Icon(Icons.Filled.ChevronRight, contentDescription = "Next", tint = Color.Black)
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun PreviewGenericMenuItem() {
    GenericMenuItemComposable("Test", content = {
        Text("Test")
    }, onClick = {

    })
}