package com.chatchatabc.parkingclient.android

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.location.Location
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Motorcycle
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Report
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.chatchatabc.parking.activity.LocationActivity
import com.chatchatabc.parking.compose.Theme.AppTheme
import com.chatchatabc.parking.di.MainMapModule
import com.chatchatabc.parking.di.ParkingRealmModule
import com.chatchatabc.parking.model.Vehicle
import com.chatchatabc.parking.model.VehicleType
import com.chatchatabc.parking.realm.ParkingLotRealmObject
import com.chatchatabc.parking.viewModel.ClientMainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.context.loadKoinModules
import java.lang.Integer.max
import android.graphics.Color as AndroidColor


class MainActivity : LocationActivity() {
    val koinModule = loadKoinModules(listOf(ParkingRealmModule, MainMapModule))
    val viewModel: ClientMainViewModel by inject()

    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val visibleParkingLots by viewModel.visibleParkingLots.collectAsState(listOf())

                    Scaffold(
                        bottomBar = {
                            NavigationBar {
                                NavigationBarItem(
                                    selected = true,
                                    onClick = { /*TODO*/ },
                                    icon = { Icon(Icons.Filled.Map, "Parking") },
                                    label = { Text("Parking") }
                                )
                                NavigationBarItem(
                                    selected = false,
                                    onClick = { /*TODO*/ },
                                    icon = { Icon(Icons.Outlined.DirectionsCar, "Jeepney") },
                                    label = { Text("Jeepney") }
                                )

                                NavigationBarItem(
                                    selected = false,
                                    onClick = { /*TODO*/ },
                                    icon = { Icon(Icons.Outlined.Report, "Report") },
                                    label = { Text("Report") }
                                )

                                NavigationBarItem(
                                    selected = false,
                                    onClick = {
                                        startActivity(
                                            Intent(
                                                this@MainActivity,
                                                AccountActivity::class.java
                                            )
                                        )
                                    },
                                    icon = { Icon(Icons.Outlined.AccountCircle, "My Profile") },
                                    label = { Text("Me") }
                                )
                            }
                        }
                    ) { padding ->
                        var hasPermission by remember { mutableStateOf(false) }
                        withLocationPermission {
                            hasPermission = true
                        }

                        if (hasPermission) {
                            MapView(
                                pins = visibleParkingLots,
                                modifier = Modifier.padding(padding),
                                onMapLoaded = {
                                    viewModel.syncParkingLots()
                                },
                            ) {
                                viewModel.getParkingLotsInRange(it)
                            }
                        }

                        val vehicleSelectorShown by viewModel.isSelectingVehicle.collectAsState()

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding)
                        ) {
                            FloatingActionButton(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(32.dp),
                                onClick = {
                                    viewModel.openVehicleSelector()
                                }
                            ) {
                                Icon(Icons.Filled.QrCode, "Show Parking QR Code")
                            }
                        }

                        var isQRShown by rememberSaveable { mutableStateOf(false) }
                        val vehicles by viewModel.vehicles.collectAsState()

                        if (vehicleSelectorShown) {
                            SelectVehicleSheet(
                                vehicles = vehicles,
                                onDismiss = {
                                    viewModel.isSelectingVehicle.value = it
                                },
                                onVehicleSelected = {
                                    viewModel.isSelectingVehicle.value = false
                                    viewModel.selectedVehicle.value = it
                                    viewModel.createQRFromString(it.vehicleUuid)
                                    isQRShown = true
                                },
                                onAddVehicleClicked = {
                                    Intent(this@MainActivity, NewVehicleActivity::class.java).also {
                                        startActivity(it)
                                    }
                                }
                            )
                        }

                        val selectedVehicle by viewModel.selectedVehicle.collectAsState()

                        val isLoadingQR by viewModel.isLoadingQRCode.collectAsState()
                        val qrCode by viewModel.qrCode.collectAsState()

                        if (isQRShown && selectedVehicle != null) {
                            AlertDialog(onDismissRequest = {
                                isQRShown = false
                            }) {
                                Column {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    ) {
                                        Box(Modifier.padding(16.dp)) {
                                            Column(
                                                Modifier.fillMaxWidth(),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    "Parking QR Code",
                                                    style = MaterialTheme.typography.labelLarge
                                                )
                                                Text(
                                                    selectedVehicle?.plateNumber ?: "",
                                                    style = MaterialTheme.typography.headlineLarge,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    "Show this QR code to the parking attendant to enter the parking lot.",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    textAlign = TextAlign.Center,
                                                )
                                            }
                                        }
                                    }
                                    Card(
                                        modifier = Modifier
                                            .aspectRatio(1f)
                                            .fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color.White
                                        )
                                    ) {
                                        Box(Modifier.padding(32.dp)) {
                                            if (isLoadingQR) {
                                                CircularProgressIndicator(Modifier.align(Alignment.Center))
                                            } else {
                                                qrCode?.let {
                                                    Image(
                                                        it.asImageBitmap(),
                                                        contentDescription = "Parking QR Code",
                                                        modifier = Modifier.align(Alignment.Center)
                                                    )
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
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MapView(
    modifier: Modifier = Modifier,
    pins: List<ParkingLotRealmObject>,
    onMapLoaded: () -> Unit,
    onMapMoved: (LatLngBounds) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val camera = rememberCameraPositionState()

    LaunchedEffect(camera.isMoving) {
        if (!camera.isMoving) {
            camera.projection?.let {
                onMapMoved(it.visibleRegion.latLngBounds)
            }
        }
    }

    Box(modifier) {
        GoogleMap(
            cameraPositionState = camera,
            properties = MapProperties(
                isBuildingEnabled = true,
                isIndoorEnabled = false,
                isMyLocationEnabled = true,
                isTrafficEnabled = false,
                mapType = MapType.NORMAL
            ),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = false,
                zoomControlsEnabled = false
            ),
            onMyLocationClick = { location ->
                coroutineScope.launch {
                    camera.animate(
                        CameraUpdateFactory.newCameraPosition(
                            CameraPosition.builder(camera.position)
                                .target(location.toLatLng())
                                .zoom(if (camera.position.zoom < 15) 15f else camera.position.zoom)
                                .build()
                        ), 1500
                    )
                }
            },
            onMapLoaded = {
                onMapLoaded()
                camera.projection?.let {
                    onMapMoved(it.visibleRegion.latLngBounds)
                }
            }
        ) {
            MarkerContainer(pins = pins)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@GoogleMapComposable
@Composable
fun MarkerContainer(
    pins: List<ParkingLotRealmObject>
) {
    val context = LocalContext.current

    val primaryColor = MaterialTheme.colorScheme.primary

    pins.forEach { pin ->
        val marker = rememberMarkerState(pin.id, LatLng(pin.latitude!!, pin.longitude!!))

        val icon = remember {
            BitmapDescriptorFactory.fromBitmap(
                createCustomMarkerBitmap(
                    context = context,
                    name = pin.name,
                    color = primaryColor.toArgb(),
                    subtext = "12/33 occupied"
                )
            )
        }

        Marker(state = marker, icon = icon)

        Circle(
            center = marker.position,
            radius = 20.0,
            fillColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        )
    }
}

fun Location.toLatLng() = LatLng(latitude, longitude)

private fun createCustomMarkerBitmap(
    name: String,
    subtext: String,
    context: Context,
    color: Int
): Bitmap {
    val drawable = ContextCompat.getDrawable(context, R.drawable.parking_icon)
    val iconWidth = drawable?.intrinsicWidth ?: 0
    val iconHeight = drawable?.intrinsicHeight ?: 0

    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        setColor(AndroidColor.BLACK)
        textSize = 32f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = Paint.Align.LEFT
    }

    val outlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        setColor(AndroidColor.WHITE)
        style = Paint.Style.STROKE
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        strokeWidth = 2f
        textSize = 32f
        textAlign = Paint.Align.LEFT
    }

    val subtextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        setColor(AndroidColor.BLACK)
        textSize = 24f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = Paint.Align.LEFT
    }

    val subtextOutlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        setColor(AndroidColor.WHITE)
        style = Paint.Style.STROKE
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        strokeWidth = 1f
        textSize = 24f
        textAlign = Paint.Align.LEFT
    }

    val textBounds = Rect()
    textPaint.getTextBounds(name, 0, name.length, textBounds)

    val subtextBounds = Rect()
    subtextPaint.getTextBounds(subtext, 0, subtext.length, subtextBounds)

    val bitmapWidth = (iconWidth + max(textBounds.width(), subtextBounds.width())) * 2
    val bitmapHeight = max(iconHeight, textBounds.height() + subtextBounds.height())

    val outputBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(outputBitmap)

    // Center the icon on the canvas
    val iconX = (bitmapWidth / 2) - (iconWidth / 2)
    val iconY = (bitmapHeight - iconHeight) / 2
    drawable?.setBounds(iconX, iconY, iconX + iconWidth, iconY + iconHeight)
    drawable?.draw(canvas)

    // Draw the text to the right of the icon
    val textX = (bitmapWidth / 2) + (iconWidth / 2) + 10
    val textY = bitmapHeight / 2 - (textBounds.height() / 2) + 10
    canvas.drawText(name, textX.toFloat(), textY.toFloat(), outlinePaint)
    canvas.drawText(name, textX.toFloat(), textY.toFloat(), textPaint)

    // Draw the subtext below the text
    val subtextY = textY + textBounds.height()
    canvas.drawText(subtext, textX.toFloat(), subtextY.toFloat(), subtextOutlinePaint)
    canvas.drawText(subtext, textX.toFloat(), subtextY.toFloat(), subtextPaint)

    return outputBitmap
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SelectVehicleSheet(
    vehicles: List<Vehicle>,
    onDismiss: (Boolean) -> Unit,
    onVehicleSelected: (Vehicle) -> Unit,
    onAddVehicleClicked: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = {
            onDismiss(false)
        },
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text("Select Vehicle", style = MaterialTheme.typography.titleLarge)
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
            items(vehicles, key = { item -> item.vehicleUuid }) { vehicle ->
                VehicleItem(vehicle = vehicle) {
                    onVehicleSelected(it)
//                    coroutineScope.launch { modalSheetState.hide() }
                    onDismiss(false)
                }
            }
        }
    }

//    ModalBottomSheetLayout(
////        modifier = Modifier.nestedScroll(scroll),
//        sheetState = modalSheetState,
//        sheetShape = MaterialTheme.shapes.large,
//        sheetContent = {
//
//        }
//    ) {
//        Box(Modifier.fillMaxSize())
//    }
}

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