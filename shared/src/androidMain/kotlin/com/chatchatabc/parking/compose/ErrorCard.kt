package com.chatchatabc.parking.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ErrorCard(
    modifier: Modifier = Modifier,
    error: Collection<String>,
    onDismiss: () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.width(IntrinsicSize.Max).weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                error.withIndex().forEach { (index, string) ->
                    Text(
                        string,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onError
                    )
                    if (index != error.size - 1) {
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .size(1.dp),
                            color = MaterialTheme.colorScheme.onError.copy(alpha = 0.25f)
                        )
                    }
                }
            }
            IconButton(
                modifier = Modifier.size(48.dp),
                onClick = { onDismiss() }
            ) {
                Icon(Icons.Sharp.Close, contentDescription = "Close")
            }
        }
    }
}

@Preview
@Composable
fun ErrorCardPreview(){
    ErrorCard(
        modifier = Modifier.fillMaxWidth(),
        error = listOf("You must agree to the TOS", "Incorrect username/password"),
        onDismiss = {}
    )
}