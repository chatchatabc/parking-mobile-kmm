package com.chatchatabc.parkingadmin.android

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.content.FileProvider
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.chatchatabc.parking.activity.LocationActivity
import com.chatchatabc.parking.compose.Theme.AppTheme
import com.chatchatabc.parking.compose.wizard.CancelState
import com.chatchatabc.parking.compose.wizard.WizardLayout
import com.chatchatabc.parking.compose.wizard.WizardText
import com.chatchatabc.parking.compose.wizard.WizardTextField
import com.chatchatabc.parking.di.NewParkingLotModule
import com.chatchatabc.parking.viewModel.ImageUpload
import com.chatchatabc.parking.viewModel.NewParkingLotViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.vmadalin.easypermissions.EasyPermissions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import org.koin.android.ext.android.inject
import org.koin.core.context.loadKoinModules
import java.io.File

class NewParkingLotActivity : LocationActivity() {
    val koinModule = loadKoinModules(NewParkingLotModule)

    private val viewModel: NewParkingLotViewModel by inject()

    lateinit var imageUri: Uri

    val onCameraPermissionGranted =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (!EasyPermissions.hasPermissions(this, android.Manifest.permission.CAMERA)) {
            EasyPermissions.requestPermissions(
                this,
                "Camera permission is required to take photos",
                200,
                android.Manifest.permission.CAMERA
            )
        } else {
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
                val currentPage by viewModel.page.collectAsState()
                val pagerState = rememberPagerState()
                val progress by animateFloatAsState(
                    targetValue = 1f / 4 * (currentPage + 1),
                    label = "Wizard Progress Bar"
                )

                Column(modifier = Modifier.fillMaxSize()) {
//                    Box(
//                        Modifier
//                            .fillMaxWidth()
//                            .background(MaterialTheme.colorScheme.primaryContainer)
//                            .padding(32.dp, 16.dp)
//                    ) {
//                        Text(
//                            text = "Add a new parking lot",
//                            style = MaterialTheme.typography.headlineLarge,
//                            color = MaterialTheme.colorScheme.onPrimaryContainer,
//                            modifier = Modifier.fillMaxWidth(0.75f)
//                        )
//                    }

//                    LaunchedEffect(currentPage) {
//                        pagerState.animateScrollToPage(currentPage)
//                    }
//
//                    LinearProgressIndicator(
//                        modifier = Modifier.fillMaxWidth(),
//                        progress = progress
//                    )

                    val page by viewModel.page.collectAsState()
                    var cancelState by rememberSaveable { mutableStateOf(CancelState.NONE) }
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
                        title = "Register a new Parking Lot",
                        pages = 4,
                        page = page,
                        onNext = {
                            if (viewModel.validate(page)) {
                                viewModel.page.value += 1
                                if (page == 0) {
                                    viewModel.createDraft()
                                }
                                else {
                                    viewModel.saveDraft()
                                }
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
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                WizardText(text = "Parking Lot Details")
                                // Name Input
                                WizardTextField(
                                    value = parkingLotName,
                                    keyName = "name",
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
                                    keyName = "address",
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
                                    PositionCard(
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
                                    LocationPickerDialog(
                                        realtimeLocation = location?.toLatLng(),
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
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                WizardText(text = "Opening Time")
                                // TODO: Convert Time Input into Wizard
                                TimeInputField(
                                    time = openingTime,
                                    isError = errors.keys.contains("openingTime"),
                                    errorMessage = errors["openingTime"]
                                ) { hour, min ->
                                    viewModel.openingTime.value = Pair(hour, min)
                                    viewModel.validate(page)
                                }

                                WizardText(text = "Closing Time")
                                TimeInputField(
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
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
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
                                            UploadPhotoButton(
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
                                        ImageItem(image = image)
                                    }
                                }
                            }
                            // Page 4
                            3 -> {
                                // TODO: Create AE animation for parking lot added
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        // Lottie
                                        val lottieComposition = rememberLottieComposition(
                                            spec = LottieCompositionSpec.RawRes(R.raw.splash)
                                        )

                                        val lottieAnimation = animateLottieCompositionAsState(
                                            composition = lottieComposition.value
                                        )

                                        LottieAnimation(
                                            composition = lottieComposition.value,
                                            progress = { lottieAnimation.value },
                                            modifier = Modifier
                                                .size(200.dp)
                                                .padding(bottom = 32.dp)
                                        )
                                        Text("Your Parking Lot has been added!")
                                        Button(onClick = {
                                            // TODO: Redirect back to parking lot page
                                            finish()
                                        }) {
                                            Text(text = "Okay")
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

    @Composable
    fun TimeInputField(
        time: Pair<Int, Int>,
        isError: Boolean,
        errorMessage: String?,
        onApply: (Int, Int) -> Unit
    ) {
        var isTimePickerShown by remember { mutableStateOf(false) }

        val interactionSource = remember { MutableInteractionSource() }
        val isTextFieldPressed by interactionSource.collectIsPressedAsState()

        LaunchedEffect(isTextFieldPressed) {
            if (isTextFieldPressed) isTimePickerShown = true
        }

        if (isTimePickerShown) {
            TimePickerDialog(time = time, onCancel = { isTimePickerShown = false }) { hour, min ->
                isTimePickerShown = false
                onApply(hour, min)
            }
        }

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = time.toTimeStr(),
            onValueChange = {},
            readOnly = true,
            interactionSource = interactionSource,
            isError = isError,
            supportingText = {
                if (isError) {
                    errorMessage?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TimePickerDialog(time: Pair<Int, Int>, onCancel: () -> Unit, onApply: (Int, Int) -> Unit) {
        AlertDialog(
            onDismissRequest = {
                onCancel()
            },
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true),
        ) {
            (LocalView.current.parent as DialogWindowProvider).window.setDimAmount(0.25f)

            val state = rememberTimePickerState(
                initialHour = time.first,
                initialMinute = time.second,
                is24Hour = false
            )

            Card(colors = CardDefaults.elevatedCardColors()) {
                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Enter Time", style = MaterialTheme.typography.labelLarge)
                    TimePicker(state = state)
                    Row(
                        modifier = Modifier.align(Alignment.End),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onCancel() },
                            colors = ButtonDefaults.textButtonColors()
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = { onApply(state.hour, state.minute) },
                            colors = ButtonDefaults.textButtonColors()
                        ) {
                            Text("Apply")
                        }
                    }
                }
            }
        }
    }

    fun Pair<Int, Int>.toTimeStr(): String {
        // With AM/PM validation
        val hour = (if (first > 12) first - 12 else first).toString().padStart(2, '0')
        val min = second.toString().padStart(2, '0')
        val ampm = if (first < 12) "AM" else "PM"
        return "$hour:$min $ampm"
    }
}

@Composable
fun LocationPickerDialog(
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
            GoogleMapsLocationPicker(
                initialLocation = realtimeLocation,
                onLocationSet = { onLocationSelected(it) }
            )
        }
    }
}


@Composable
fun PositionCard(location: LatLng?, isError: Boolean, onCardPressed: () -> Unit) {
    Log.d("PositionCard", "Location: $location")
    val context = LocalContext.current

    Card(
        shape = MaterialTheme.shapes.extraSmall,
        modifier = Modifier
            .border(
                1.dp,
                color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.extraSmall
            )
    ) {
        val camera = rememberCameraPositionState()
        LaunchedEffect(location) {
            location?.let {
                Log.d("PositionCard", "Moving camera to $it")
                camera.move(CameraUpdateFactory.newCameraPosition(CameraPosition(it, 13f, 0f, 0f)))
            }
        }

        Box {
            GoogleMap(
                cameraPositionState = camera,
                uiSettings = MapUiSettings(
                    compassEnabled = false,
                    myLocationButtonEnabled = false,
                    mapToolbarEnabled = false,
                    scrollGesturesEnabled = false,
                    tiltGesturesEnabled = false,
                    zoomControlsEnabled = false,
                    zoomGesturesEnabled = false,
                ),
                properties = MapProperties(
                    mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.neutral),
                ),
                modifier = Modifier
                    .height(100.dp)
                    .blur(if (location == null) 10.dp else 0.dp)
            )
            IconButton(
                onClick = { onCardPressed() }, modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp), colors = IconButtonDefaults.filledIconButtonColors()
            ) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit Location", Modifier.size(16.dp))
            }
            if (location != null) {
                Image(
                    painterResource(
                        id = R.drawable.parking_icon
                    ),
                    contentDescription = "Pin",
                    modifier = Modifier
                        .wrapContentSize(Alignment.Center)
                        .align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun GoogleMapsLocationPicker(
    initialLocation: LatLng?,
    onLocationSet: (location: LatLng) -> Unit = { },
) {
    val camera = rememberCameraPositionState()
    var hasLocationLock by remember { mutableStateOf(false) }

    LaunchedEffect(hasLocationLock) {
        if (!hasLocationLock) {
            hasLocationLock = true
            camera.move(
                CameraUpdateFactory.newLatLng(
                    initialLocation ?: LatLng(
                        7.110192,
                        125.635160
                    )
                )
            )
        }
    }

    val moveCamera = { location: LatLng ->
        CoroutineScope(Dispatchers.Main).launch {
            camera.animate(
                update = CameraUpdateFactory.newCameraPosition(
                    CameraPosition(
                        location,
                        if (camera.position.zoom.toInt() != 15) 15f else camera.position.zoom,
                        camera.position.tilt,
                        camera.position.bearing
                    )
                ),
                durationMs = 500
            )
        }
    }

    Box(
        modifier = Modifier.clip(RoundedCornerShape(32.dp))
    ) {
        val context = LocalContext.current
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = camera,
            properties = MapProperties(
                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.neutral),
            ),
            onMyLocationClick = { moveCamera(LatLng(it.latitude, it.longitude)) },
            onPOIClick = { moveCamera(it.latLng) }
        )
        Image(
            painter = painterResource(id = R.drawable.parking_icon),
            contentDescription = "Parking Lot",
            modifier = Modifier.align(Alignment.Center)
        )
        Button(onClick = {
            Log.d("GoogleMapsLocationPicker", "Location set: ${camera.position.target}")
            onLocationSet(camera.position.target)
        }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Select this location")
        }
    }
}

@Preview
@Composable
fun UploadPhotoButton(onCameraClick: () -> Unit = {}, onUploadClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .aspectRatio(32 / 9f)
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.medium)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable {
                        onUploadClick()
                    }) {
                Column(
                    Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Filled.AddPhotoAlternate,
                        contentDescription = "Upload a photo",
                        Modifier.size(24.dp)
                    )
                    Text("Upload a photo")
                }
            }
            Text("OR")
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable {
                        onCameraClick()
                    }) {
                Column(
                    Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Filled.CameraAlt,
                        contentDescription = "Take a photo",
                        Modifier.size(24.dp)
                    )
                    Text("Take a photo")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageItem(
    image: ImageUpload,
    onDelete: () -> Unit = {},
) {
    var isMaximized by remember { mutableStateOf(false) }
    var confirmDeleteDialogOpened by remember { mutableStateOf(false) }

    if (isMaximized) {
        AlertDialog(
            onDismissRequest = { isMaximized = false },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = true,
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                val image = rememberAsyncImagePainter(model = image.remoteUrl!!)

                when (image.state) {
                    is AsyncImagePainter.State.Error -> {
                        Box(
                            Modifier
                                .padding(32.dp)
                                .align(Alignment.Center)
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                                )
                            ) {
                                Column(Modifier.padding(16.dp)) {
                                    Icon(
                                        Icons.Filled.Error,
                                        contentDescription = "Error loading image",
                                        tint = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    Text(
                                        "Error loading image",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text("An error occurred while loading the image. Please try again later.")
                                    Button(onClick = {
                                        isMaximized = false
                                    }) {
                                        Text("Close")
                                    }
                                }
                            }
                        }
                    }

                    is AsyncImagePainter.State.Success -> {
                        Image(
                            painter = image,
                            contentDescription = null,
                            alignment = Alignment.Center,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .zoomable(rememberZoomState(), false)
                                .fillMaxSize()
                        )
                    }

                    is AsyncImagePainter.State.Loading -> {
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    }

                    is AsyncImagePainter.State.Empty -> {
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    }
                }

                Row(modifier = Modifier.align(Alignment.TopEnd)) {
                    FilledTonalIconButton(
                        modifier = Modifier.padding(32.dp),
                        onClick = { isMaximized = false }
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", Modifier.size(24.dp))
                    }

                    FilledIconButton(
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        modifier = Modifier.padding(32.dp),
                        onClick = { confirmDeleteDialogOpened = true }
                    ) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Delete",
                            Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }

    if (confirmDeleteDialogOpened) {
        AlertDialog(onDismissRequest = {
            confirmDeleteDialogOpened = false
        }) {
            Dialog(onDismissRequest = { confirmDeleteDialogOpened = false }) {
                Column {
                    Text("Are you sure you want to delete this photo?")
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = {
                            confirmDeleteDialogOpened = false
                        }) {
                            Text("Cancel")
                        }
                        TextButton(onClick = {
                            confirmDeleteDialogOpened = false
                            isMaximized = false
                            onDelete()
                        }) {
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }

    Box(modifier = Modifier
        .aspectRatio(16 / 9f)
        .fillMaxWidth()
        .clip(MaterialTheme.shapes.medium)
        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.medium)
        .clickable {
            image.remoteUrl?.let {
                isMaximized = true
            }
        }
    ) {
        image.remoteUrl?.let {
            val image = rememberAsyncImagePainter(model = it)
            when (image.state) {
                is AsyncImagePainter.State.Error -> {
                    Box(
                        Modifier
                            .padding(32.dp)
                            .align(Alignment.Center)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Icon(
                                    Icons.Filled.Error,
                                    contentDescription = "Error loading image",
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Text(
                                    "Error loading image",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text("An error occurred while loading the image. Please try again later.")
                                Button(onClick = {
                                    isMaximized = false
                                }) {
                                    Text("Close")
                                }
                            }
                        }
                    }
                }

                is AsyncImagePainter.State.Success -> {
                    Image(
                        painter = image,
                        contentDescription = null,
                        alignment = Alignment.Center,
                        contentScale = ContentScale.FillWidth
                    )

                    FilledIconButton(
                        onClick = { onDelete() }, modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        Icon(
                            Icons.Filled.DeleteForever,
                            contentDescription = "Delete",
                            Modifier.size(24.dp)
                        )
                    }
                }

                is AsyncImagePainter.State.Loading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }

                is AsyncImagePainter.State.Empty -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

fun Pair<Double, Double>.toLatLng() = LatLng(first, second)