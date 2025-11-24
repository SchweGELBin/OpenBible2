package com.schwegelbin.openbible

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.schwegelbin.openbible.logic.getMainThemeOptions
import com.schwegelbin.openbible.ui.screens.App
import com.schwegelbin.openbible.ui.theme.OpenBibleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainApp()
        }
    }
}

@Composable
fun MainApp() {
    val context = LocalContext.current
    val systemDarkTheme = isSystemInDarkTheme()
    var (darkTheme, dynamicColor, amoled) = getMainThemeOptions(context)
    if (darkTheme == null) darkTheme = systemDarkTheme

    val isDarkTheme = remember { mutableStateOf(darkTheme) }
    val isDynamicColor = remember { mutableStateOf(dynamicColor) }
    val isAmoled = remember { mutableStateOf(amoled) }

    OpenBibleTheme(
        darkTheme = isDarkTheme.value,
        dynamicColor = isDynamicColor.value,
        amoled = isAmoled.value
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            App(
                onThemeChange = { newDarkTheme, newDynamicColor, newAmoled ->
                    if (newDarkTheme != null) isDarkTheme.value =
                        newDarkTheme else isDarkTheme.value = systemDarkTheme
                    if (newDynamicColor != null) isDynamicColor.value = newDynamicColor
                    if (newAmoled != null) isAmoled.value = newAmoled
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MainApp()
}