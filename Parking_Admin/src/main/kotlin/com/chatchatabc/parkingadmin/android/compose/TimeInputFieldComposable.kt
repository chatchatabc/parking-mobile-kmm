package com.chatchatabc.parkingadmin.android.compose

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun TimeInputFieldComposable(
    time: Pair<Int, Int>,
    isError: Boolean,
    errorMessage: String?,
    onApply: (Int, Int) -> Unit
) {
    var isTimePickerShown by remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }
    val isTextFieldPressed by interactionSource.collectIsPressedAsState()

    LaunchedEffect(isTextFieldPressed) {
        if (isTextFieldPressed) isTimePickerShown = true
    }

    if (isTimePickerShown) {
        TimePickerDialogComposable(
            time = time,
            onCancel = { isTimePickerShown = false }) { hour, min ->
            isTimePickerShown = false
            onApply(hour, min)
        }
    }

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = time.toTimeStr(),
        onValueChange = {},
        readOnly = true,
        interactionSource = interactionSource,
        isError = isError,
        supportingText = {
            if (isError) {
                errorMessage?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    )
}

fun Pair<Int, Int>.toTimeStr(): String {
    // With AM/PM validation
    val hour = (if (first > 12) first - 12 else first).toString().padStart(2, '0')
    val min = second.toString().padStart(2, '0')
    val ampm = if (first < 12) "AM" else "PM"
    return "$hour:$min $ampm"
}