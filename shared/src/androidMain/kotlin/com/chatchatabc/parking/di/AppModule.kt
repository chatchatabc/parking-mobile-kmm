package com.chatchatabc.parking.di

import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.chatchatabc.parking.api.LoginAPI
import com.chatchatabc.parking.api.ParkingAPI
import com.chatchatabc.parking.api.UserAPI
import com.chatchatabc.parking.httpClient
import com.chatchatabc.parking.realm.ParkingLotRealmObject
import com.chatchatabc.parking.viewModel.ClientMapViewModel
import com.chatchatabc.parking.viewModel.LoginViewModel
import com.chatchatabc.parking.viewModel.MainViewModel
import com.chatchatabc.parking.viewModel.NewParkingLotViewModel
import com.chatchatabc.parking.viewModel.NewUserViewModel
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.statement.bodyAsText
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

// TODO: Properly scope and name the modules

val AppModule = module {
    single { httpClient {
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
    } }
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
        get<SharedPreferences>().getString("authToken", null)
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
        get<SharedPreferences>().getString("authToken", null)
    }
    single { ParkingAPI(get(), get(named("token"))) }
    viewModel { NewParkingLotViewModel(get(), get()) }
}

val MainModule = module {
    factory(named("token")) {
        get<SharedPreferences>().getString("authToken", null)
    }
    single { ParkingAPI(get(), get(named("token"))) }
    single { UserAPI(get(), get(named("token"))) }
    viewModel {
        MainViewModel(get(), get())
    }
}

val ParkingRealmModule = module {
    val config = RealmConfiguration.Builder(schema = setOf(ParkingLotRealmObject::class)).apply {
        deleteRealmIfMigrationNeeded()
    }.build()
    single(named("parkingRealm")) { Realm.open(config) }
}

val MainMapModule = module {
    factory(named("token")) {
        get<SharedPreferences>().getString("authToken", null)
    }
    single { ParkingAPI(get(), get(named("token"))) }
    viewModel {
        ClientMapViewModel(get(), get(named("parkingRealm")))
    }
}
