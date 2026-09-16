import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.jvm)
}

compose.desktop {
    application {
        mainClass = "com.schwegelbin.openbible.MainKt"

        nativeDistributions {
            licenseFile.set(rootProject.file("LICENSE"))
            packageName = "OpenBible"
            packageVersion = libs.versions.app.versionName.get()
            targetFormats(TargetFormat.Deb, TargetFormat.Dmg, TargetFormat.Msi)
            linux {
                appCategory = "EDUCATION"
                iconFile.set(project.file("src/main/res/drawable/app-icon.png"))
            }
            macOS {
                appCategory = "public.app-category.education"
                iconFile.set(project.file("src/main/res/drawable/app-icon.icns"))
            }
            windows {
                iconFile.set(project.file("src/main/res/drawable/app-icon.ico"))
            }
        }
    }
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)
    implementation(libs.compose.uiToolingPreview)
    implementation(project(":shared"))
}