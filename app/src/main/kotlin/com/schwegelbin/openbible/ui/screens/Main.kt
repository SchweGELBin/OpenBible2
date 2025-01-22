package com.schwegelbin.openbible.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.schwegelbin.openbible.logic.checkForUpdates
import com.schwegelbin.openbible.logic.getCheckAtStartup
import com.schwegelbin.openbible.logic.getFirstLaunch
import kotlinx.serialization.Serializable

@Serializable
object Read

@Serializable
data class Selection(val isSplitScreen: Boolean)

@Serializable
object Settings

@Serializable
object Start

@Composable
fun App(onThemeChange: (Boolean?, Boolean?, Boolean?) -> Unit) {
    val context = LocalContext.current
    var update = false
    if (getCheckAtStartup(context)) update = checkForUpdates(context, false)
    val startDestination: Any = if (update || getFirstLaunch(context)) Start else Read

    val navController = rememberNavController()
    NavHost(navController, startDestination = startDestination) {
        composable<Read> {
            ReadScreen(
                onNavigateToSelection = { isSplitScreen ->
                    navController.navigate(Selection(isSplitScreen))
                },
                onNavigateToSettings = { navController.navigate(Settings) },
                onNavigateToStart = {
                    navController.navigate(Start) {
                        popUpTo(0) { inclusive = true }
                    }
                })
        }
        composable<Selection> { backStackEntry ->
            val selection = backStackEntry.toRoute<Selection>()
            SelectionScreen(
                onNavigateToRead = {
                    navController.navigate(Read) {
                        popUpTo(0) { inclusive = true }
                    }
                }, selection.isSplitScreen
            )
        }
        composable<Settings> {
            SettingsScreen(
                onNavigateToRead = {
                    navController.navigate(Read) {
                        popUpTo(0) { inclusive = true }
                    }
                }, onThemeChange = onThemeChange
            )
        }
        composable<Start> {
            StartScreen(onNavigateToRead = {
                navController.navigate(Read) {
                    popUpTo(0) { inclusive = true }
                }
            })
        }
    }
}