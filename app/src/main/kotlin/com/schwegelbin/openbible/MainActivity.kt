package com.schwegelbin.openbible

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.schwegelbin.openbible.logic.getMainThemeOptions
import com.schwegelbin.openbible.ui.screens.App
import com.schwegelbin.openbible.ui.theme.OpenBibleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainApp()
        }
    }
}

@Composable
fun MainApp() {
    val context = LocalContext.current
    var (darkTheme, dynamicColor, amoled) = getMainThemeOptions(context)
    if (darkTheme == null) darkTheme = isSystemInDarkTheme()

    OpenBibleTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, amoled = amoled) {
        Surface(modifier = Modifier.fillMaxSize()) {
            App()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MainApp()
}