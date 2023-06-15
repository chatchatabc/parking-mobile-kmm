package com.chatchatabc.parkingclient.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.chatchatabc.parking.compose.Theme.AppTheme
import com.chatchatabc.parking.compose.wizard.CancelState
import com.chatchatabc.parking.compose.wizard.WizardLayout
import com.chatchatabc.parking.compose.wizard.WizardSegmentedSelector
import com.chatchatabc.parking.compose.wizard.WizardTextField
import com.chatchatabc.parking.di.NewVehicleModule
import com.chatchatabc.parking.model.VehicleType
import com.chatchatabc.parking.viewModel.NewVehicleViewModel
import org.koin.android.ext.android.inject
import org.koin.core.context.loadKoinModules

class NewVehicleActivity: ComponentActivity() {
    val koinModule = loadKoinModules(NewVehicleModule)

    val viewModel: NewVehicleViewModel by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val page by viewModel.page.collectAsState()
            val name by viewModel.name.collectAsState()
            val platenumber by viewModel.plateNumber.collectAsState()
            val type by viewModel.type.collectAsState()
            val errors by viewModel.errors.collectAsState()

            val isLoading by viewModel.isLoading.collectAsState()

            var cancelState by rememberSaveable { mutableStateOf(CancelState.NONE) }

            AppTheme {
                Surface {
                    // Use WizardView here, we might need additional pages in the future.
                    WizardLayout(
                        errors = errors,
                        onErrorDismiss = {
                            viewModel.errors.value = errors.filterKeys { key -> key != "type" }
                        },
                        isLoading = isLoading,
                        title = "Register a New Vehicle",
                        pages = 2,
                        page = page,
                        onNext = {
                            if (viewModel.validate(page)) viewModel.page.value += 1
                        },
                        onPrevious = {
                            viewModel.page.value -= 1
                        },
                        onSubmit = {
                            viewModel.validateAndSubmit()
                        },
                        cancelState = cancelState,
                        onFinish = {
                            finish()
                        },
                        onCancelStateChanged = { state ->
                            cancelState = state
                        },
                    ) { page ->
                        when (page) {
                            0 -> Column {
                                WizardSegmentedSelector(
                                    label = "Vehicle Type",
                                    keyName = "type",
                                    items = listOf(VehicleType.CAR, VehicleType.MOTORCYCLE),
                                    itemLabels = {
                                        when (it) {
                                            VehicleType.CAR -> "Car"
                                            VehicleType.MOTORCYCLE -> "Motorcycle"
                                            VehicleType.NONE -> "None"
                                        }
                                    },
                                    errors = errors,
                                    selected = type,
                                    onSelected = {
                                        viewModel.type.value = it
                                        viewModel.errors.value = errors.filterKeys { key -> key != "type" }
                                    })
                                WizardTextField(
                                    value = name,
                                    keyName = "name",
                                    onValueChange = {
                                        viewModel.name.value = it
                                        viewModel.errors.value = errors.filterKeys { key -> key != "name" }
                                    },
                                    label = "Name",
                                    errors = errors
                                )
                                WizardTextField(
                                    value = platenumber.uppercase(),
                                    keyName = "platenumber",
                                    onValueChange = {
                                        viewModel.plateNumber.value = it
                                        viewModel.errors.value = errors.filterKeys { key -> key != "platenumber" }
                                    },
                                    label = "Plate Number",
                                    errors = errors,
                                    supportingText = when (type) {
                                        VehicleType.CAR -> "e.g. ABC-1234 or AB-12345 (for temporary plates)"
                                        VehicleType.MOTORCYCLE -> "e.g. ABC-1234 or 1234-1234567 (for temporary plates)"
                                        VehicleType.NONE -> "Select a vehicle type to view valid plate number format."
                                    }
                                )
                            }
                            1 -> {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Filled.Check, "Success")
                                        Text("Vehicle successfully registered!")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}