package com.chatchatabc.parking.di

import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.chatchatabc.parking.api.LoginAPI
import com.chatchatabc.parking.api.ParkingAPI
import com.chatchatabc.parking.api.ProfileAPI
import com.chatchatabc.parking.api.UserAPI
import com.chatchatabc.parking.api.VehicleAPI
import com.chatchatabc.parking.httpClient
import com.chatchatabc.parking.realm.ParkingLotRealmObject
import com.chatchatabc.parking.viewModel.AccountViewModel
import com.chatchatabc.parking.viewModel.ClientMainViewModel
import com.chatchatabc.parking.viewModel.LoginViewModel
import com.chatchatabc.parking.viewModel.MainViewModel
import com.chatchatabc.parking.viewModel.NewParkingLotViewModel
import com.chatchatabc.parking.viewModel.NewUserViewModel
import com.chatchatabc.parking.viewModel.NewVehicleViewModel
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.statement.bodyAsText
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val TokenModule = module {
    factory(named("token")) {
        get<SharedPreferences>().getString("authToken", null)
    }
}

val EncryptedSharedPreferencesModule = module {
    factory(named("masterKey")) {
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

val AppModule = module {
    single {
        httpClient {
            // Allow incomplete JSON values
            install(ContentNegotiation) {
                Json {
                    ignoreUnknownKeys = true
                }
            }
            ResponseObserver {
                // Print entire response
                println(it.bodyAsText())
            }
        }
    }
}

val LoginModule = module {
    includes(EncryptedSharedPreferencesModule)
    single { LoginAPI(get()) }
    viewModel { LoginViewModel(get(), get()) }
}

val NewUserModule = module {
    includes(TokenModule, EncryptedSharedPreferencesModule)
    single {
        UserAPI(get())
    }
    viewModel { NewUserViewModel(get(), get()) }
}

val NewParkingLotModule = module {
    includes(TokenModule)
    single { ParkingAPI(get()) }
    viewModel { NewParkingLotViewModel(get(), get()) }
}

val MainModule = module {
    includes(TokenModule, EncryptedSharedPreferencesModule)
    single { ParkingAPI(get()) }
    single { UserAPI(get()) }
    single { ProfileAPI(get()) }
    viewModel {
        MainViewModel(get(), get(), get(), get())
    }
}

val ParkingRealmModule = module {
    val config = RealmConfiguration.Builder(schema = setOf(ParkingLotRealmObject::class)).apply {
        deleteRealmIfMigrationNeeded()
    }.build()
    single(named("parkingRealm")) { Realm.open(config) }
}

val MainMapModule = module {
    includes(TokenModule, EncryptedSharedPreferencesModule)
    single { ProfileAPI(get()) }
    single { ParkingAPI(get()) }
    single { VehicleAPI(get()) }
    viewModel {
        ClientMainViewModel(get(), get(), get(), get(named("parkingRealm")), get())
    }
}

val NewVehicleModule = module {
    includes(TokenModule, EncryptedSharedPreferencesModule)
    single { VehicleAPI(get()) }
    viewModel {
        NewVehicleViewModel(get())
    }
}

val AccountModule = module {
    includes(TokenModule, EncryptedSharedPreferencesModule)
    single { ProfileAPI(get()) }
    viewModel {
        AccountViewModel(get(), get())
    }
}