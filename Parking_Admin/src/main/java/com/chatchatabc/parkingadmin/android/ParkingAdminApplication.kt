package com.chatchatabc.parkingadmin.android

import android.app.Application
import com.chatchatabc.parking.di.AppModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ParkingAdminApplication: Application() {
    override fun onCreate() {
        startKoin {
            // load modules
            androidContext(this@ParkingAdminApplication)
            modules(AppModule)
        }

        super.onCreate()
    }
}