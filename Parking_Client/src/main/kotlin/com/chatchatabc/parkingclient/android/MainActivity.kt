package com.chatchatabc.parkingclient.android

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color.TRANSPARENT
import android.location.Location
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Report
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewModelScope
import com.chatchatabc.parking.activity.LocationActivity
import com.chatchatabc.parking.compose.Theme.AppTheme
import com.chatchatabc.parking.model.Jeepney
import com.chatchatabc.parking.model.response.buildPath
import com.chatchatabc.parkingclient.android.compose.account.GenericMenuItemComposable
import com.chatchatabc.parkingclient.android.compose.account.MenuSubtextComposable
import com.chatchatabc.parkingclient.android.compose.main.SearchBarComposable
import com.chatchatabc.parkingclient.android.compose.vehicle.SelectVehicleSheet
import com.chatchatabc.parkingclient.android.di.MainMapModule
import com.chatchatabc.parkingclient.android.viewmodel.ClientMainViewModel
import com.chatchatabc.parkingclient.android.viewmodel.JeepneyViewModel
import com.chatchatabc.parkingclient.android.viewmodel.MainUIState
import com.chatchatabc.parkingclient.android.viewmodel.ParkingUIState
import com.chatchatabc.parkingclient.android.viewmodel.ParkingViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberMarkerState
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.CoordinateBounds
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.style.expressions.generated.Expression
import com.mapbox.maps.extension.style.image.image
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.layers.generated.symbolLayer
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.extension.style.layers.properties.generated.SymbolPlacement
import com.mapbox.maps.extension.style.layers.properties.generated.TextAnchor
import com.mapbox.maps.extension.style.layers.properties.generated.TextJustify
import com.mapbox.maps.extension.style.layers.properties.generated.TextPitchAlignment
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.sources.getSourceAs
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.compass.compass
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.addOnMoveListener
import com.mapbox.maps.plugin.scalebar.scalebar
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.context.loadKoinModules
import timber.log.Timber

class MainActivity : LocationActivity() {
    val koinModule = loadKoinModules(listOf(MainMapModule))

    val viewModel: ClientMainViewModel by inject()

