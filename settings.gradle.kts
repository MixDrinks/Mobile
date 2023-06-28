rootProject.name = "MixDrinks"

include(":androidApp")
include(":shared")

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
    }

    plugins {
        val kotlinVersion = extra["kotlin.version"] as String
        val agpVersion = extra["agp.version"] as String
        val composeVersion = extra["compose.version"] as String

        kotlin("jvm").version(kotlinVersion)
        kotlin("multiplatform").version(kotlinVersion)
        kotlin("android").version(kotlinVersion)

        kotlin("plugin.serialization").version(kotlinVersion)
        id("com.google.devtools.ksp").version("${kotlinVersion}-1.0.9")
        id("de.jensklingenberg.ktorfit") version "1.0.0"

        id("com.android.application").version(agpVersion)
        id("com.android.library").version(agpVersion)

        id("org.jetbrains.compose").version(composeVersion)

        kotlin("native.cocoapods").version(kotlinVersion)
        id("com.google.gms.google-services").version("4.3.14")
        id("com.google.firebase.crashlytics").version("2.9.6")
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
