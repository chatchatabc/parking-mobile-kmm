package com.chatchatabc.parking.compose.wizard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindowProvider

enum class CancelState{
    NONE,
    PROMPT,
    CONFIRM
}
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WizardLayout(
    title: String,
    subtext: String? = null,
    pages: Int,
    page: Int,
    finishCTAText: String = "Finish",
    cancelPrompt: String = "Are you sure you want to cancel? This will discard all progress.",
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSubmit: () -> Unit,
    onFinish: () -> Unit,
    cancelState: CancelState,
    onCancelStateChanged: (CancelState) -> Unit,
    content: @Composable (page: Int) -> Unit
) {
    val pagerState = rememberPagerState(initialPage = 0)
    val progress by animateFloatAsState(targetValue = (1f / (pages - 1)) * (page + 1))



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
            Box {
                content(page)
            }
        }

        Row(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(32.dp, 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (pages != page) {
                OutlinedIconButton(onClick = {
                    onCancelStateChanged(CancelState.PROMPT)
                }, colors = IconButtonDefaults.outlinedIconButtonColors()) {
                    Icon(Icons.Filled.Close, contentDescription = "Cancel")
                }
            }

            if (page == 0 && pages != page) {
                Button(
                    colors = ButtonDefaults.filledTonalButtonColors(),
                    onClick = {
                        onPrevious()
                    },
                    enabled = true,
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text("Back")
                }
            }

            if (page < pages-1) {
                Button(
                    colors = ButtonDefaults.filledTonalButtonColors(),
                    onClick = {
                        onNext()
                    },
                    enabled = true,
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text("Next")
                }
            }

            if (page == pages-1) {
                Button(
                    colors = ButtonDefaults.filledTonalButtonColors(),
                    onClick = {
                        onSubmit()
                    },
                    enabled = true,
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text("Save")
                }
            }

            if (page == pages) {
                Button(
                    colors = ButtonDefaults.filledTonalButtonColors(),
                    onClick = {
                              onFinish()
                    },
                    enabled = true,
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(finishCTAText)
                }
            }
        }

        if (cancelState == CancelState.PROMPT) {
            AlertDialog(onDismissRequest = {
                onCancelStateChanged(CancelState.NONE)
            }) {
                (LocalView.current.parent as DialogWindowProvider).window.setDimAmount(0.50f)

                Box(
                    Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text("Are you sure you want to cancel? Your changed will be saved.")
                        Row(
                            Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                colors = ButtonDefaults.filledTonalButtonColors(),
                                onClick = {
                                    onCancelStateChanged(CancelState.NONE)
                                }
                            ) {
                                Text("No")
                            }
                            Button(
                                colors = ButtonDefaults.filledTonalButtonColors(),
                                onClick = {
                                    onCancelStateChanged(CancelState.CONFIRM)
                                }
                            ) {
                                Text("Yes")
                            }
                        }
                    }
                }
            }
        }
    }
}