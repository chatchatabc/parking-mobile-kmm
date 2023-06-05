package com.chatchatabc.parkingadmin.android.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.unit.dp

// TODO: Add functionality.
@Composable
fun OverrideCapacityCardComposable() {
    Card {
        Row(
            Modifier
                .height(IntrinsicSize.Min)
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .background(Color.Red)
                    .fillMaxHeight()
                    .width(96.dp)
            ) {
                Icon(
                    Icons.Filled.Remove, null,
                    Modifier
                        .size(32.dp)
                        .align(Alignment.Center)
                )
            }
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("CAPACITY", style = MaterialTheme.typography.labelSmall)
                Text(
                    "200", style = MaterialTheme.typography.displaySmall.copy(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        ),
                    )
                )
            }
            Box(
                Modifier
                    .background(Color.Green)
                    .fillMaxHeight()
                    .width(96.dp)
            ) {
                Icon(
                    Icons.Filled.Remove, null,
                    Modifier
                        .size(32.dp)
                        .align(Alignment.Center)
                )
            }
        }
    }
}