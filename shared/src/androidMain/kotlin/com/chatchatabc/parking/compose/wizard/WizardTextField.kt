package com.chatchatabc.parking.compose.wizard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun WizardTextField(
    value: String,
    description: String = "",
    keyName: String,
    onValueChange: (String) -> Unit,
    label: String,
    errors: Map<String, String>,
    supportingText: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        label = { Text(label) },
        singleLine = true,
        isError = errors.keys.contains(keyName),
        supportingText = {
            Column {
                supportingText?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall)
                }
                errors[keyName]?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}