    override fun onResume() {
        super.onResume()

        viewModel.viewModelScope.launch {
            viewModel.startListening()
            viewModel.parkingViewModel.getAllVehicles()
            viewModel.parkingViewModel.syncParkingLots()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.startListening()

        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = TRANSPARENT

        setContent {
            val paddings = WindowInsets.statusBars

            val uiState by viewModel.state.collectAsState()

            LaunchedEffect(uiState) {
                Timber.d("UI State: $uiState")
            }

            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        contentWindowInsets = paddings,
                        floatingActionButton = {
                            AnimatedVisibility(uiState == MainUIState.PARKING) {
                                FloatingActionButton(
                                    modifier = Modifier
                                        .padding(32.dp),
                                    onClick = {
                                        viewModel.parkingViewModel.openVehicleSelector()
                                    }
                                ) {
                                    Icon(Icons.Filled.QrCode, "Show Parking QR Code")
                                }
                            }
                        },
                        bottomBar = {
                            NavigationBar {
                                NavigationBarItem(
                                    selected = uiState == MainUIState.PARKING,
                                    onClick = {
                                        // set current page to 0
                                        viewModel.setPage(MainUIState.PARKING)
                                    },
                                    icon = { Icon(Icons.Filled.Map, "Parking") },
                                    label = { Text("Parking") }
                                )
                                NavigationBarItem(
                                    selected = uiState == MainUIState.JEEPNEY,
                                    onClick = {
                                        // set current page to 1
                                        viewModel.setPage(MainUIState.JEEPNEY)
                                    },
                                    icon = { Icon(Icons.Outlined.DirectionsCar, "Jeepney") },
                                    label = { Text("Jeepney") }
                                )

                                NavigationBarItem(
                                    selected = uiState == MainUIState.REPORT,
                                    onClick = {
                                        // set current page to 2
                                        viewModel.setPage(MainUIState.REPORT)
                                    },
                                    icon = { Icon(Icons.Outlined.Report, "Report") },
                                    label = { Text("Report") }
                                )

                                NavigationBarItem(
                                    selected = uiState == MainUIState.ACCOUNT,
                                    onClick = {
                                        // set current page to 3
                                        viewModel.setPage(MainUIState.ACCOUNT)
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

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            var mapUpdateKey by remember { mutableStateOf(0) }
                            val visibleParkingLots by viewModel.parkingViewModel.visibleParkingLots.collectAsState()

                            val selectedRoute by viewModel.jeepneyViewModel.selectedRoute.collectAsState()
                            val selectedRouteNodesAndEdges by viewModel.jeepneyViewModel.selectedRouteNodesAndEdges.collectAsState()
                            val jeepneys by viewModel.jeepneyViewModel.jeepneys.collectAsState(initial = listOf())

                            LaunchedEffect(jeepneys) {
                                Timber.d("Jeepneys got updated!")
                                jeepneys.forEach {
                                    Timber.d("${it.jeepneyUuid}, ${it.name} ${it.plateNumber}")
                                }
                            }

                            val parkingAnnotationManagers = remember { mutableMapOf<String, PointAnnotationManager>() }
                            val jeepneyAnnotationManagers = remember { mutableMapOf<String, PointAnnotationManager>() }
                            val jeepneyRouteAnnotationManagers = remember { mutableMapOf<String, PolylineAnnotationManager>() }

                            LaunchedEffect(jeepneys) {
                                println("Checking if updatekey is updated when jeepney changes.")
                                mapUpdateKey += 1
                            }

                            LaunchedEffect(visibleParkingLots) { mapUpdateKey++ }

                            MapboxMap(mapUpdateKey = mapUpdateKey,
                                onUpdate = {
                                    println("Starting update event loop!")
                                    val mapView = this
                                    val annotationApi = mapView.annotations

                                    this.getMapboxMap().let {
                                        if (uiState != MainUIState.PARKING) {
                                            it.getStyle { style ->
                                                val newFeatureCollection = FeatureCollection.fromFeatures(listOf())
                                                val source = style.getSourceAs<GeoJsonSource>("parkinglots")
                                                source?.featureCollection(newFeatureCollection)
                                            }
                                        }
                                        if (uiState != MainUIState.JEEPNEY) {
                                            if (jeepneyAnnotationManagers.isNotEmpty()) {
                                                jeepneyAnnotationManagers.forEach { (key, value) ->
                                                    annotationApi.removeAnnotationManager(value)
                                                }
                                                jeepneyAnnotationManagers.clear()
                                            }
                                            if (jeepneyRouteAnnotationManagers.isNotEmpty()) {
                                                jeepneyRouteAnnotationManagers.forEach { (key, value) ->
                                                    annotationApi.removeAnnotationManager(value)
                                                }
                                                jeepneyRouteAnnotationManagers.clear()
                                            }
                                        }

                                        if (uiState == MainUIState.PARKING) {
                                            it.getStyle { style ->
                                                val newFeatureCollection = FeatureCollection.fromFeatures(visibleParkingLots.response?.map {
                                                    Feature.fromGeometry(
                                                        Point.fromLngLat(it.longitude!!, it.latitude!!)
                                                    ).apply {
                                                        addStringProperty("name", it.name)
                                                        addStringProperty("parkingLotUuid", it.parkingLotUuid)
                                                    }
                                                } ?: listOf())
                                                val source = style.getSourceAs<GeoJsonSource>("parkinglots")
                                                source?.featureCollection(newFeatureCollection)
                                            }
                                        } else if (uiState == MainUIState.JEEPNEY) {
                                            selectedRoute.response?.let { route ->
                                                it.getStyle {style ->
                                                    val path = selectedRouteNodesAndEdges.response?.buildPath()?.map {
                                                        Point.fromLngLat(it.longitude, it.latitude)
                                                    } ?: listOf()
                                                    val newFeatureCollection = FeatureCollection.fromFeature(
                                                        Feature.fromGeometry(LineString.fromLngLats(path)).apply {
                                                            addStringProperty("name", route.name)
                                                        }
                                                    )
                                                    val source = style.getSourceAs<GeoJsonSource>("route")
                                                    source?.featureCollection(newFeatureCollection)

                                                    if (path.isNotEmpty()) {
                                                        val padding = EdgeInsets(200.0, 200.0, 200.0, 200.0)

                                                        camera.flyTo(
                                                            getMapboxMap().cameraForCoordinates(path, padding),
                                                            MapAnimationOptions.mapAnimationOptions {
                                                                duration(2000L)
                                                            }
                                                        )
                                                    }
                                                }

//                                                jeepneyRouteAnnotationManagers.forEach {
//                                                    if (it.key != route.routeUuid) {
//                                                        annotationApi.removeAnnotationManager(it.value)
//                                                    }
//                                                }
//
//                                                if (jeepneyAnnotationManagers.isEmpty()) {
//                                                    selectedRouteNodesAndEdges.response?.let { nodes ->
//                                                        val path = nodes.buildPath().map {
//                                                            Point.fromLngLat(
//                                                                it.longitude,
//                                                                it.latitude
//                                                            )
//                                                        }
//
//                                                        val polylineAnnotationManager =
//                                                            annotationApi.createPolylineAnnotationManager()
//                                                        val polylineAnnotationOptions =
//                                                            PolylineAnnotationOptions()
//                                                                .withPoints(path)
//                                                                .withLineColor("#FF0000")
//                                                                .withLineWidth(5.0)
//                                                        polylineAnnotationManager.create(
//                                                            polylineAnnotationOptions
//                                                        )
//                                                        jeepneyRouteAnnotationManagers[route.routeUuid] =
//                                                            polylineAnnotationManager
//
//                                                        // Get the CameraAnimationsPlugin
//                                                        val camera = mapView.camera
//
//                                                        // Define the padding
//
//                                                    }
//                                                }
                                            }

                                            jeepneys.forEach { jeepney ->
                                                println("UPDATING JEEPNEYS!!!!")
                                                // Remove old annotation manager if it exists
                                                if (jeepney.jeepneyUuid in jeepneyAnnotationManagers.keys) {
                                                    annotationApi.removeAnnotationManager(
                                                        jeepneyAnnotationManagers[jeepney.jeepneyUuid]!!
                                                    )
                                                }
                                                // Create a new annotation manager and annotation
                                                val pointAnnotationManager = annotationApi.createPointAnnotationManager()
                                                println("Setting the pointAnnotationOptions for ${jeepney.name}")
                                                println("Updating jeep with lat: ${jeepney.latitude} and long: ${jeepney.longitude}")
                                                val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                                                    // Define a geographic coordinate.
                                                    .withPoint(Point.fromLngLat(jeepney.longitude, jeepney.latitude))
                                                    // Specify the bitmap you assigned to the point annotation
                                                    // The bitmap will be added to map style automatically.
                                                    .withIconImage(context.resources.getDrawable(R.drawable.jeep_pin).toBitmap())
                                                    .withIconSize(0.5)
                                                    .withIconRotate(jeepney.direction.toDouble())
                                                    .withTextField(jeepney.name ?: "Name Here!")
                                                    .withTextOffset(listOf(1.0, 0.0))
                                                    .withTextSize(15.0)
                                                    .withTextJustify(TextJustify.LEFT)
                                                    .withTextLineHeight(0.8)
                                                    .withTextAnchor(TextAnchor.LEFT)
                                                    .withTextMaxWidth(10.0)
                                                pointAnnotationManager.create(pointAnnotationOptions)

                                                jeepneyAnnotationManagers[jeepney.jeepneyUuid] = pointAnnotationManager
                                            }

                                        }
                                    }
                                }, onMapMoved = {
                                    if (uiState == MainUIState.PARKING) {
                                        viewModel.parkingViewModel.getParkingLotsInRange(it)
                                    }
                                })
                        }

                        LaunchedEffect(uiState) {
                            // Subscribe and unsubscribe, depending on the current page
                            if (uiState == MainUIState.PARKING) viewModel.parkingViewModel.subscribe()
                            else viewModel.parkingViewModel.unsubscribe()

                            if (uiState != MainUIState.JEEPNEY) {
                                viewModel.jeepneyViewModel.unsubscribe()
                            }
                        }

                        Box(
                            Modifier
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.White,
                                            Color.Transparent
                                        )
                                    )
                                )
                                .fillMaxWidth()
                                .height(200.dp)
                        )

                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(top = padding.calculateTopPadding())) {

                            AnimatedVisibility(uiState == MainUIState.PARKING) {
                                ParkingOverlay(
                                    viewModel = viewModel.parkingViewModel,
                                    onAddVehicle = {
                                        startActivity(
                                            Intent(
                                                this@MainActivity,
                                                NewVehicleActivity::class.java
                                            )
                                        )
                                    }
                                )
                            }

                            AnimatedVisibility(uiState == MainUIState.JEEPNEY,
                                enter = fadeIn(
                                    animationSpec = tween(400)
                                ) + slideInVertically(
                                    initialOffsetY = { fullHeight -> -fullHeight},
                                    animationSpec = tween(400)
                                ),
                                exit = fadeOut(
                                    animationSpec = tween(400)
                                ) + slideOutVertically(
                                    targetOffsetY = { fullHeight -> -fullHeight},
                                    animationSpec = tween(400)
                                )
                            ) {
                                JeepneyOverlay(viewModel = viewModel.jeepneyViewModel)
                            }

                            AnimatedVisibility(uiState == MainUIState.ACCOUNT) {
                                AccountComposable {
                                    viewModel.clearAuthToken()
                                    startActivity(
                                        Intent(this@MainActivity, LoginActivity::class.java).apply {
                                            flags =
                                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                        }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkingOverlay(
    viewModel: ParkingViewModel,
    onAddVehicle: () -> Unit
) {
    val uiState by viewModel.parkingState.collectAsState()
    var textValue by remember { mutableStateOf("") }
    Column(
        // Primary background color
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Search Bar
        SearchBarComposable(
            textValue = textValue,
            onValueChange = { newValue ->
                textValue = newValue
            }
        )
//        // Parking Lot Highlights
//        ParkingLotHighlightComposable()
    }

    val vehiclesState by viewModel.vehicles.collectAsState()

    if (uiState == ParkingUIState.QR_VIEWER_VEHICLE_SELECT) {
        SelectVehicleSheet(
            state = vehiclesState,
            onDismiss = {
                viewModel.parkingState.value =
                    if (it) ParkingUIState.QR_VIEWER else ParkingUIState.DEFAULT
            },
            onVehicleSelected = {
                viewModel.selectedVehicle.value = it
                viewModel.createQRFromString(it.vehicleUuid)
                viewModel.parkingState.value = ParkingUIState.QR_VIEWER
            },
            onAddVehicleClicked = {
                onAddVehicle()
            },
            onVehicleRefresh = {
                viewModel.getAllVehicles()
            }
        )
    }

    val selectedVehicle by viewModel.selectedVehicle.collectAsState()
    val qrCode by viewModel.qrCodeState.collectAsState()

    if (uiState == ParkingUIState.QR_VIEWER || uiState == ParkingUIState.PARKED_WITH_QR_VIEWER || uiState == ParkingUIState.LEFT_WITH_QR_VIEWER) {
        AlertDialog(
            onDismissRequest = {
                viewModel.parkingState.value = ParkingUIState.DEFAULT
            }, properties = DialogProperties(decorFitsSystemWindows = false)
        ) {
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
                    Box(
                        Modifier
                            .padding(32.dp)
                            .fillMaxSize()
                    ) {
                        if (uiState == ParkingUIState.PARKED_WITH_QR_VIEWER) {
                            val parkedLot by viewModel.parkedLot.collectAsState()

                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.align(Alignment.Center)
                            ) {
                                Icon(Icons.Filled.AccessTime, null)
                                Text(
                                    text = "Successfully Parked!",
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                parkedLot?.let {
                                    Text(
                                        text = "Your parking in ${it.name} has been successfully confirmed!.",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Button(
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = {
                                        /*TODO*/
                                    }) {
                                    Text("View Details")
                                }
                            }
                        } else if (uiState == ParkingUIState.LEFT_WITH_QR_VIEWER) {
                            val parkedLot by viewModel.parkedLot.collectAsState()
                            val invoice by viewModel.currentInvoice.collectAsState()

                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.align(Alignment.Center)
                            ) {
                                Icon(Icons.Filled.AccessTime, null)
                                Text(
                                    text = "Left Parking Lot!",
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                parkedLot?.let {
                                    Text(
                                        text = "You have successfully left in ${it.name} with a total cost of PHP ${invoice!!.total}!.",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    invoice?.paidAt?.let {
                                        Text(
                                            text = "This session has been marked as paid.",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                                Button(
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = {
                                        /*TODO*/
                                    }) {
                                    Text("View Details")
                                }
                            }
                        } else {
                            if (qrCode.isLoading) {
                                CircularProgressIndicator(
                                    Modifier.align(
                                        Alignment.Center
                                    )
                                )
                            } else {
                                qrCode.response?.let {
                                    Image(
                                        it.asImageBitmap(),
                                        contentDescription = "Parking QR Code",
                                        modifier = Modifier.align(
                                            Alignment.Center
                                        )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountComposable(onLogout: () -> Unit) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var logoutPopupOpened by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.primary,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.Center)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.Center)
                    .background(MaterialTheme.colorScheme.primary),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),

                )
        }
    ) {

        // Logout confirmation alert dialog
        if (logoutPopupOpened) {
            AlertDialog(onDismissRequest = {
                logoutPopupOpened = false
            }) {
                (LocalView.current.parent as DialogWindowProvider).window.setDimAmount(
                    0.50f
                )

                Box(
                    Modifier
                        .clip(RoundedCornerShape(32.dp))
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(
                            16.dp
                        ),
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(
                            "Are you sure you want to logout? We will miss you!",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(
                                8.dp
                            )
                        ) {
                            Button(
                                colors = ButtonDefaults.filledTonalButtonColors(),
                                onClick = {
                                    logoutPopupOpened = false
                                }
                            ) {
                                Text("No")
                            }
                            Button(
                                colors = ButtonDefaults.filledTonalButtonColors(),
                                onClick = {
                                    onLogout()
                                }
                            ) {
                                Text("Yes")
                            }
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(it),
            verticalArrangement = Arrangement.Center
        ) {
            // Profile Photo
            GenericMenuItemComposable("Profile Photo", content = {
                // TODO: Replace with User Profile Picture
                MenuSubtextComposable(label = "\"Photo\"")
            }, onClick = {
                // TODO: Add functionality
                println("Profile Photo Clicked")
            })

            // My Coupons
            GenericMenuItemComposable("My Coupons", content = {
                MenuSubtextComposable(label = "")
            }, onClick = {
                // TODO: Add functionality
                println("Coupons Clicked")
            })

            // First Name
            GenericMenuItemComposable("First Name", content = {
                MenuSubtextComposable(label = "")
            }, onClick = {
                // TODO: Add functionality
                println("First name Clicked")
            })

            // Last Name
            GenericMenuItemComposable("Last Name", content = {
                MenuSubtextComposable(label = "")
            }, onClick = {

            })

            // Phone
            GenericMenuItemComposable("Phone", content = {
                MenuSubtextComposable(label = "")
            }, onClick = {

            })

            // Email
            GenericMenuItemComposable("Email", content = {
                MenuSubtextComposable(label = "")
            }, onClick = {

            })

            // Language
            GenericMenuItemComposable("Language", content = {
                MenuSubtextComposable(label = "")
            }, onClick = {

            })

            // Feedback
            GenericMenuItemComposable("Feedback", content = {
                MenuSubtextComposable(label = "")
            }, onClick = {

            })

            // Create Logout Button that is centered and width full
            Button(
                onClick = {
                    logoutPopupOpened = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                colors = ButtonDefaults.filledTonalButtonColors(),
                shape = RectangleShape
            ) {
                Text(
                    "Logout",
                    // Red font color
                    color = Color.Red,
                    // Biggish font size
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JeepneyOverlay(viewModel: JeepneyViewModel) {
    val isRouteSelectorOpened by viewModel.isRouteSelectorOpened.collectAsState(false)
    val selectedRoute by viewModel.selectedRoute.collectAsState()

    LaunchedEffect(true) {
        viewModel.syncRoutes()
    }

    Column(Modifier.padding(16.dp)) {
        Row(
            Modifier.height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                Modifier
                    .shadow(
                        16.dp,
                        CircleShape,
                        false,
                        ambientColor = MaterialTheme.colorScheme.surfaceVariant,
                        spotColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .weight(1f)
                    .fillMaxSize()
                    .clickable {
                        viewModel.isRouteSelectorOpened.value = true
                    },
                contentAlignment = Alignment.CenterStart
            ) {
                if (selectedRoute.response == null) {
                    Text(
                        "Search",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    Text(
                        selectedRoute.response!!.name!!,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            Box(
                Modifier
                    .clip(CircleShape)
                    .shadow(
                        16.dp,
                        CircleShape,
                        false,
                        ambientColor = MaterialTheme.colorScheme.surfaceVariant,
                        spotColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                    )
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .aspectRatio(1f)
                    .fillMaxSize()
                    .clickable {},
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.NotificationsNone,
                    null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }

    val routes by viewModel.routes.collectAsState()

    if (isRouteSelectorOpened) {
        LaunchedEffect(Unit) {
            viewModel.getAllRoutesFromDB()
        }

        ModalBottomSheet(
            onDismissRequest = {
                viewModel.isRouteSelectorOpened.value = false
            }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Row {
                        Text("Select Route", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.weight(1f))
                        FilledTonalIconButton(onClick = {
                            // TODO: Refresh Routes
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
                }
                if (routes.isEmpty()) {
                    item {
                        Card(Modifier.fillMaxWidth()) {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Finding your vehicles...")
                                CircularProgressIndicator()
                            }
                        }
                    }
                } else {
                    items(routes, key = { item -> item.routeUuid }) { route ->
                        GenericBottomSheetItem(
                            title = route.name ?: "",
                        ) {
                            viewModel.setRoute(route)
                            viewModel.isRouteSelectorOpened.value = false
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GenericBottomSheetItem(
    title: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
        }
    }
}

fun Location.toLatLng() = LatLng(latitude, longitude)

@Composable
@GoogleMapComposable
fun JeepneyRenderer(jeepneys: List<Jeepney>) {
    println("RERENDERING JEEPNEYS")
    jeepneys.forEach {
        val markerState =
            rememberMarkerState(key = it.jeepneyUuid, LatLng(it.latitude, it.longitude))

        markerState.also { state ->
            println("MARKER ${it.jeepneyUuid} ${state.position}")
        }

        println("JEEPNEY: ${it.jeepneyUuid} ${it.latitude}, ${it.longitude}")
        Marker(state = markerState, onClick = { marker ->
            println("JEEPNEY CLICKED ${it.jeepneyUuid}")
            true
        })
    }
}

@Composable
fun MapboxMap(
    mapUpdateKey: Int,
    onUpdate: MapView.() -> Unit,
    onMapMoved: (CoordinateBounds) -> Unit
) {
    val lineColor = MaterialTheme.colorScheme.primary.toArgb()
    val context = LocalContext.current
    val mapView = MapView(context)

    AndroidView(factory = {
        mapView.apply {
            scalebar.updateSettings { enabled = false }
            compass.updateSettings { enabled = false }

            val featureCollection = FeatureCollection.fromFeatures(listOf<Feature>())

            this.getMapboxMap().loadStyle(
                style(styleUri = "mapbox://styles/mikearnado123/cljdty93i000q01pre4wh7xd4") {
                    +image("parking-lot-icon") {
                        bitmap(BitmapFactory.decodeResource(resources, R.drawable.pin_png))
                    }
                    +geoJsonSource("parkinglots") {
                        featureCollection(featureCollection)
                    }

                    +symbolLayer("parkinglots", "parkinglots") {
                        textField(Expression.get("name"))
                        textSize(13.0)
                        iconAllowOverlap(true)
                        textOptional(true)
                        textAllowOverlap(false)
                        iconSize(0.75)
                        iconImage("parking-lot-icon")
                        textRadialOffset(1.0)
                        textVariableAnchor(listOf(
                            "left","right"
                        ))
                    }
                    +geoJsonSource("route") {
                        featureCollection(featureCollection)
                    }
                    +lineLayer("route", "route") {
                        lineCap(LineCap.BUTT)
                        lineJoin(LineJoin.ROUND)
                        lineWidth(20.0)
                        lineBlur(2.0)
                        lineColor(lineColor)
//                        symbolPlacement(SymbolPlacement.LINE_CENTER)
//                        textField(Expression.get("name"))
//                        textSize(13.0)
//                        textKeepUpright(true)
                    }
                    +symbolLayer("route-name", "route") {
                        textField(Expression.get("name")) // Replace "name" with the name of your property
                        symbolPlacement(SymbolPlacement.LINE_CENTER)
                        textColor("#FFFFFF")
                        textKeepUpright(true)
                        textAllowOverlap(true)
                        minZoom(0.0)
                        textPadding(1.0)
                        textPitchAlignment(TextPitchAlignment.VIEWPORT)
                    }
                }

            )

            getMapboxMap().apply {
                addOnMoveListener(object : OnMoveListener {
                    override fun onMoveBegin(detector: MoveGestureDetector) {}

                    override fun onMove(detector: MoveGestureDetector) = false

                    override fun onMoveEnd(detector: MoveGestureDetector) {
                        onMapMoved(mapView.getMapboxMap().getBounds().bounds)
                    }
                })
                setCamera(
                    CameraOptions.Builder().zoom(15.0)
                        .center(Point.fromLngLat(125.635272, 7.110451)).build()
                )
            }
        }
    }, update = {
        println("UPDATING MAP WITH UPDATEINDEX: $mapUpdateKey")
        it.onUpdate()
    })
}
