package com.chatchatabc.parkingadmin.android

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chatchatabc.parking.compose.Theme.AppTheme
import com.chatchatabc.parking.di.MainModule
import com.chatchatabc.parking.model.ParkingLot
import com.chatchatabc.parking.viewModel.MainViewModel
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
                                        Modifier.size(24.dp)
                                    )
                                }
                                IconButton(onClick = { /* TODO */ }) {
                                    Icon(
                                        Icons.Filled.NotificationsNone,
                                        contentDescription = "Notifications",
                                        Modifier.size(24.dp)
                                    )
                                }
                                // TODO: Clicking Account Icon Temporarily Logs User out. Replace with actual button
                                IconButton(onClick = {
                                    viewModel.clearAuthToken()
                                    startActivity(
                                        Intent(
                                            this@MainActivity,
                                            LoginActivity::class.java
                                        ).apply {
                                            flags =
                                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                        })
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
                            DashboardView()
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun DashboardView() {
    // TODO: Use better colors. This does not look good at the moment.
    // TODO: Connect to actual data source.
    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(16.dp)) {
        Card(
            Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Box(Modifier.fillMaxSize()) {
                Column(Modifier.padding(16.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier
                                .clip(CircleShape)
                                .size(8.dp)
                                .background(Color.Green)
                        )
                        Text("AVAILABLE", style = MaterialTheme.typography.labelSmall)
                    }
                    Text(
                        "200", style = MaterialTheme.typography.displayLarge.copy(
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
                                    Modifier
                                        .clip(CircleShape)
                                        .size(8.dp)
                                        .background(Color.Yellow)
                                )
                                Text("LEAVING SOON", style = MaterialTheme.typography.labelSmall)
                            }
                            Text(
                                "17", style = MaterialTheme.typography.displaySmall.copy(
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
                                    Modifier
                                        .clip(CircleShape)
                                        .size(8.dp)
                                        .background(Color.Gray)
                                )
                                Text("OCCUPIED", style = MaterialTheme.typography.labelSmall)
                            }
                            Text(
                                "139", style = MaterialTheme.typography.displaySmall.copy(
                                    platformStyle = PlatformTextStyle(
                                        includeFontPadding = false
                                    ),
                                )
                            )
                        }
                    }
                }
                Column(
                    Modifier
                        .width(82.dp)
                        .padding(16.dp)
                        .align(Alignment.TopEnd),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        Modifier
                            .weight(200f)
                            .clip(RoundedCornerShape(3.dp))
                            .fillMaxWidth()
                            .background(Color.Green)
                    )
                    Box(
                        Modifier
                            .weight(17f)
                            .clip(RoundedCornerShape(3.dp))
                            .fillMaxWidth()
                            .background(Color.Yellow)
                    )
                    Box(
                        Modifier
                            .weight(139f)
                            .clip(RoundedCornerShape(3.dp))
                            .fillMaxWidth()
                            .background(Color.Gray)
                    )
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                        "332", style = MaterialTheme.typography.displaySmall.copy(
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
                        "₱2.5k", style = MaterialTheme.typography.displaySmall.copy(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            ),
                        )
                    )
                }
            }
        }
        OverrideCapacityCard()
        VehicleSearchButton()
    }
}


// TODO: Add functionality.
@Composable
fun OverrideCapacityCard() {
    Card {
        Row(
            Modifier
                .height(IntrinsicSize.Min)
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .background(Color.Red)
                    .fillMaxHeight()
                    .width(96.dp)
            ) {
                Icon(
                    Icons.Filled.Remove, null,
                    Modifier
                        .size(32.dp)
                        .align(Alignment.Center)
                )
            }
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("CAPACITY", style = MaterialTheme.typography.labelSmall)
                Text(
                    "200", style = MaterialTheme.typography.displaySmall.copy(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        ),
                    )
                )
            }
            Box(
                Modifier
                    .background(Color.Green)
                    .fillMaxHeight()
                    .width(96.dp)
            ) {
                Icon(
                    Icons.Filled.Remove, null,
                    Modifier
                        .size(32.dp)
                        .align(Alignment.Center)
                )
            }
        }
    }
}

// TODO: Add functionality.
@Composable
fun VehicleSearchButton() {
    Card(
        Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Row(
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("VEHICLE SEARCH", style = MaterialTheme.typography.labelSmall)
            Icon(Icons.Filled.Search, null, Modifier.size(24.dp))
        }
    }
}

