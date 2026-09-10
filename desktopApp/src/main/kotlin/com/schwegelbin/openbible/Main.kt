package com.schwegelbin.openbible

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "OpenBible"
    ) {
        App()
    }
}

@Composable
fun App() {
    MaterialTheme {
        Text("Test")
    }
}