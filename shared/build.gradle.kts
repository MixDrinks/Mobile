@file:Suppress("UnusedPrivateMember", "MaxLineLength")

import org.jetbrains.compose.ComposeBuildConfig

plugins {
  kotlin("multiplatform")
  kotlin("native.cocoapods")
  id("com.android.library")
  id("org.jetbrains.compose")
  kotlin("plugin.serialization") version "1.8.10"
  id("com.google.devtools.ksp") version "1.8.10-1.0.9"
  id("de.jensklingenberg.ktorfit") version "1.0.0"
  id("kotlin-parcelize")
}

configure<de.jensklingenberg.ktorfit.gradle.KtorfitGradleConfiguration> {
  version = "1.0.0"
}

val ktorVersion = "2.2.4"
val ktorfitVersion = "1.0.1"

kotlin {
  android()

  ios()
  iosSimulatorArm64()

  cocoapods {
    summary = "Shared Module for mix drinks"
    homepage = "Shared Module"
    version = "1.0"
    ios.deploymentTarget = "14.1"
    podfile = project.file("../ios/Podfile")
    framework {
      baseName = "shared"
      isStatic = true
    }
  }

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(compose.ui)
        implementation(compose.foundation)
        implementation(compose.material)
        implementation(compose.runtime)

        implementation("org.jetbrains.compose.components:components-resources:${ComposeBuildConfig.composeVersion}")
        implementation("org.mixdrinks:core:0.8.1-777512bd")

        implementation("de.jensklingenberg.ktorfit:ktorfit-lib:1.0.1")

        implementation("io.ktor:ktor-client-serialization:$ktorVersion")
        implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
        implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

        implementation("com.arkivanov.decompose:decompose:1.0.0")
        implementation("com.arkivanov.decompose:extensions-compose-jetpack:1.0.0")

        implementation("io.github.qdsfdhvh:image-loader:1.2.10")
      }
    }
    val commonTest by getting {
      dependencies {
        implementation(kotlin("test"))
      }
    }
    val androidMain by getting {
      dependsOn(commonMain)
      dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
      }
    }
    val iosMain by getting {
      dependsOn(commonMain)
    }
    val iosSimulatorArm64Main by getting {
      dependsOn(iosMain)
    }
  }
}

android {
  namespace = "org.mixdrinks"
  compileSdk = 33
  defaultConfig {
    minSdk = 24
    targetSdk = 33
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
}

dependencies {
  add("kspCommonMainMetadata", "de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorfitVersion")
  add("kspAndroid", "de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorfitVersion")
  add("kspIosX64", "de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorfitVersion")
  add("kspIosArm64", "de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorfitVersion")
  add("kspIosSimulatorArm64", "de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorfitVersion")
}
