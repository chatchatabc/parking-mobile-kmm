package com.chatchatabc.parkingclient.android.compose.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp

@Composable
fun SearchBarComposable(
    textValue: String,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
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
                .fillMaxWidth()
                .clickable {
                },
            contentAlignment = Alignment.CenterStart
        ) {
            if (textValue.isEmpty()) {
                Text(
                    modifier = Modifier.padding(start=16.dp),
                    text = "Search",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
            BasicTextField(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                value = textValue,
                onValueChange = {
                    onValueChange(it)
                },
                singleLine = true
            )
        }
    }
}