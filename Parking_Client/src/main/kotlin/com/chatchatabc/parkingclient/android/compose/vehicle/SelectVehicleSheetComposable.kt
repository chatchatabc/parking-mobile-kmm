package com.chatchatabc.parkingclient.android.compose.vehicle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chatchatabc.parking.model.Vehicle
import com.chatchatabc.parking.model.pagination.Page
import com.chatchatabc.parking.model.response.ApiResponse
import com.chatchatabc.parking.model.response.FlowCall

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun SelectVehicleSheet(
    state: FlowCall<ApiResponse<Page<Vehicle>>>,
    onDismiss: (Boolean) -> Unit,
    onVehicleSelected: (Vehicle) -> Unit,
    onAddVehicleClicked: () -> Unit,
    onVehicleRefresh: () -> Unit,
) {
    ModalBottomSheet(
        modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 0.dp),
        onDismissRequest = {
            onDismiss(false)
        },
//        sheetState = rememberStandardBottomSheetState(SheetValue.Expanded),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Row {
                    Text("Select Vehicle", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.weight(1f))
                    FilledTonalIconButton(onClick = {
                        onVehicleRefresh()
                    }) {
                        Icon(
                            Icons.Filled.Refresh,
                            contentDescription = "Refresh Vehicles",
                            modifier = Modifier
                                .padding(8.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onAddVehicleClicked()
                    },
                    colors = CardDefaults.outlinedCardColors()
                ) {
                    Row(
                        Modifier.padding(16.dp, 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "Add Vehicle",
                            modifier = Modifier
                                .padding(8.dp)
                        )
                        Text("Add Vehicle")
                    }
                }
            }
            if (state.isLoading) {
                item {
                    Card(Modifier.fillMaxWidth()) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Finding your vehicles...")
                            CircularProgressIndicator()
                        }
                    }
                }
            } else {
                items(state.response?.data?.content ?: emptyList(), key = { item -> item.vehicleUuid }) { vehicle ->
                    VehicleItem(vehicle = vehicle) {
                        onVehicleSelected(it)
//                    coroutineScope.launch { modalSheetState.hide() }
                        onDismiss(true)
                    }
                }
            }
        }
    }
}