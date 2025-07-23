rootProject.name = "FinAwareAdmin"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google() // ✅ No mavenContent filter here
        mavenCentral()
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
    }
}

include(":composeApp")
include(":server")
include(":shared")
