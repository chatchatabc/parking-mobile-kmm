package com.chatchatabc.parking.compose.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chatchatabc.parking.viewModel.NewUserState
import com.chatchatabc.parking.viewModel.NewUserViewModel

// TODO: Scope composables better
@Composable
fun NewUserViewComposable(viewModel: NewUserViewModel, onContinue: () -> Unit) {
    val firstName by viewModel.firstName.collectAsState("")
    val lastName by viewModel.lastName.collectAsState("")
    val email by viewModel.email.collectAsState("")

    val loadState by viewModel.loadState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val errors by viewModel.errors.collectAsState()

    Box(Modifier.fillMaxSize()) {
        when (uiState) {
            NewUserState.INPUT -> Column(
                Modifier
                    .padding(32.dp)
                    .align(Alignment.Center)
                    .heightIn(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Tell us more about yourself", style = MaterialTheme.typography.headlineLarge)
                Card(
                    modifier = Modifier
                        .weight(1f, false)
                        .height(IntrinsicSize.Min)
                        .fillMaxWidth()
                        .width(IntrinsicSize.Max)
                ) {
                    Box(
                        Modifier
                            .verticalScroll(rememberScrollState())
//                            .padding(32.dp)
                            .fillMaxWidth()
                            .height(IntrinsicSize.Max)
                            .width(IntrinsicSize.Max)
                    ) {
                        Column(Modifier.padding(32.dp)) {
                            Text(text = "Full Name", style = MaterialTheme.typography.labelLarge)
                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = firstName,
                                onValueChange = { viewModel.firstName.value = it },
                                label = { Text("First Name") },
                                isError = errors.contains("firstName"),
                                supportingText = { if (errors.contains("firstName")) Text(errors["firstName"]!!) }
                            )
                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = lastName,
                                onValueChange = { viewModel.lastName.value = it },
                                label = { Text("Last Name") },
                                isError = errors.contains("lastName"),
                                supportingText = { if (errors.contains("lastName")) Text(errors["lastName"]!!) }
                            )
                            Text(
                                text = "Email",
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = email,
                                onValueChange = { viewModel.email.value = it },
                                label = { Text("Email") },
                                supportingText = { Text("Optional") }
                            )
                        }
                        if (loadState) {
                            Box(modifier = Modifier
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                                .clickable(enabled = false) {}
                                .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }

                }
                Button(
                    onClick = {
                        viewModel.validateAndSubmit()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(ButtonDefaults.MinHeight),
                    colors = ButtonDefaults.elevatedButtonColors(),
                    enabled = !loadState
                ) {
                    Text("Save")
                }
            }

            NewUserState.SUBMITTED -> {
                Column(
                    Modifier
                        .padding(32.dp)
                        .align(Alignment.Center),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Thank you!", style = MaterialTheme.typography.headlineLarge)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .width(IntrinsicSize.Max)
                    ) {
                        Box(
                            Modifier
                                .padding(32.dp)
                                .fillMaxWidth()
                                .width(IntrinsicSize.Max),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Filled.Check,
                                    contentDescription = "Check",
                                    modifier = Modifier.size(64.dp)
                                )
                                Text(
                                    text = "Your details have been saved.",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                    Button(
                        onClick = {
                            onContinue()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        colors = ButtonDefaults.elevatedButtonColors()
                    ) {
                        Text("Continue")
                    }
                }
            }
        }
    }
}