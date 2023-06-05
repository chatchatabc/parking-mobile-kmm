package com.chatchatabc.parkingadmin.android.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.chatchatabc.parking.viewModel.ImageUpload
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageItemComposable(
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