package com.schwegelbin.openbible.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.schwegelbin.openbible.R
import com.schwegelbin.openbible.logic.Screen
import com.schwegelbin.openbible.logic.getDefaultFiles

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val context = LocalContext.current
    getDefaultFiles(context)

    val currentScreen = remember { mutableStateOf(Screen.Home) }
    val showSettings = remember { mutableStateOf(false) }

    if (showSettings.value) {
        SettingsScreen(onClose = { showSettings.value = false })
    } else {
        Scaffold(topBar = {
            TopAppBar(colors = TopAppBarDefaults.topAppBarColors(titleContentColor = MaterialTheme.colorScheme.primary),
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = { showSettings.value = true }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(R.string.settings)
                        )
                    }
                })
        }, bottomBar = {
            NavigationBar {
                Screen.entries.forEach { screen ->
                    var label = ""
                    NavigationBarItem(icon = {
                        when (screen) {
                            Screen.Home -> {
                                Icon(
                                    Icons.Filled.Home,
                                    contentDescription = stringResource(R.string.screen_home)
                                )
                                label = stringResource(R.string.screen_home)
                            }

                            Screen.Read -> {
                                Icon(
                                    Icons.Filled.Star,
                                    contentDescription = stringResource(R.string.screen_read)
                                )
                                label = stringResource(R.string.screen_read)
                            }

                            else -> {}
                        }
                    },
                        label = { Text(label) },
                        selected = currentScreen.value == screen,
                        onClick = { currentScreen.value = screen },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
        }) { innerPadding ->
            when (currentScreen.value) {
                Screen.Home -> HomeScreen(modifier = Modifier.padding(innerPadding))
                Screen.Read -> ReadScreen(modifier = Modifier.padding(innerPadding))
                else -> {}
            }
        }
    }
}