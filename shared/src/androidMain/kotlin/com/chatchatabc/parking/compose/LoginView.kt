package com.chatchatabc.parking.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp


@Composable
fun LoginView(
    modifier: Modifier = Modifier,
    loginTitle: String = "ParkingUser Login",
    errors: Map<String, String>,
    phone: String,
    tos: Boolean,
    username: String? = null,
    onPhoneChanged: (String) -> Unit,
    onUsernameChanged: (String) -> Unit = {},
    onTosChanged: (Boolean) -> Unit,
    onLogin: () -> Unit,
) {
    Column(modifier.width(IntrinsicSize.Max), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            loginTitle,
            modifier = Modifier
                .fillMaxWidth(),
            style = MaterialTheme.typography.titleLarge
        )

        if (username != null) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = username,
                onValueChange = { onUsernameChanged(it) },
                label = { Text("Username") },
                isError = errors.containsKey("username"),
                supportingText = {
                    errors["username"]?.let {
                        Text(
                            it,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }

        // TODO: Add validation for number
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = phone,
            onValueChange = { onPhoneChanged(it) },
            label = { Text("Phone Number") },
            isError = errors.containsKey("phone"),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            prefix = {
                Text("+63", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            },
            supportingText = {
                errors["phone"]?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = tos, onCheckedChange = { onTosChanged(it) })
            // TODO: Add link to TOS page
            Text(
                "I agree to the terms and conditions",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onTosChanged(!tos) },
                style = MaterialTheme.typography.labelLarge
            )
        }
        Button(modifier = Modifier.fillMaxWidth(), onClick = { onLogin() }) {
            Text("Login")
        }
    }
}