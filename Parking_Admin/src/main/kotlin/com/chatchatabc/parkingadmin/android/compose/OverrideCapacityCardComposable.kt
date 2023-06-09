package com.chatchatabc.parkingadmin.android.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.unit.dp
import com.chatchatabc.parking.compose.Theme.extendedColors

// TODO: Add functionality.
@Composable
fun OverrideCapacityCardComposable(
    capacity: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
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
                    .clip(CircleShape)
                    .clickable {
                        onDecrement()
                    }
                    .background(MaterialTheme.extendedColors.orange)
                    .fillMaxHeight()
                    .width(96.dp)
            ) {
                Icon(
                    Icons.Filled.Remove, null,
                    Modifier
                        .size(32.dp)
                        .align(Alignment.Center),
                    tint = MaterialTheme.extendedColors.onOrange
                )
            }
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("CAPACITY", style = MaterialTheme.typography.labelSmall)
                Text(
                    capacity.toString(), style = MaterialTheme.typography.displaySmall.copy(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        ),
                    )
                )
            }
            Box(
                Modifier
                    .clip(CircleShape)
                    .clickable {
                        onIncrement()
                    }
                    .background(MaterialTheme.extendedColors.green)
                    .fillMaxHeight()
                    .width(96.dp)
            ) {
                Icon(
                    Icons.Filled.Add, null,
                    Modifier
                        .size(32.dp)
                        .align(Alignment.Center),
                    tint = MaterialTheme.extendedColors.onGreen
                )
            }
        }
    }
}