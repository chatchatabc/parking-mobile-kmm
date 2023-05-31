package com.chatchatabc.parking.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun OTPView(
    modifier: Modifier = Modifier,
    errors: Map<String, String> = emptyMap(),
    otp: String,
    number: String,
    timer: Int,
    onBackPressed: () -> Unit,
    onOTPChanged: (String) -> Unit,
    onOTPRefreshClicked: () -> Unit,
    onOTPConfirm: () -> Unit
) {
    Column(
        modifier = modifier.width(IntrinsicSize.Max),
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            Modifier
                .width(IntrinsicSize.Max)
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Sharp.ArrowBack, contentDescription = "Back",
                Modifier
                    .padding(end = 8.dp)
                    .clip(CircleShape)
                    .clickable { onBackPressed() })
            Text("Enter OTP",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleLarge)
        }

        OtpTextField(
            modifier = Modifier.fillMaxWidth(),
            otpText = otp,
            onOtpTextChange = { it, isComplete ->
                onOTPChanged(it)
                if (isComplete) onOTPConfirm()
            },
            isError = errors.containsKey("otp")
        )
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(number)
            Text(if (timer != 0) "Resend in ${timer}s" else "Resend OTP")
        }
    }
}