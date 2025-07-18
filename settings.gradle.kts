rootProject.name = "FinAwareAdmin"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google() // ✅ No mavenContent filter here
        mavenCentral()
        gradlePluginPortal()
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
