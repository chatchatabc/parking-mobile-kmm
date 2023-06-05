package com.chatchatabc.parkingclient.android

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindowProvider
import com.chatchatabc.parking.compose.Theme.AppTheme
import com.chatchatabc.parking.di.AccountModule
import com.chatchatabc.parking.viewModel.AccountViewModel
import org.koin.android.ext.android.inject
import org.koin.core.context.loadKoinModules

class AccountActivity : ComponentActivity() {
    val koinModule = loadKoinModules(AccountModule)
    val viewModel: AccountViewModel by inject()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
                val logoutPopupOpened by viewModel.logoutPopupOpened.collectAsState()

                Scaffold(
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                    containerColor = MaterialTheme.colorScheme.primary,
                    topBar = {
                        TopAppBar(
                            title = {
                                Text("Profile")
                            },
                            navigationIcon = {
                                IconButton(onClick = {
                                    startActivity(
                                        Intent(
                                            this@AccountActivity,
                                            MainActivity::class.java
                                        )
                                    )
                                }) {
                                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                                }
                            }
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
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier.padding(32.dp)
                                ) {
                                    Text(
                                        "Are you sure you want to logout? We will miss you!",
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Row(
                                        Modifier
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            colors = ButtonDefaults.filledTonalButtonColors(),
                                            onClick = {
                                                viewModel.logoutPopupOpened.value = false
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
                                                        this@AccountActivity,
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
                        Button(
                            onClick = {
                                // TODO:
                            },
                            modifier = Modifier
                                .fillMaxWidth(),
                            colors = ButtonDefaults.filledTonalButtonColors(),
                            shape = RectangleShape
                        ) {
                            Text("Profile Photo")
                        }

                        // My Coupons
                        Button(
                            onClick = {
                                // TODO:
                            },
                            modifier = Modifier
                                .fillMaxWidth(),
                            colors = ButtonDefaults.filledTonalButtonColors(),
                            shape = RectangleShape
                        ) {
                            Text("My Coupons")
                        }

                        // First Name
                        Button(
                            onClick = {
                                // TODO:
                            },
                            modifier = Modifier
                                .fillMaxWidth(),
                            colors = ButtonDefaults.filledTonalButtonColors(),
                            shape = RectangleShape
                        ) {
                            Text("First Name")
                        }

                        // Last Name
                        Button(
                            onClick = {
                                // TODO:
                            },
                            modifier = Modifier
                                .fillMaxWidth(),
                            colors = ButtonDefaults.filledTonalButtonColors(),
                            shape = RectangleShape
                        ) {
                            Text("Last Name")
                        }

                        // Phone
                        Button(
                            onClick = {
                                // TODO:
                            },
                            modifier = Modifier
                                .fillMaxWidth(),
                            colors = ButtonDefaults.filledTonalButtonColors(),
                            shape = RectangleShape
                        ) {
                            Text("Phone")
                        }

                        // Email
                        Button(
                            onClick = {
                                // TODO:
                            },
                            modifier = Modifier
                                .fillMaxWidth(),
                            colors = ButtonDefaults.filledTonalButtonColors(),
                            shape = RectangleShape
                        ) {
                            Text("Email")
                        }

                        // Language
                        Button(
                            onClick = {
                                // TODO:
                            },
                            modifier = Modifier
                                .fillMaxWidth(),
                            colors = ButtonDefaults.filledTonalButtonColors(),
                            shape = RectangleShape
                        ) {
                            Text("Language")
                        }

                        // Feedback
                        Button(
                            onClick = {
                                // TODO:
                            },
                            modifier = Modifier
                                .fillMaxWidth(),
                            colors = ButtonDefaults.filledTonalButtonColors(),
                            shape = RectangleShape
                        ) {
                            Text("Feedback")
                        }

                        // Create Logout Button that is centered and width full
                        Button(
                            onClick = {
                                viewModel.logoutPopupOpened.value = true
                            },
                            modifier = Modifier
                                .fillMaxWidth(),
                            colors = ButtonDefaults.filledTonalButtonColors(),
                            shape = RectangleShape
                        ) {
                            Text("Logout")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GenericMenuItem(
    label: String,
    content: @Composable () -> Unit,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(label)
            Spacer(Modifier.weight(1f))
            content()
            if (onClick != null) {
                IconButton(onClick = onClick) {
                    Icon(Icons.Filled.ChevronRight, contentDescription = "Next")
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun PreviewGenericMenuItem() {
    GenericMenuItem("Test", content = {
        Text("Test")
    }, onClick = {

    })
}