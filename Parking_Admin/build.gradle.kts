plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.serialization")
}

android {
    namespace = "com.chatchatabc.parkingadmin.android"
    compileSdk = 33
    defaultConfig {
        applicationId = "com.chatchatabc.parkingadmin.android"
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
    packagingOptions {
        resources {
//            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
        getByName("debug") {
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
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

val ktor_version: String by project

dependencies {
    implementation(project(":shared"))
    // androidMain

    implementation("androidx.compose.ui:ui:1.4.1")
    implementation("androidx.compose.ui:ui-tooling:1.4.1")
    implementation("androidx.compose.foundation:foundation:1.4.1")
    implementation("androidx.compose.material:material:1.4.1")
    implementation("androidx.compose.material3:material3:1.1.0-beta02")
    implementation("androidx.activity:activity-compose:1.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation(platform("androidx.compose:compose-bom:2022.10.00"))
    implementation("androidx.compose.ui:ui-graphics")
    androidTestImplementation(platform("androidx.compose:compose-bom:2022.10.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    implementation("com.google.maps.android:maps-compose:2.11.2")

    // Make sure to also include the latest version of the Maps SDK for Android
    implementation("com.google.android.gms:play-services-maps:18.1.0")

    // Optionally, you can include the Compose utils library for Clustering, etc.
    implementation("com.google.maps.android:maps-compose-utils:2.11.2")

    // Optionally, you can include the widgets library for ScaleBar, etc.
    implementation("com.google.maps.android:maps-compose-widgets:2.11.2")

    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")

    // For developers using AndroidX in their applications
//    implementation("pub.devrel:easypermissions:3.0.0")
    implementation("com.vmadalin:easypermissions-ktx:1.0.0")

    // Compose LiveData
    implementation("androidx.compose.runtime:runtime-livedata:1.4.1")

    // Accompanist

    // SystemUI Controller
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.30.1")
    // Insets
    implementation("com.google.accompanist:accompanist-insets:0.30.1")

    // Extended Material Icons
    implementation("androidx.compose.material:material-icons-extended:1.5.0-alpha02")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    // Ktor
    implementation("io.ktor:ktor-client-android:$ktor_version")
    // Koin
    implementation("io.insert-koin:koin-android:3.2.0")

    // Encrypted Shared Preferences
    implementation("androidx.security:security-crypto:1.0.0")

    implementation("androidx.core:core-splashscreen:1.0.0")

    val lottieVersion = "6.0.0"
    implementation("com.airbnb.android:lottie:$lottieVersion")
    implementation("com.airbnb.android:lottie-compose:$lottieVersion")

    implementation("io.coil-kt:coil-compose:2.3.0")

    // Zoomable
    implementation("net.engawapg.lib:zoomable:1.4.2")

    implementation("com.google.mlkit:barcode-scanning:17.1.0")

    implementation("androidx.camera:camera-camera2:1.3.0-alpha07")

    // https://mvnrepository.com/artifact/androidx.camera/camera-lifecycle
    runtimeOnly("androidx.camera:camera-lifecycle:1.3.0-alpha07")

    // https://mvnrepository.com/artifact/androidx.camera/camera-mlkit-vision
    implementation("androidx.camera:camera-mlkit-vision:1.3.0-alpha07")

    // https://mvnrepository.com/artifact/androidx.camera/camera-viewfinder
    // implementation("androidx.camera:camera-viewfinder:1.3.0-alpha07")


    // https://mvnrepository.com/artifact/androidx.camera/camera-core
    implementation("androidx.camera:camera-lifecycle:1.3.0-alpha07")
}