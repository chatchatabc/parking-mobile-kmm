package com.chatchatabc.parkingadmin.android

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.chatchatabc.parkingadmin.android.theme.AppTheme
import com.chatchatabc.parkingadmin.di.LoginModule
import com.chatchatabc.parkingadmin.viewModel.LoginState
import com.chatchatabc.parkingadmin.viewModel.LoginViewModel
import kotlinx.coroutines.delay
import org.koin.android.ext.android.inject
import org.koin.core.context.loadKoinModules
import kotlin.time.Duration.Companion.seconds

class LoginActivity : ComponentActivity() {
    val koinModule = loadKoinModules(LoginModule)

    val preferences: SharedPreferences by inject()

    val viewModel: LoginViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (preferences.contains("authToken")) {
            startActivity(Intent(
                this@LoginActivity,
                MainActivity::class.java
            ).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }

        setContent {
            val loginState by viewModel.uiState.collectAsState()
            val success by viewModel.isLoggedIn.collectAsState()

            val errors by viewModel.errors.collectAsState()

            LaunchedEffect(success) {
                if (success) {
                    startActivity(Intent(
                        this@LoginActivity,
                        if (!viewModel.hasUserDetails) NewUserActivity::class.java else MainActivity::class.java
                    ).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                }
            }

            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Box(Modifier.padding(32.dp)){
                        val otp by viewModel.otp.collectAsState()
                        val number by viewModel.phone.collectAsState()

                        Card(Modifier.align(Alignment.Center)) {
                            when (loginState) {
                                LoginState.PHONE -> LoginView(
                                    error = errors.filter { it.key == "phone" }.values.firstOrNull(),
                                    number = number,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    onNumberChanged = {
                                        viewModel.phone.value = it
                                        viewModel.errors.value = viewModel.errors.value.filter { it.key != "phone" }
                                                      },
                                    onLogin = {
                                        viewModel.validateAndSubmitPhone()
                                    }
                                )

                                LoginState.OTP -> OTPView(
                                    error = errors.filter { it.key == "otp" }.values.firstOrNull(),
                                    otp = otp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    number = number,
                                    onBackPressed = { viewModel.uiState.value = LoginState.PHONE },
                                    onOTPChanged = { otp ->
                                        viewModel.otp.value = otp
                                        viewModel.errors.value = viewModel.errors.value.filter { it.key != "otp" }
                                                   },
                                    onOTPConfirm = {
                                        viewModel.validateAndSumbitOTP()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoginView(
    number: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    onNumberChanged: (String) -> Unit,
    onLogin: (String) -> Unit,
) {
    var agreeToTerms by rememberSaveable { mutableStateOf(false) }

    Column(modifier.width(IntrinsicSize.Max), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Login",
            modifier = Modifier
                .fillMaxWidth(),
            style = MaterialTheme.typography.titleLarge
        )

        // TODO: Add validation for number
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = number,
            onValueChange = { onNumberChanged(it) },
            label = { Text("Phone Number") },
            // TODO: Improve error handling
            isError = error != null, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            prefix = {
                Text("+63", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            },
            supportingText = {
                error?.let {
                    Text(error, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.error)
                }
            }
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = agreeToTerms, onCheckedChange = { agreeToTerms = it })
            Text("I agree to the terms and conditions", modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.labelLarge)
        }
        Button(modifier = Modifier.fillMaxWidth(), onClick = { onLogin(number) }) {
            Text("Login")
        }
    }
}

@Composable
fun OTPView(
    error: String? = null,
    otp: String,
    modifier: Modifier = Modifier,
    number: String,
    onBackPressed: () -> Unit,
    onOTPChanged: (String) -> Unit,
    onOTPConfirm: () -> Unit
) {
    var timeout by rememberSaveable { mutableStateOf(60) }

    LaunchedEffect(Unit) {
        while(timeout != 0) {
            delay(1.seconds)
            timeout--
        }
    }

    Column(
        modifier = modifier.width(IntrinsicSize.Max),
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            Modifier
                .width(IntrinsicSize.Max)
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Sharp.ArrowBack, contentDescription = "Back",
                Modifier
                    .padding(end = 8.dp)
                    .clip(CircleShape)
                    .clickable { onBackPressed() })
            Text("Enter OTP",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleLarge)
        }

        error?.let {
            Text(it, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.error)
        }

        OtpTextField(modifier = Modifier.fillMaxWidth(), otpText = otp, onOtpTextChange = { it, isComplete ->
            onOTPChanged(it)
            if (isComplete) onOTPConfirm()
        })
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(number)
            Text(if (timeout != 0) "Resend in ${timeout}s" else "Resend OTP")
        }
    }
}

@Composable
fun OtpTextField(
    modifier: Modifier = Modifier,
    otpText: String,
    otpCount: Int = 6,
    onOtpTextChange: (String, Boolean) -> Unit
) {
    BasicTextField(
        modifier = modifier,
        value = TextFieldValue(otpText, selection = TextRange(otpText.length)),
        onValueChange = {
            if (it.text.length <= otpCount) {
                onOtpTextChange.invoke(it.text, it.text.length == otpCount)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        decorationBox = {
            Row(horizontalArrangement = Arrangement.Center) {
                repeat(otpCount) { index ->
                    CharView(
                        index = index,
                        text = otpText
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    )
}

@Composable
private fun RowScope.CharView(
    index: Int,
    text: String
) {
    val isFocused = text.length == index
    val char = when {
        index == text.length -> "_"
        index > text.length -> ""
        else -> text[index].toString()
    }
    Text(
        modifier = Modifier
            .weight(1f)
            .border(
                1.dp, when {
                    isFocused -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.outline
                }, RoundedCornerShape(8.dp)
            )
            .padding(2.dp),
        text = char,
        style = MaterialTheme.typography.displaySmall,
        color = MaterialTheme.colorScheme.secondary,
        textAlign = TextAlign.Center
    )
}
