package com.chatchatabc.parkingadmin.android.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialogComposable(
    time: Pair<Int, Int>,
    onCancel: () -> Unit,
    onApply: (Int, Int) -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            onCancel()
        },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true),
    ) {
        (LocalView.current.parent as DialogWindowProvider).window.setDimAmount(0.25f)

        val state = rememberTimePickerState(
            initialHour = time.first,
            initialMinute = time.second,
            is24Hour = false
        )

        Card(colors = CardDefaults.elevatedCardColors()) {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Enter Time", style = MaterialTheme.typography.labelLarge)
                TimePicker(state = state)
                Row(
                    modifier = Modifier.align(Alignment.End),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onCancel() },
                        colors = ButtonDefaults.textButtonColors()
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = { onApply(state.hour, state.minute) },
                        colors = ButtonDefaults.textButtonColors()
                    ) {
                        Text("Apply")
                    }
                }
            }
        }
    }
}