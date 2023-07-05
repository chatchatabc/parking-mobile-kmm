package com.chatchatabc.parkingclient.android.di

import androidx.room.Room
import com.chatchatabc.parking.api.InvoiceAPI
import com.chatchatabc.parking.api.JeepneyAPI
import com.chatchatabc.parking.api.ParkingAPI
import com.chatchatabc.parking.api.ProfileAPI
import com.chatchatabc.parking.api.RouteAPI
import com.chatchatabc.parking.api.UserAPI
import com.chatchatabc.parking.api.VehicleAPI
import com.chatchatabc.parking.di.AppModule
import com.chatchatabc.parking.di.EncryptedSharedPreferencesModule
import com.chatchatabc.parking.di.TokenModule
import com.chatchatabc.parking.service.NatsService
import com.chatchatabc.parkingclient.android.db.AppDB
import com.chatchatabc.parkingclient.android.db.entity.Converters
import com.chatchatabc.parkingclient.android.viewmodel.ClientMainViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val DBModule = module {
    single {
        Room.databaseBuilder(
            androidApplication(),
            AppDB::class.java,
            "ParkingDB"
        ).fallbackToDestructiveMigration()
            .addTypeConverter(Converters()).build()
    }
}

val MainMapModule = module {
    includes(TokenModule, EncryptedSharedPreferencesModule, DBModule, AppModule)
    single { UserAPI(get()) }
    single { ProfileAPI(get()) }
    single { ParkingAPI(get()) }
    single { VehicleAPI(get()) }
    single { InvoiceAPI(get()) }
    single { JeepneyAPI(get()) }
    single { RouteAPI(get()) }
    single { NatsService() }
    viewModel {
        ClientMainViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get())
    }
}