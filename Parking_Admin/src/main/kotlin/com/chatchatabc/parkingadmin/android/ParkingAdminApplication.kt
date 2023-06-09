package com.chatchatabc.parkingadmin.android

import android.app.Application
import com.chatchatabc.parking.di.AppModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber


class ParkingAdminApplication: Application() {
    override fun onCreate() {
        // TODO: Add conditional to only work in debug builds
        Timber.plant(Timber.DebugTree())

        startKoin {
            // load modules
            androidContext(this@ParkingAdminApplication)
            modules(AppModule)
        }

        super.onCreate()
    }
}