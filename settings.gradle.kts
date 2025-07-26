rootProject.name = "FinAwareAdmin"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google() // ✅ No mavenContent filter here
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/ktor/eap")

        gradlePluginPortal()
    }
    plugins {
        id("com.google.gms.google-services") version "4.4.0" // ✅ Use correct version
    }
}

dependencyResolutionManagement {
    repositories {
        google() // ✅ No mavenContent filter here either
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/ktor/eap")

    }
}

include(":composeApp")
include(":server")
include(":shared")