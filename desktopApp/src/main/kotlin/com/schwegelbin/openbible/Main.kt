package com.schwegelbin.openbible

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.schwegelbin.openbible.shared.MainApp

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "OpenBible"
    ) {
        MainApp()
    }
}