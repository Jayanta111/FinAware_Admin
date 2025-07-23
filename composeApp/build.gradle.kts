@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)

    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.24"
    id("com.google.gms.google-services")
}

val ktorVersion = "2.3.10"
val serializationVersion = "1.6.3"

kotlin {
    androidTarget {
        compilations.all {
            compilerOptions.configure {
                jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
                freeCompilerArgs.add("-Xcontext-receivers")
            }
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)

                implementation(libs.androidx.lifecycle.viewmodel)
                implementation(libs.androidx.lifecycle.runtimeCompose)

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")

                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-client-logging:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                implementation("io.ktor:ktor-client-cio:$ktorVersion")

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
                implementation("com.google.genai:google-genai:1.4.1")
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(compose.preview)
                implementation(libs.androidx.activity.compose)

                implementation("io.coil-kt:coil-compose:2.2.2")
                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")

                implementation(project.dependencies.platform("com.google.firebase:firebase-bom:32.7.3"))
                implementation(libs.firebase.auth)
                implementation("com.google.firebase:firebase-firestore-ktx:24.10.1")
                implementation("com.google.firebase:firebase-storage-ktx")

                implementation("io.grpc:grpc-okhttp:1.57.2")
                implementation("io.grpc:grpc-protobuf-lite:1.57.2")
                implementation("io.grpc:grpc-stub:1.57.2")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
                implementation("org.litote.kmongo:kmongo:4.11.0")
                implementation("org.litote.kmongo:kmongo-coroutine:4.11.0")

                implementation("androidx.navigation:navigation-compose:2.7.7")
                implementation("androidx.compose.material:material-icons-extended:1.6.1")

                implementation(libs.jetbrains.kotlinx.serialization.json)
                implementation("com.halilibo.compose-richtext:richtext-ui-material3:0.17.0")

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

    packaging {
        resources {
            excludes += setOf(
                "META-INF/INDEX.LIST",
                "META-INF/LICENSE.md",
                "META-INF/LICENSE-notice.md",
                "META-INF/DEPENDENCIES",
                "META-INF/AL2.0",
                "META-INF/LGPL2.1",
                "META-INF/native-image/org.mongodb/bson/native-image.properties"
            )
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
