package com.chatchatabc.parkingadmin.android.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.unit.dp
import com.chatchatabc.parking.compose.Theme.extendedColors
import com.chatchatabc.parking.model.DashboardStatistics
import com.github.tehras.charts.piechart.PieChart
import com.github.tehras.charts.piechart.PieChartData

@Composable
fun DashboardViewComposable(
    stats: DashboardStatistics? = null,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onVehicleSearchClicked: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(8.dp)
                                .background(MaterialTheme.extendedColors.seedGreen)
                        )
                        Text("AVAILABLE", style = MaterialTheme.typography.labelSmall)
                    }
                    Text(
                        "${stats?.availableParkingCapacity ?: "..."}",
                        style = MaterialTheme.typography.displayLarge.copy(
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
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .size(8.dp)
                                        .background(MaterialTheme.extendedColors.seedOrange)
                                )
                                Text("LEAVING SOON", style = MaterialTheme.typography.labelSmall)
                            }
                            Text(
                                "${stats?.leavingSoon ?: "..."}",
                                style = MaterialTheme.typography.displaySmall.copy(
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
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .size(8.dp)
                                        .background(MaterialTheme.colorScheme.secondary)
                                )
                                Text("OCCUPIED", style = MaterialTheme.typography.labelSmall)
                            }
                            Text(
                                "${stats?.occupiedParkingCapacity ?: "..."}",
                                style = MaterialTheme.typography.displaySmall.copy(
                                    platformStyle = PlatformTextStyle(
                                        includeFontPadding = false
                                    ),
                                )
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .size(128.dp)
                        .padding(16.dp)
                        .align(Alignment.TopEnd)
                        .aspectRatio(1f)
                ) {
                    val total by remember {
                        derivedStateOf {
                            (stats?.availableParkingCapacity ?: 0) + (stats?.leavingSoon
                                ?: 0) + (stats?.occupiedParkingCapacity ?: 0)
                        }
                    }

                    PieChart(
                        modifier = Modifier.fillMaxSize(),
                        pieChartData = PieChartData(
                            listOf(
                                PieChartData.Slice(
                                    value = (stats?.availableParkingCapacity
                                        ?: 0).toFloat() / total,
                                    color = MaterialTheme.extendedColors.seedGreen
                                ),
                                PieChartData.Slice(
                                    value = (stats?.leavingSoon ?: 0).toFloat() / total,
                                    color = MaterialTheme.extendedColors.seedOrange
                                ),
                                PieChartData.Slice(
                                    value = (stats?.occupiedParkingCapacity ?: 0).toFloat() / total,
                                    color = MaterialTheme.colorScheme.secondary
                                ),
                            )
                        )
                    )
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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
                        "${stats?.traffic ?: 0}",
                        style = MaterialTheme.typography.displaySmall.copy(
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
                        "₱${stats?.traffic ?: 0}",
                        style = MaterialTheme.typography.displaySmall.copy(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            ),
                        )
                    )
                }
            }
        }
        OverrideCapacityCardComposable(
            capacity = stats?.availableParkingCapacity ?: 0,
            onIncrement = { onIncrement() },
            onDecrement = { onDecrement() }
        )
        VehicleSearchButtonComposable {
            onVehicleSearchClicked()
        }
    }
}