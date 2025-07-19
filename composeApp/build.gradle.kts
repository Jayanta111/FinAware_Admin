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

    iosX64()
    iosArm64()
    iosSimulatorArm64()

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

                // Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")

                // Ktor Multiplatform
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-client-logging:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

                // Gemini GenAI
                implementation("com.google.genai:google-genai:1.4.1")

                // Shared module
                implementation(projects.shared)
            }
        }

        val androidMain by getting {
            dependencies {
                // Android-only Compose
                implementation(compose.preview)
                implementation(libs.androidx.activity.compose)

                // Coil
                implementation("io.coil-kt:coil-compose:2.2.2")

                // Android Ktor
                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")

                // Firebase
                implementation(project.dependencies.platform("com.google.firebase:firebase-bom:33.16.0"))
                implementation("com.google.firebase:firebase-auth")
                implementation("com.google.firebase:firebase-firestore-ktx:24.10.1")
                implementation("com.google.firebase:firebase-storage-ktx")

                // Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")

                // Material Icons Extended (fixes icon issue)
                implementation("androidx.compose.material:material-icons-extended:1.6.1")
            }
        }

        val iosMain by creating {
            dependsOn(commonMain)
        }
        named("iosX64Main") { dependsOn(iosMain) }
        named("iosArm64Main") { dependsOn(iosMain) }
        named("iosSimulatorArm64Main") { dependsOn(iosMain) }

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

        buildConfigField("String", "GEMINI_API_KEY", "\"${project.properties["GEMINI_API_KEY"]}\"")
    }

    buildFeatures {
        buildConfig = true
    }

    // ✅ FIX packaging conflict (for META-INF/INDEX.LIST)
    packaging {
        resources {
            excludes += setOf(
                "META-INF/INDEX.LIST",
                "META-INF/LICENSE.md",
                "META-INF/LICENSE-notice.md",
                "META-INF/DEPENDENCIES",
                "META-INF/AL2.0",
                "META-INF/LGPL2.1"
            )
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
    implementation(libs.generativeai)
    debugImplementation(compose.uiTooling)

    // UI + Icons
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.compose.ui:ui:1.6.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.compose.material:material-icons-extended:1.6.1")
}
