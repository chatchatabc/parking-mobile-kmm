package com.chatchatabc.parkingadmin.android.component

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationDropdown(
    items: List<String>,
    default: String,
    modifier: Modifier = Modifier,
    onSelect: (String) -> Unit,
) {
    var selected: String by rememberSaveable { mutableStateOf(
        if (items.contains(default)) default else items.first()
    )}

    var expanded: Boolean by remember { mutableStateOf(false) }

    var menuSize by remember { mutableStateOf(IntSize(0,0)) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        modifier = modifier.background(Color.Transparent),
        onExpandedChange = {
            expanded = it
            Log.d("WOWWW", "WWW")
        }
    ) {
        TextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
//            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            leadingIcon = { Icon(Icons.Filled.LocationOn, contentDescription = "Location Icon") },
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = if (expanded) Color.White else Color.Transparent,
                unfocusedLeadingIconColor = Color.White,
                focusedLeadingIconColor = if (expanded) Color.Black else Color.White,
                unfocusedTextColor = Color.White,
                focusedTextColor = if (expanded) Color.Black else Color.White,
                unfocusedTrailingIconColor = Color.White,
                focusedTrailingIconColor = if (expanded) Color.Black else Color.White,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            ),
            modifier = Modifier.menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.exposedDropdownSize(true)
        ) {
            items.forEach {
                DropdownMenuItem(text = {
                    Text(text = it,
                        color = Color.Black
                    )
                }, onClick = {
                    selected = it
                    onSelect(it)
                    expanded = false
                })
            }
        }
    }
}

@Preview
@Composable
fun LocationDropdownPreview() {
    LocationDropdown(
        items = listOf("Davao", "Cebu", "Manila"),
        default = "Davao",
    ) {

    }
}