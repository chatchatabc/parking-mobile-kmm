package com.chatchatabc.parkingclient.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Surface
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chatchatabc.parking.compose.Theme.AppTheme
import com.chatchatabc.parking.di.NewVehicleModule
import com.chatchatabc.parking.viewModel.NewVehicleViewModel
import org.koin.android.ext.android.inject
import org.koin.core.context.loadKoinModules

class NewVehicleActivity: ComponentActivity() {
    val koinModule = loadKoinModules(NewVehicleModule)

    val viewModel: NewVehicleViewModel by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val page = viewModel.page.collectAsState()
            val name = viewModel.name.collectAsState()
            val platenumber = viewModel.platenumber.collectAsState()
            val type = viewModel.type.collectAsState()

            AppTheme {
                Surface {
                    // Use WizardView here, we might need additional pages in the future.
                    WizardLayout(
                        title = "Register a New Vehicle",
                        pages = 2,
                        page = 0,
                        onNext = {
                            viewModel.page.value += 1
                        },
                        onPrevious = {
                            viewModel.page.value -= 1
                        },
                        onSubmit = {
                            viewModel.validateAndSubmitVehicle()
                        },
                        onCancel = {

                        },
                        onSave = {

                        }
                    ) {

                    }
                }
            }
        }
    }
}

@Composable
fun NewVehicle(
    errors: Map<String, String>,
    name: String,
    platenumber: String,
) {

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WizardLayout(
    title: String,
    subtext: String? = null,
    pages: Int,
    page: Int,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    content: @Composable (page: Int) -> Unit
) {
    val pagerState = rememberPagerState(initialPage = 0)
    val progress by animateFloatAsState(targetValue = 1f / 4 * (page + 1))

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(32.dp, 16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.fillMaxWidth(0.75f)
            )
            // TODO: Add subtext
        }

        LaunchedEffect(page) {
            pagerState.animateScrollToPage(page)
        }

        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(),
            progress = progress
        )

        HorizontalPager(
            state = pagerState,
            pageCount = 4,
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .weight(1f)
                .background(MaterialTheme.colorScheme.background),
            beyondBoundsPageCount = 1, userScrollEnabled = false,
        ) { page ->
            content(page)
        }

//        val popupOpened by viewModel.popupOpened.collectAsState()

//        if (popupOpened) {
//            AlertDialog(onDismissRequest = {
//                viewModel.popupOpened.value = false
//            }) {
//                (LocalView.current.parent as DialogWindowProvider).window.setDimAmount(0.50f)
//
//                Box(
//                    Modifier
//                        .clip(RoundedCornerShape(32.dp))
//                        .fillMaxWidth()
//                        .background(MaterialTheme.colorScheme.surface),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Column(
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        verticalArrangement = Arrangement.spacedBy(16.dp),
//                        modifier = Modifier.padding(32.dp)
//                    ) {
//                        Text("Are you sure you want to cancel? Your changed will be saved.")
//                        Row(
//                            Modifier
//                                .fillMaxWidth(),
//                            horizontalArrangement = Arrangement.spacedBy(8.dp)
//                        ) {
//                            Button(
//                                colors = ButtonDefaults.filledTonalButtonColors(),
//                                onClick = {
//                                    viewModel.popupOpened.value = false
//                                }
//                            ) {
//                                Text("No")
//                            }
//                            Button(
//                                colors = ButtonDefaults.filledTonalButtonColors(),
//                                onClick = {
//                                    finish()
//                                }
//                            ) {
//                                Text("Yes")
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }
}