import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

val ktorVersion = "2.3.10"

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(), iosArm64(), iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                // Compose Multiplatform
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)

                // Lifecycle
                implementation(libs.androidx.lifecycle.viewmodel)
                implementation(libs.androidx.lifecycle.runtimeCompose)

                // Kotlin Coroutines (Multiplatform core)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")

                // Ktor Client (Multiplatform)
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-client-logging:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

                // Shared module
                implementation(projects.shared)
            }
        }

        val androidMain by getting {
            dependencies {
                // Compose Android Preview
                implementation(compose.preview)
                implementation(libs.androidx.activity.compose)

                // Coil (Android-only)
                implementation("io.coil-kt:coil-compose:2.2.2")

                // Android Ktor Engine
                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")

                // Firebase BOM + Modules
                implementation(project.dependencies.platform("com.google.firebase:firebase-bom:33.16.0"))
                implementation("com.google.firebase:firebase-auth")
                implementation("com.google.firebase:firebase-firestore-ktx:24.10.1")

                // Coroutines for Android
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")

                // Material Icons
                implementation("androidx.compose.material:material-icons-extended:1.6.0")

                // Generative AI SDK (Android-only)
                implementation("com.google.genai:google-genai:1.4.1")
            }
        }

        val iosMain by creating {
            dependsOn(commonMain)
        }

        named("iosX64Main") {
            dependsOn(iosMain)
        }
        named("iosArm64Main") {
            dependsOn(iosMain)
        }
        named("iosSimulatorArm64Main") {
            dependsOn(iosMain)
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

android {
    namespace = "org.finawreadmin.project"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.finawreadmin.project"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        // Load Gemini API Key securely from local.properties
        val apiKey = project.findProperty("GEMINI_API_KEY") as String? ?: ""
        buildConfigField("String", "GEMINI_API_KEY", "\"$apiKey\"")
    }

    buildFeatures {
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.support.annotations)
    debugImplementation(compose.uiTooling)
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.compose.ui:ui:1.6.4")
}
