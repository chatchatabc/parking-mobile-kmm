plugins {
    id("com.android.application")
    kotlin("android")
    id("io.realm.kotlin")
    id("com.google.devtools.ksp")
    kotlin("plugin.serialization")
}

android {
    namespace = "com.chatchatabc.parkingclient.android"
    compileSdk = 33
    defaultConfig {
        applicationId = "com.chatchatabc.parkingclient.android"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.4"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(":shared"))

    implementation("androidx.compose.ui:ui:1.4.0")
    implementation("androidx.compose.ui:ui-tooling:1.4.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.4.0")
    implementation("androidx.compose.foundation:foundation:1.4.0")
    implementation("androidx.compose.material:material:1.4.3")
    implementation("androidx.activity:activity-compose:1.7.1")
    implementation("androidx.compose.material3:material3:1.1.0-beta02")

    // Koin
    implementation("io.insert-koin:koin-android:3.2.0")

    // Extended Material Icons
    implementation("androidx.compose.material:material-icons-extended:1.5.0-alpha02")

    // Maps SDK
    implementation("com.google.android.gms:play-services-maps:18.1.0")

    // Maps for Compose
    implementation("com.google.maps.android:maps-compose:2.11.2")
    // Maps Compose Utils
    implementation("com.google.maps.android:maps-compose-utils:2.11.2")

    // Maps Widgets
    implementation("com.google.maps.android:maps-compose-widgets:2.11.2")

    // https://mvnrepository.com/artifact/io.github.g0dkar/qrcode-kotlin-android
    implementation("io.github.g0dkar:qrcode-kotlin-android:3.3.0")

    // TODO: Maybe move to shared?
    // https://mvnrepository.com/artifact/com.spoton/nats-android
    implementation("com.spoton:nats-android:2.4.2")

    // https://mvnrepository.com/artifact/com.jakewharton.timber/timber
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Kotlinx-datetime
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

    implementation("com.mapbox.maps:android:10.14.0")

    val room_version = "2.5.2"

    implementation("androidx.room:room-runtime:$room_version")
    ksp("androidx.room:room-compiler:$room_version")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
}