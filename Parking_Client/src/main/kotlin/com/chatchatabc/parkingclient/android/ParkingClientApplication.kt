package com.chatchatabc.parkingclient.android

import android.app.Application
import com.chatchatabc.parking.di.AppModule
import com.chatchatabc.parkingclient.android.di.DBModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ParkingClientApplication: Application() {
    override fun onCreate() {
        startKoin {
            // load modules
            androidContext(this@ParkingClientApplication)
            modules(AppModule, DBModule)
        }
        super.onCreate()
    }
}