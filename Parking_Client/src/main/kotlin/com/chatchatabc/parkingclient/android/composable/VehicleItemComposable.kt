package com.chatchatabc.parkingclient.android.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Motorcycle
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.chatchatabc.parking.model.Vehicle
import com.chatchatabc.parking.viewModel.VehicleType

@Composable
fun VehicleItem(vehicle: Vehicle, onClick: (Vehicle) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick(vehicle)
            },
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(vehicle.name, style = MaterialTheme.typography.titleMedium)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    if (vehicle.type.toEnum<VehicleType>() == VehicleType.CAR) Icons.Filled.DirectionsCar else Icons.Filled.Motorcycle,
                    contentDescription = "Vehicle Type",
                    modifier = Modifier.size(24.dp)
                )
                Text(vehicle.plateNumber, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

inline fun <reified T : Enum<T>> Int.toEnum(): T {
    return enumValues<T>()[this]
}