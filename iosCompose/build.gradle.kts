@file:Suppress("UnusedPrivateMember")
import org.jetbrains.compose.experimental.dsl.IOSDevices
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("multiplatform")
  id("org.jetbrains.compose") version "1.3.1"
}

version = "1.0"

repositories {
  mavenLocal()
  mavenCentral()
  gradlePluginPortal()
  maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
  google()
}

kotlin {
  iosX64("uikitX64") {
    binaries {
      executable {
        entryPoint = "main"
        freeCompilerArgs += listOf(
            "-linker-option", "-framework", "-linker-option", "Metal",
            "-linker-option", "-framework", "-linker-option", "CoreText",
            "-linker-option", "-framework", "-linker-option", "CoreGraphics"
        )
      }
    }
  }
  iosArm64("uikitArm64") {
    binaries {
      executable {
        entryPoint = "main"
        freeCompilerArgs += listOf(
            "-linker-option", "-framework", "-linker-option", "Metal",
            "-linker-option", "-framework", "-linker-option", "CoreText",
            "-linker-option", "-framework", "-linker-option", "CoreGraphics"
        )
        freeCompilerArgs += "-Xdisable-phases=VerifyBitcode"
      }
    }
  }

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(project(":shared"))
        implementation(compose.ui)
        implementation(compose.foundation)
        implementation(compose.material)
        implementation(compose.runtime)
      }
    }

    val commonTest by getting {
      dependencies {}
    }

    val nativeMain by creating {
      dependsOn(commonMain)
    }
    val uikitMain by creating {
      dependsOn(nativeMain)
    }
    val uikitX64Main by getting {
      dependsOn(uikitMain)
    }
    val uikitArm64Main by getting {
      dependsOn(uikitMain)
    }
  }
}

compose.experimental {
  web.application {}
  uikit.application {
    bundleIdPrefix = "org.mixdrinks"
    projectName = "MixDrinks"
    deployConfigurations {
      simulator("IPhone13Pro") {
        //Usage: ./gradlew iosDeployIPhone13ProDebug
        device = IOSDevices.IPHONE_13_PRO
      }
      simulator("IPad") {
        //Usage: ./gradlew iosDeployIPadDebug
        device = IOSDevices.IPAD_MINI_6th_Gen
      }
      connectedDevice("Device") {
        //Usage: ./gradlew iosDeployDeviceRelease
      }
    }
  }
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "11"
}

kotlin {
  targets.withType<KotlinNativeTarget> {
    binaries.all {
      freeCompilerArgs += "-Xdisable-phases=VerifyBitcode"
    }
  }
}

project.tasks.withType(org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile::class.java).configureEach {
  kotlinOptions.freeCompilerArgs += listOf(
      "-Xir-dce-runtime-diagnostic=log"
  )
}
