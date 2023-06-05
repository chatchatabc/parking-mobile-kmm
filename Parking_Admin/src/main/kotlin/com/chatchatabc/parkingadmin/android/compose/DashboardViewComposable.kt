package com.chatchatabc.parkingadmin.android.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun DashboardViewComposable() {
    // TODO: Use better colors. This does not look good at the moment.
    // TODO: Connect to actual data source.
    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(16.dp)) {
        Card(
            Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Box(Modifier.fillMaxSize()) {
                Column(Modifier.padding(16.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier
                                .clip(CircleShape)
                                .size(8.dp)
                                .background(Color.Green)
                        )
                        Text("AVAILABLE", style = MaterialTheme.typography.labelSmall)
                    }
                    Text(
                        "200", style = MaterialTheme.typography.displayLarge.copy(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            ),
                        )
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Column {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    Modifier
                                        .clip(CircleShape)
                                        .size(8.dp)
                                        .background(Color.Yellow)
                                )
                                Text("LEAVING SOON", style = MaterialTheme.typography.labelSmall)
                            }
                            Text(
                                "17", style = MaterialTheme.typography.displaySmall.copy(
                                    platformStyle = PlatformTextStyle(
                                        includeFontPadding = false
                                    ),
                                )
                            )
                        }
                        Column {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    Modifier
                                        .clip(CircleShape)
                                        .size(8.dp)
                                        .background(Color.Gray)
                                )
                                Text("OCCUPIED", style = MaterialTheme.typography.labelSmall)
                            }
                            Text(
                                "139", style = MaterialTheme.typography.displaySmall.copy(
                                    platformStyle = PlatformTextStyle(
                                        includeFontPadding = false
                                    ),
                                )
                            )
                        }
                    }
                }
                Column(
                    Modifier
                        .width(82.dp)
                        .padding(16.dp)
                        .align(Alignment.TopEnd),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        Modifier
                            .weight(200f)
                            .clip(RoundedCornerShape(3.dp))
                            .fillMaxWidth()
                            .background(Color.Green)
                    )
                    Box(
                        Modifier
                            .weight(17f)
                            .clip(RoundedCornerShape(3.dp))
                            .fillMaxWidth()
                            .background(Color.Yellow)
                    )
                    Box(
                        Modifier
                            .weight(139f)
                            .clip(RoundedCornerShape(3.dp))
                            .fillMaxWidth()
                            .background(Color.Gray)
                    )
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Card(modifier = Modifier.weight(1f)) {
                Column(
                    Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("TRAFFIC", style = MaterialTheme.typography.labelSmall)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Filled.ArrowUpward, null, Modifier.size(16.dp))
                            Text("32.2%", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    Text(
                        "332", style = MaterialTheme.typography.displaySmall.copy(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            ),
                        )
                    )
                }
            }
            Card(modifier = Modifier.weight(1f)) {
                Column(
                    Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("PROFIT", style = MaterialTheme.typography.labelSmall)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Filled.ArrowUpward, null, Modifier.size(16.dp))
                            Text("32.2%", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    Text(
                        "₱2.5k", style = MaterialTheme.typography.displaySmall.copy(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            ),
                        )
                    )
                }
            }
        }
        OverrideCapacityCardComposable()
        VehicleSearchButtonComposable()
    }
}