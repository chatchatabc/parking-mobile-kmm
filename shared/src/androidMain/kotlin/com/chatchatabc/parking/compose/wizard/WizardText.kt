package com.chatchatabc.parking.compose.wizard

import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun WizardText(
    text: String
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge
    )
}