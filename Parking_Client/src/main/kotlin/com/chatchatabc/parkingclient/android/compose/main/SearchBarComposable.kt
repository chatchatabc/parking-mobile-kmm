package com.chatchatabc.parkingclient.android.compose.main

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SearchBarComposable(
    textValue: String,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 16.dp, 16.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            singleLine = true,
            value = textValue,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f),
            // rounded corners
            shape = RoundedCornerShape(16.dp),
            leadingIcon = {
                Icon(
                    Icons.Filled.Search,
                    "Search",
                    modifier = Modifier
                        .padding(end = 8.dp)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.LightGray,
                focusedContainerColor = Color.White,
                unfocusedLeadingIconColor = Color.Gray,
                focusedLeadingIconColor = Color.Black,
                unfocusedTextColor = Color.White,
                focusedTextColor = Color.Black,
                unfocusedTrailingIconColor = Color.Gray,
                focusedTrailingIconColor = Color.Black,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            ),
            trailingIcon = {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Search",
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            placeholder = {
                Text("Search")
            }
        )
    }
}