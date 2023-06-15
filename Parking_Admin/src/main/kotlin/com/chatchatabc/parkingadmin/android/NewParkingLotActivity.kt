package com.chatchatabc.parkingadmin.android

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.chatchatabc.parking.activity.LocationActivity
import com.chatchatabc.parking.compose.Theme.AppTheme
import com.chatchatabc.parking.compose.wizard.CancelState
import com.chatchatabc.parking.compose.wizard.WizardCheckBox
import com.chatchatabc.parking.compose.wizard.WizardLayout
import com.chatchatabc.parking.compose.wizard.WizardSegmentedSelector
import com.chatchatabc.parking.compose.wizard.WizardText
import com.chatchatabc.parking.compose.wizard.WizardTextField
import com.chatchatabc.parking.di.NewParkingLotModule
import com.chatchatabc.parking.viewModel.NewParkingLotViewModel
import com.chatchatabc.parking.viewModel.RateBuilderViewModel
import com.chatchatabc.parking.viewModel.RateInterval
import com.chatchatabc.parking.viewModel.RateType
import com.chatchatabc.parkingadmin.android.compose.ImageItemComposable
import com.chatchatabc.parkingadmin.android.compose.LocationPickerDialogComposable
import com.chatchatabc.parkingadmin.android.compose.PositionCardComposable
import com.chatchatabc.parkingadmin.android.compose.TimeInputFieldComposable
import com.chatchatabc.parkingadmin.android.compose.UploadPhotoButtonComposable
import com.google.android.gms.maps.model.LatLng
import com.vmadalin.easypermissions.EasyPermissions
import org.koin.android.ext.android.inject
import org.koin.core.context.loadKoinModules
import java.io.File

class NewParkingLotActivity : LocationActivity() {
    val koinModule = loadKoinModules(NewParkingLotModule)

    private val viewModel: NewParkingLotViewModel by inject()

    lateinit var imageUri: Uri

    val realtimeValidationEnabled: Boolean = false

    val onCameraPermissionGranted = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                val imageFile = File.createTempFile(
                    "image",
                    ".jpg",
                    filesDir
                )

                imageUri = FileProvider.getUriForFile(
                    this,
                    "com.chatchatabc.parkingadmin.fileprovider",
                    imageFile
                )

