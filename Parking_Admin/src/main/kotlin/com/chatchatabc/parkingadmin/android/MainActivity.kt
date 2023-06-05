package com.chatchatabc.parkingadmin.android

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.chatchatabc.parking.compose.Theme.AppTheme
import com.chatchatabc.parking.di.MainModule
import com.chatchatabc.parking.model.ParkingLot
import com.chatchatabc.parking.viewModel.MainViewModel
import com.chatchatabc.parkingadmin.android.compose.DashboardViewComposable
import org.koin.android.ext.android.inject
import org.koin.core.context.loadKoinModules

class MainActivity : ComponentActivity() {
    val koinModule = loadKoinModules(MainModule)

    val viewModel: MainViewModel by inject()

    override fun onResume() {
        super.onResume()

        viewModel.parkingLot.value.let { parkingLot ->
            if (parkingLot == null) {
                viewModel.getParkingLot()
            } else if (parkingLot.status != ParkingLot.VERIFIED) {
                viewModel.getParkingLot()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

                val parkingLot by viewModel.parkingLot.collectAsState()
                val isLoading by viewModel.isLoading.collectAsState()

                Scaffold(
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                    containerColor = MaterialTheme.colorScheme.primary,
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Transparent,
                                actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                                scrolledContainerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                    alpha = 1f
                                ),
                            ),
                            title = {
                                Text("Dashboard", style = MaterialTheme.typography.titleMedium)
                            },
                            actions = {
                                IconButton(onClick = { /* TODO*/ }) {
                                    Icon(
                                        Icons.Filled.Settings,
                                        contentDescription = "Notifications",
           x                             Modifier.size(24.dp)
                                    )
                                }
                                IconButton(onClick = { /* TODO */ }) {
                                    Icon(
                                        Icons.Filled.NotificationsNone,
                                        contentDescription = "Notifications",
                                        Modifier.size(24.dp)
                                    )
                                }

                                IconButton(onClick = {
                                    startActivity(
                                        Intent(
                                            this@MainActivity,
                                            AccountActivity::class.java
                                        )
                                    )
                                }) {
                                    Icon(
                                        Icons.Filled.AccountCircle,
                                        contentDescription = "Account",
                                        Modifier.size(32.dp)
                                    )
                                }
                            },
                            scrollBehavior = scrollBehavior
                        )
                    },
                    floatingActionButton = {
                        FloatingActionButton(onClick = {
                            startActivity(Intent(this@MainActivity, QRScanActivity::class.java))
                        }) {
                            Icon(
                                Icons.Filled.QrCodeScanner,
                                contentDescription = "Scan QR Code",
                                Modifier.size(24.dp)
                            )
                        }
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .padding(it),
                        verticalArrangement = Arrangement.Center
                    ) {
                        // TODO: Add logic for Dashboard View vs New Parking Lot Prompt
                        if (!isLoading) {
                            Box(
                                modifier = Modifier
                                    .padding(32.dp)
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        MaterialTheme.colorScheme.onSecondaryContainer.copy(
                                            alpha = 0.1f
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    if (parkingLot == null) {
                                        Text(
                                            "You have no parking lots set up",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Button(
                                            onClick = {
                                                startActivity(
                                                    Intent(
                                                        this@MainActivity,
                                                        NewParkingLotActivity::class.java
                                                    )
                                                )
                                            },
                                            colors = ButtonDefaults.elevatedButtonColors()
                                        ) {
                                            Text("Create a new parking lot")
                                        }
                                    } else {
                                        when (parkingLot!!.status) {
                                            ParkingLot.DRAFT -> {
                                                Text(
                                                    "You have a parking lot in draft",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                                Button(onClick = {
                                                    startActivity(
                                                        Intent(
                                                            this@MainActivity,
                                                            NewParkingLotActivity::class.java
                                                        ).apply {
                                                            parkingLot?.let {
                                                                this.putExtra(
                                                                    "parkingLot",
                                                                    it.parkingLotUuid
                                                                )
                                                            }
                                                        }
                                                    )
                                                }, colors = ButtonDefaults.elevatedButtonColors()) {
                                                    Text("Continue editing")
                                                }
                                            }

                                            ParkingLot.PENDING_VERIFICATION -> {
                                                Text(
                                                    "Your parking lot is pending verification",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                                Text(
                                                    "Check back later for updates",
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            DashboardViewComposable()
                        }
                    }
                }
            }
        }
    }
}