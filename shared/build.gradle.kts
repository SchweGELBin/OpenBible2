plugins {
    alias(libs.plugins.android.multiplatformLibrary)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
}

compose.resources {
    packageOfResClass = "com.schwegelbin.openbible.shared.resources"
    publicResClass = true
}

kotlin {
    android {
        namespace = "com.schwegelbin.openbible.shared"
        compileSdk = libs.versions.app.compileSdk.get().toInt()
        minSdk = libs.versions.app.minSdk.get().toInt()

        androidResources {
            enable = true
        }
        withHostTest {
            isIncludeAndroidResources = true
        }
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")

    jvm()

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiTooling)
            implementation(libs.compose.uiToolingPreview)
        }
        commonMain.dependencies {
            implementation(libs.androidx.navigationCompose)
            implementation(libs.compose.foundation)
            implementation(libs.compose.componentsResources)
            implementation(libs.compose.material3)
            implementation(libs.compose.materialIconsExtended)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.kotlinx.serializationJson)
            implementation(libs.zip4j)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}