package com.chatchatabc.parking.compose.wizard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WizardCheckBox(
    value: Boolean,
    onValueChange: (Boolean) -> Unit,
    label: String,
    errors: Map<String, String>,
    supportingText: String? = null,
    keyName: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Checkbox(checked = value, onCheckedChange = {
            onValueChange(it)
        })
        Column (Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(label)
            supportingText?.let {
                Text(it, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}