pluginManagement {
  repositories {
    google()
    gradlePluginPortal()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    mavenCentral()
  }
}

dependencyResolutionManagement {
  repositories {
    google()
    mavenCentral()
    maven { url = uri("https://s01.oss.sonatype.org/content/repositories/releases/") }
  }
}

rootProject.name = "compose-ios"
include(":android")
include(":shared")
include(":iosCompose")
