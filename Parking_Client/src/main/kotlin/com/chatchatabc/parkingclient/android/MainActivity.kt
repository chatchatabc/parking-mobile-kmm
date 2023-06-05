package com.chatchatabc.parkingclient.android

import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindowProvider
import com.chatchatabc.parking.activity.LocationActivity
import com.chatchatabc.parking.compose.Theme.AppTheme
import com.chatchatabc.parking.di.MainMapModule
import com.chatchatabc.parking.di.ParkingRealmModule
import com.chatchatabc.parking.viewModel.ClientMainViewModel
import com.chatchatabc.parkingclient.android.compose.account.GenericMenuItemComposable
import com.chatchatabc.parkingclient.android.compose.account.MenuSubtextComposable
import com.chatchatabc.parkingclient.android.compose.main.MapViewComposable
import com.chatchatabc.parkingclient.android.compose.main.SearchBarComposable
import com.chatchatabc.parkingclient.android.compose.vehicle.SelectVehicleSheet
import com.google.android.gms.maps.model.LatLng
import org.koin.android.ext.android.inject
import org.koin.core.context.loadKoinModules


class MainActivity : LocationActivity() {
    val koinModule = loadKoinModules(listOf(ParkingRealmModule, MainMapModule))
    val viewModel: ClientMainViewModel by inject()

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
                    val logoutPopupOpened by viewModel.logoutPopupOpened.collectAsState()
                    val visibleParkingLots by viewModel.visibleParkingLots.collectAsState(listOf())
                    val currentPage by viewModel.currentPage.collectAsState()

                    Scaffold(
                        floatingActionButton = {
                            if (currentPage == 0) {
                                FloatingActionButton(
                                    modifier = Modifier
                                        .padding(32.dp),
                                    onClick = {
                                        viewModel.openVehicleSelector()
                                    }
                                ) {
                                    Icon(Icons.Filled.QrCode, "Show Parking QR Code")
                                }
                            }
                        },
                        bottomBar = {
                            NavigationBar {
                                NavigationBarItem(
                                    selected = currentPage == 0,
                                    onClick = {
                                        // set current page to 0
                                        viewModel.setCurrentPage(0)
                                    },
                                    icon = { Icon(Icons.Filled.Map, "Parking") },
                                    label = { Text("Parking") }
                                )
                                NavigationBarItem(
                                    selected = currentPage == 1,
                                    onClick = {
                                        // set current page to 1
                                        viewModel.setCurrentPage(1)
                                    },
                                    icon = { Icon(Icons.Outlined.DirectionsCar, "Jeepney") },
                                    label = { Text("Jeepney") }
                                )

                                NavigationBarItem(
                                    selected = currentPage == 2,
                                    onClick = {
                                        // set current page to 2
                                        viewModel.setCurrentPage(2)
                                    },
                                    icon = { Icon(Icons.Outlined.Report, "Report") },
                                    label = { Text("Report") }
                                )

                                NavigationBarItem(
                                    selected = currentPage == 3,
                                    onClick = {
                                        // set current page to 3
                                        viewModel.setCurrentPage(3)
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

                        val pagerState = rememberPagerState(
                            initialPage = 0
                        )

                        LaunchedEffect(currentPage) {
                            pagerState.animateScrollToPage(currentPage)
                        }

                        HorizontalPager(
                            state = pagerState,
                            userScrollEnabled = false,
                            pageCount = 4,
                            beyondBoundsPageCount = 4,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding)
                        ) { page ->
                            when (page) {
                                // Parking Page
                                0 -> {
                                    var textValue by remember { mutableStateOf("") }
                                    Column(
                                        // Primary background color
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(MaterialTheme.colorScheme.primary)
                                    ) {
                                        // Search Bar
                                        SearchBarComposable(
                                            textValue = textValue,
                                            onValueChange = { newValue ->
                                                textValue = newValue
                                            }
                                        )
                                        if (hasPermission) {
                                            MapViewComposable(
                                                pins = visibleParkingLots,
                                                modifier = Modifier
                                                    .padding(16.dp, 32.dp)
                                                    .clip(RoundedCornerShape(16.dp)),
                                                onMapLoaded = {
                                                    viewModel.syncParkingLots()
                                                },
                                            ) {
                                                viewModel.getParkingLotsInRange(it)
                                            }
                                        }
                                    }

                                    val vehicleSelectorShown by viewModel.isSelectingVehicle.collectAsState()

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
                                                Intent(
                                                    this@MainActivity,
                                                    NewVehicleActivity::class.java
                                                ).also {
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
                                                            CircularProgressIndicator(
                                                                Modifier.align(
                                                                    Alignment.Center
                                                                )
                                                            )
                                                        } else {
                                                            qrCode?.let {
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

                                // Jeepney Page
                                1 -> {
                                    // TODO: Implement Page
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .wrapContentSize(Alignment.Center)
                                    ) {
                                        Text(
                                            text = "Page under construction",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .wrapContentSize(Alignment.Center)
                                        )
                                    }
                                }

                                // Report Page
                                2 -> {
                                    // TODO: Implement Page
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .wrapContentSize(Alignment.Center)
                                    ) {
                                        Text(
                                            text = "Page under construction",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .wrapContentSize(Alignment.Center)
                                        )
                                    }
                                }

                                // Me page
                                3 -> {
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
                                                viewModel.logoutPopupOpened.value = false
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
                                                                    viewModel.logoutPopupOpened.value =
                                                                        false
                                                                }
                                                            ) {
                                                                Text("No")
                                                            }
                                                            Button(
                                                                colors = ButtonDefaults.filledTonalButtonColors(),
                                                                onClick = {
                                                                    viewModel.clearAuthToken()
                                                                    startActivity(
                                                                        Intent(
                                                                            this@MainActivity,
                                                                            LoginActivity::class.java
                                                                        ).apply {
                                                                            flags =
                                                                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                                                        })
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
                                                MenuSubtextComposable(label = "aaaaaaaaaaaaaaaaaaaaaaaabbbbbbbbb")
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
                                                    viewModel.logoutPopupOpened.value = true
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
                            }
                        }
                    }
                }
            }
        }
    }
}

fun Location.toLatLng() = LatLng(latitude, longitude)