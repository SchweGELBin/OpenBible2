package com.schwegelbin.openbible

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.schwegelbin.openbible.shared.MainApp
import com.schwegelbin.openbible.shared.getConfigDir
import com.schwegelbin.openbible.shared.getPlatform


fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "OpenBible"
    ) {
        init()
        MainApp()
    }
}

fun init() {
    val prefsDir = getConfigDir(getPlatform())
    System.setProperty("java.util.prefs.systemRoot", prefsDir)
    System.setProperty("java.util.prefs.userRoot", prefsDir)
}