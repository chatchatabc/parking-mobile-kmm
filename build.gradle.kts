plugins {
    //trick: for the same plugin versions in all sub-modules
    id("com.android.application").version("8.2.0-alpha06").apply(false)
    id("com.android.library").version("8.2.0-alpha06").apply(false)
    kotlin("android").version("1.8.10").apply(false)
    kotlin("multiplatform").version("1.8.10").apply(false)
    kotlin("plugin.serialization").version("1.8.10").apply(false)
    id("io.realm.kotlin").version("1.8.0").apply(false)
    // KSP
    id("com.google.devtools.ksp").version("1.8.10-1.0.9").apply(false)
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
