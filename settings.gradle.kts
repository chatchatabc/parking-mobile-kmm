pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            authentication {
                create("basic", BasicAuthentication::class)
            }
            credentials {
                // Do not change the username below.
                // This should always be `mapbox` (not your username).
                username = "mapbox"
                // Use the secret token you stored in gradle.properties as the password
                password = "sk.eyJ1IjoibWlrZWFybmFkbzEyMyIsImEiOiJjbGo0MnYwbHUxdnkwM3JxaHJhazE2eGZrIn0.FZ9SfUWPF2fH-00MZxasMg"
            }
        }

    }
}

rootProject.name = "Parking_Admin"
include(":Parking_Admin")
include(":Parking_Client")
include(":shared")
