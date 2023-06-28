@file:Suppress("UnusedPrivateMember")

plugins {
    kotlin("multiplatform")
    id("com.android.application")
    id("org.jetbrains.compose")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

kotlin {
    android()
    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation(project(":shared"))
                implementation("com.google.android.gms:play-services-auth:20.6.0")
                implementation(platform("com.google.firebase:firebase-bom:32.1.1"))
                implementation("com.google.firebase:firebase-auth-ktx")
                implementation("com.google.firebase:firebase-crashlytics-ktx")
                implementation("com.google.firebase:firebase-analytics-ktx")
            }
        }
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "org.mixdrinks"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        applicationId = "org.mixdrinks.app"
        minSdk = (findProperty("android.minSdk") as String).toInt()
        targetSdk = (findProperty("android.targetSdk") as String).toInt()
        versionCode = System.getenv("MIXDRINKS_MOBILE_APP_VERSION_CODE")?.toIntOrNull() ?: 1
        versionName = System.getenv("MIXDRINKS_MOBILE_APP_VERSION_NAME") ?: "0.0.1"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            this.resValue("string", "app_name", "MixDrinks")
        }
        getByName("debug") {
            isDebuggable = true
            this.resValue("string", "app_name", "MixDrinks")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        jvmToolchain(11)
    }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:32.1.1"))
    implementation("com.google.firebase:firebase-analytics-ktx")
}
