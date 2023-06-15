package com.chatchatabc.parkingadmin.android

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.chatchatabc.parking.compose.Theme.AppTheme
import com.chatchatabc.parking.di.NewUserModule
import com.chatchatabc.parking.viewModel.NewUserViewModel
import com.chatchatabc.parking.compose.user.NewUserViewComposable
import org.koin.android.ext.android.inject
import org.koin.core.context.loadKoinModules

class NewUserActivity : ComponentActivity() {
    val koinModule = loadKoinModules(NewUserModule)

    val viewModel: NewUserViewModel by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    NewUserViewComposable(viewModel) {
                        startActivity(Intent(this, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                    }
                }
            }
        }
    }
}