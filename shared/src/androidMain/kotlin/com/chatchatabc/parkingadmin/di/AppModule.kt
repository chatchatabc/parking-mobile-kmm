package com.chatchatabc.parkingadmin.di

import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.chatchatabc.parkingadmin.api.LoginAPI
import com.chatchatabc.parkingadmin.api.ParkingAPI
import com.chatchatabc.parkingadmin.api.UserAPI
import com.chatchatabc.parkingadmin.httpClient
import com.chatchatabc.parkingadmin.viewModel.LoginViewModel
import com.chatchatabc.parkingadmin.viewModel.MainViewModel
import com.chatchatabc.parkingadmin.viewModel.NewParkingLotViewModel
import com.chatchatabc.parkingadmin.viewModel.NewUserViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

// TODO: Properly scope and name the modules

val AppModule = module {
    single { httpClient {} }
    single(named("masterKey")) {
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    }
    single {
        EncryptedSharedPreferences.create(
            "ParkingAdminPreferences",
            get(named("masterKey")),
            get(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}

val LoginModule = module {
    single { LoginAPI(get()) }
    viewModel { LoginViewModel(get(), get()) }
}

val NewUserModule = module {
    factory(named("masterKey")) {
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    }
    factory(named("token")) {
        get<SharedPreferences>().getString("authToken", null) as Token
    }
    single {
        UserAPI(get(), get(named("token")))
    }
    single {
        EncryptedSharedPreferences.create(
            "ParkingAdminPreferences",
            get(named("masterKey")),
            get(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    viewModel { NewUserViewModel(get(), get()) }
}

typealias Token = String

val NewParkingLotModule = module {
    factory(named("token")) {
        get<SharedPreferences>().getString("authToken", null) as Token
    }
    single { ParkingAPI(get(), get(named("token"))) }
    viewModel { NewParkingLotViewModel(get(), get()) }
}

val MainModule = module {
    factory(named("token")) {
        get<SharedPreferences>().getString("authToken", null) as Token
    }
    single { ParkingAPI(get(), get(named("token"))) }
    single { UserAPI(get(), get(named("token"))) }
    viewModel {
        MainViewModel(get(), get())
    }
}