                onImageCaptured.launch(imageUri)
            }
        }

    val onImageCaptured =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                viewModel.addToUploadQueue(imageUri)
            }
        }

    val onImageSelected = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let { uri ->
            Log.d("URI", "uri: $uri")
            viewModel.addToUploadQueue(uri)
        }
    }

    val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                onCameraPermissionGranted.launch(android.Manifest.permission.CAMERA)
            }
        }

    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent.getStringExtra("parkingLot")?.let {
            viewModel.setId(it)
        }

        setContent {
            AppTheme {
                Column(modifier = Modifier.fillMaxSize()) {
                    val page by viewModel.page.collectAsState()
                    var cancelState by rememberSaveable { mutableStateOf(CancelState.NONE) }

                    val isLoading by viewModel.isLoading.collectAsState()

                    val parkingLotName by viewModel.parkingLotName.collectAsState()
                    val parkingLotAddress by viewModel.parkingLotAddress.collectAsState()
                    val location by viewModel.location.collectAsState()
                    val description by viewModel.description.collectAsState()

                    val errors by viewModel.errors.collectAsState()

                    val openingTime by viewModel.openingTime.collectAsState()
                    val closingTime by viewModel.closingTime.collectAsState()

                    val daysOpen by viewModel.daysOpen.collectAsState()

                    val images by viewModel.images.collectAsState()

                    WizardLayout(
                        errors = errors,
                        onErrorDismiss = {
                            viewModel.errors.value = errors.filterKeys { key -> key != "type" }
                        },
                        isLoading = isLoading,
                        title = "Register a new Parking Lot",
                        pages = 5,
                        page = page,
                        onNext = {
                            if (viewModel.validate(page)) {
                                if (viewModel.parkingLotUuid.value == null) {
                                    viewModel.createDraft()
                                } else {
                                    viewModel.saveDraft()
                                }
                                viewModel.page.value += 1
                            }
                        },
                        onPrevious = {
                            viewModel.page.value -= 1
                        },
                        onSubmit = {
                            viewModel.validateAll()
                            if (errors.isEmpty()) {
                                viewModel.setToPending()
                            }
                        },
                        cancelState = cancelState,
                        onFinish = {
                            finish()
                        },
                        onCancelStateChanged = { state ->
                            cancelState = state
                            if (cancelState == CancelState.CONFIRM) {
                                viewModel.saveDraft()
                                finish()
                            }
                        }
                    ) { page ->
                        when (page) {
                            // Page 1
                            0 -> Column(
                                modifier = Modifier.fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                                    .padding(32.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                WizardText(text = "Parking Lot Details")
                                // Name Input
                                WizardTextField(
                                    value = parkingLotName,
                                    keyName = "parkingLotName",
                                    onValueChange = {
                                        viewModel.parkingLotName.value = it
                                        viewModel.errors.value =
                                            errors.filterKeys { key -> key != "parkingLotName" }
                                    },
                                    label = "Name*",
                                    errors = errors
                                )
                                // Address Input
                                WizardTextField(
                                    value = parkingLotAddress,
                                    keyName = "parkingLotAddress",
                                    onValueChange = {
                                        viewModel.parkingLotAddress.value = it
                                        viewModel.errors.value =
                                            errors.filterKeys { key -> key != "parkingLotAddress" }
                                    },
                                    label = "Address*",
                                    errors = errors
                                )
                                WizardText(text = "Location*")

                                var locationDialogShown by remember { mutableStateOf(false) }

                                // TODO: Convert Position Card to Wizard
                                Column {
                                    PositionCardComposable(
                                        location = location?.toLatLng(),
                                        isError = errors.keys.contains("location")
                                    ) {
                                        Log.d("NewParkingLotActivity", "Location card pressed")
                                        withLocationPermission {
                                            locationDialogShown = true
                                        }
                                    }
                                    if (errors.keys.contains("location")) {
                                        Text(
                                            "Location must be set",
                                            color = MaterialTheme.colorScheme.error,
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(
                                                start = 16.dp,
                                                top = 4.dp
                                            )
                                        )
                                    }
                                }

                                // Description Input
                                WizardTextField(
                                    value = description,
                                    keyName = "description",
                                    onValueChange = {
                                        viewModel.description.value = it
                                    },
                                    label = "Description",
                                    errors = errors
                                )

                                LaunchedEffect(locationDialogShown) {
                                    if (locationDialogShown) locationService.startListening()
                                    else locationService.stopListening()
                                }

                                if (locationDialogShown) {
                                    LocationPickerDialogComposable(
                                        locationService = locationService,
                                        initialLocation = location?.toLatLng(),
                                        onDismissRequest = { locationDialogShown = false },
                                        onLocationSelected = { latlng ->
                                            viewModel.location.value =
                                                Pair(latlng.latitude, latlng.longitude)
                                            locationDialogShown = false
                                            viewModel.errors.value =
                                                errors.filterKeys { key -> key != "location" }
                                        }
                                    )
                                }
                            }
                            // Page 2
                            1 -> Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                                    .padding(32.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                WizardText(text = "Opening Time")
                                // TODO: Convert Time Input into Wizard
                                TimeInputFieldComposable(
                                    time = openingTime,
                                    isError = errors.keys.contains("openingTime"),
                                    errorMessage = errors["openingTime"]
                                ) { hour, min ->
                                    viewModel.openingTime.value = Pair(hour, min)
                                    viewModel.validate(page)
                                }

                                WizardText(text = "Closing Time")
                                TimeInputFieldComposable(
                                    time = closingTime,
                                    isError = false,
                                    errorMessage = null
                                ) { hour, min ->
                                    viewModel.closingTime.value = Pair(hour, min)
                                    viewModel.validate(page)
                                }

                                WizardText(text = "Days Open")

                                // TODO: Convert the following into a wizard
                                Column {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        listOf("S", "M", "T", "W", "T", "F", "S").withIndex()
                                            .forEach { (index, str) ->
                                                FilledTonalIconToggleButton(
                                                    checked = daysOpen.contains(index),
                                                    onCheckedChange = { checked ->
                                                        viewModel.daysOpen.value =
                                                            if (checked) daysOpen.plus(index) else daysOpen.minus(
                                                                index
                                                            )
                                                        viewModel.validate(page)
                                                    },
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .aspectRatio(1f),
                                                    colors = IconButtonDefaults.filledIconToggleButtonColors(
                                                        contentColor = MaterialTheme.colorScheme.onSurface
                                                    )
                                                ) {
                                                    Text(
                                                        str,
                                                        style = MaterialTheme.typography.labelLarge
                                                    )
                                                }
                                            }
                                    }

                                    if (errors.keys.contains("daysOpen")) {
                                        Text(
                                            "At least one day must be selected",
                                            color = MaterialTheme.colorScheme.error,
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                        )
                                    }
                                }
                            }
                            // Page 3
                            2 -> {
                                RateBuilder(
                                    viewModel = viewModel.rateBuilderViewModel,
                                )
                            }
                            // Page 4
                            3 -> {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    contentPadding = PaddingValues(32.dp),
                                ) {
                                    this.item(span = { GridItemSpan(2) }) {
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(8.dp),
                                        ) {
                                            Text(
                                                "Add a photo of your parking lot",
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                            Text(
                                                "You'll need to add minimum of 1, with a maximum of 6. You will be able to set this later.",
                                                style = MaterialTheme.typography.bodySmall,
                                                modifier = Modifier.padding(top = 8.dp)
                                            )
                                            UploadPhotoButtonComposable(
                                                onCameraClick = {
                                                    if (EasyPermissions.hasPermissions(
                                                            this@NewParkingLotActivity,
                                                            android.Manifest.permission.CAMERA
                                                        )
                                                    ) {
                                                        onCameraPermissionGranted.launch(android.Manifest.permission.CAMERA)
                                                    } else {
                                                        EasyPermissions.requestPermissions(
                                                            this@NewParkingLotActivity,
                                                            "This app needs access to your camera",
                                                            200,
                                                            android.Manifest.permission.CAMERA
                                                        )
                                                    }
                                                }, onUploadClick = {
                                                    onImageSelected.launch("image/*")
                                                }
                                            )
                                        }

                                    }
                                    items(images) { image ->
                                        ImageItemComposable(image = image)
                                    }
                                }
                            }
                            // Page 5
                            4 -> {
                                // TODO: Create AE animation for parking lot added
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.CheckCircle,
                                            contentDescription = "Parking Lot Added",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(64.dp)
                                        )
                                        Text("Your Parking Lot has been added!")
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

fun Pair<Double, Double>.toLatLng() = LatLng(first, second)

@Composable
fun RateBuilder(
    viewModel: RateBuilderViewModel
) {
    val errors by viewModel.errors.collectAsState()

    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(32.dp)) {
        Text("Rate Builder", style = MaterialTheme.typography.titleMedium)
        Text(
            "You can set your own rates for your parking lot. The calculation of your rate will depend on the values you enter here.",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 8.dp)
        )

        val rate by viewModel.rateType.collectAsState()

        WizardSegmentedSelector(
            keyName = "rateType",
            label = "Rate Type",
            description = "Choose the type of rate you want to set",
            items = listOf(RateType.Fixed, RateType.Flexible),
            disabledItems = listOf(RateType.Flexible),
            itemLabels = { it.name },
            selected = rate,
            errors = errors,
            onSelected = {
                viewModel.rateType.value = it
            }
        )

        val rateInterval by viewModel.rateInterval.collectAsState()

        if (rate != RateType.None) {
            Spacer(modifier = Modifier.height(4.dp))
            Text("Interval", style = MaterialTheme.typography.labelLarge)
            Text(
                "Choose 'Hourly' or 'Daily' to determine the frequency of rate application. 'Free hours' offer no-cost parking for the initial set duration, while 'Pay for free hours when exceeded' ensures the free hours are charged if parking duration extends beyond them.",
                style = MaterialTheme.typography.labelMedium
            )

            WizardSegmentedSelector(
                keyName = "rateInterval",
                label = "Interval",
                description = "Choose the interval of your rate",
                items = listOf(RateInterval.Hourly, RateInterval.Daily),
                itemLabels = { it.name },
                selected = rateInterval,
                errors = errors,
                onSelected = {
                    viewModel.rateInterval.value = it
                }
            )
        }

        val freeHours by viewModel.freeHours.collectAsState()
        val payForFreeHours by viewModel.payFreeHoursWhenExceeded.collectAsState()

        Spacer(modifier = Modifier.height(4.dp))
        if (rateInterval == RateInterval.Hourly) {
            // Free Hours - WizardTextField
            WizardTextField(
                value = freeHours.toString(),
                label = "Free Hours",
                keyboardType = KeyboardType.Number,
                errors = errors,
                supportingText = "The number of hours that will be free of charge.",
                onValueChange = {
                    viewModel.freeHours.value = it
                },
                keyName = "freeHours"
            )
            WizardCheckBox(
                value = payForFreeHours,
                onValueChange = {
                    viewModel.payFreeHoursWhenExceeded.value = it
                }, label = "Pay free hours when exceeded",
                supportingText = "If the parking duration exceeds the free hours, the free hours will be charged.",
                errors = errors,
                keyName = "freeHours"
            )
        }

        val startRate by viewModel.startRate.collectAsState()
        val rateValue by viewModel.rateValue.collectAsState()

        if (rateInterval != RateInterval.None) {
            // Start Rate
            WizardTextField(
                value = startRate.toString(),
                label = "Start Rate",
                keyboardType = KeyboardType.Number,
                errors = errors,
                onValueChange = {
                    viewModel.startRate.value = it
                },
                keyName = "startRate"
            )
            // Hourly/Daily Rate
            WizardTextField(
                value = rateValue.toString(),
                label = "${if (rateInterval == RateInterval.Hourly) "Hourly" else "Daily"} Rate",
                keyboardType = KeyboardType.Number,
                errors = errors,
                onValueChange = {
                    viewModel.rateValue.value = it
                },
                keyName = "rateValue"
            )
        }
    }
}