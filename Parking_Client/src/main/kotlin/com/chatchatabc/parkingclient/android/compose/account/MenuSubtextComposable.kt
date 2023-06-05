package com.chatchatabc.parkingclient.android.compose.account

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun MenuSubtextComposable(
    label: String
) {
    Text(
        text = label,
        // Black font color
        color = MaterialTheme.colorScheme.onSurface,
        // Set overflow to Ellipsis
        overflow = TextOverflow.Ellipsis,
        // Specify the maximum number of lines
        maxLines = 1,
        // Add padding left
        modifier = Modifier.padding(start = 24.dp),
    )
}