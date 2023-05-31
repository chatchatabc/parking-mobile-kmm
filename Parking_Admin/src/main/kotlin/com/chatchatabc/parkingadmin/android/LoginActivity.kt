package com.chatchatabc.parkingadmin.android

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chatchatabc.parking.compose.LoginView
import com.chatchatabc.parking.compose.OTPView
import com.chatchatabc.parking.compose.Theme.AppTheme
import com.chatchatabc.parking.di.LoginModule
import com.chatchatabc.parking.viewModel.LoginState
import com.chatchatabc.parking.viewModel.LoginType
import com.chatchatabc.parking.viewModel.LoginViewModel
import org.koin.android.ext.android.inject
import org.koin.core.context.loadKoinModules

class LoginActivity : ComponentActivity() {
    val koinModule = loadKoinModules(LoginModule)
    val preferences: SharedPreferences by inject()

    val viewModel: LoginViewModel by inject()

    @OptIn(ExperimentalFoundationApi::class)
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
                        val phone by viewModel.phone.collectAsState()
                        val tos by viewModel.tos.collectAsState()
                        val timer by viewModel.timer.collectAsState()

                        Card(Modifier.align(Alignment.Center)) {
                            when (loginState) {
                                LoginState.PHONE -> LoginView(
                                    loginTitle = "Parking Owner Login",
                                    errors = errors,
                                    phone = phone,
                                    tos = tos,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    onPhoneChanged = {
                                        viewModel.phone.value = it
                                        viewModel.errors.value = viewModel.errors.value.filter { it.key != "phone" }
                                                      },
                                    onTosChanged = {
                                        viewModel.tos.value = it
                                        viewModel.errors.value = viewModel.errors.value.filter { it.key != "tos" }},
                                    onLogin = {
                                        viewModel.validateAndSubmitPhone(LoginType.ADMIN)
                                    }
                                )

                                LoginState.OTP -> OTPView(
                                    errors = errors,
                                    timer = timer,
                                    otp = otp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    number = phone,
                                    onBackPressed = { viewModel.uiState.value = LoginState.PHONE },
                                    onOTPChanged = { otp ->
                                        viewModel.otp.value = otp
                                        viewModel.errors.value = viewModel.errors.value.filter { it.key != "otp" }
                                                   },
                                    onOTPRefreshClicked = {
                                        viewModel.validateAndSubmitPhone(LoginType.ADMIN)
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