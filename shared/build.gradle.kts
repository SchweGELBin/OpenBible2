plugins {
    alias(libs.plugins.android.multiplatformLibrary)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.multiplatform)
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

    jvm()

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiTooling)
            implementation(libs.compose.uiToolingPreview)
        }
        commonMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}