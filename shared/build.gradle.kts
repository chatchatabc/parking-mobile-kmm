plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    kotlin("plugin.serialization")
    id("io.realm.kotlin")
}

val version = "3.2.0"
val core = "io.insert-koin:koin-core:$version"
val android = "io.insert-koin:koin-android:$version"

kotlin {
    android {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../Parking_Admin/Podfile")
        framework {
            baseName = "shared"
        }
    }

    val koin_version = "3.2.0"
    
    sourceSets {
        val ktorVersion = "2.3.0"

        val commonMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-logging:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")

                api("io.insert-koin:koin-core:$koin_version")

                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")

                // Realm
                implementation("io.realm.kotlin:library-base:1.8.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
                implementation("io.insert-koin:koin-android:$koin_version")

                implementation("androidx.security:security-crypto:1.0.0")

                // Form Builder
                implementation("com.github.jkuatdsc:form-builder:1.0.6")

                implementation("androidx.compose.ui:ui:1.4.1")
                implementation("androidx.compose.ui:ui-tooling:1.4.1")
                implementation("androidx.compose.foundation:foundation:1.4.1")
                implementation("androidx.compose.foundation:foundation-layout:1.4.1")
                implementation("androidx.compose.material:material:1.4.1")
                implementation("androidx.compose.material3:material3:1.1.0-beta02")

                implementation("com.google.android.gms:play-services-location:21.0.1")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")

                implementation("com.google.android.gms:play-services-maps:18.1.0")

                // https://mvnrepository.com/artifact/io.realm/realm-android-kotlin-extensions
                implementation("io.realm:realm-android-kotlin-extensions:10.15.1")

                // https://mvnrepository.com/artifact/io.github.g0dkar/qrcode-kotlin-android
                implementation("io.github.g0dkar:qrcode-kotlin-android:3.3.0")
            }
        }
//        val androidUnitTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation("io.ktor:ktor-client-darwin:$ktorVersion")
            }
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

android {
    namespace = "com.chatchatabc.parkingadmin"
    compileSdk = 33
    defaultConfig {
        minSdk = 24
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.4"
    }
}