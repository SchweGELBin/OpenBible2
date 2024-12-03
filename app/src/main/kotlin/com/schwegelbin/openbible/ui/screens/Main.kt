package com.schwegelbin.openbible.ui.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.schwegelbin.openbible.logic.getFirstLaunch
import com.schwegelbin.openbible.logic.saveNewIndex
import kotlinx.serialization.Serializable

@Serializable
object Read

@Serializable
data class Selection(val isSplitScreen: Boolean)

@Serializable
object Settings

@Serializable
object Start

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(onThemeChange: (Boolean?, Boolean?, Boolean?) -> Unit) {
    val context = LocalContext.current
    saveNewIndex(LocalContext.current)

    var startDestination: Any = Start
    if (!getFirstLaunch(context)) startDestination = Read

    val navController = rememberNavController()
    NavHost(navController, startDestination = startDestination) {
        composable<Read> {
            ReadScreen(
                onNavigateToSelection = { isSplitScreen ->
                    navController.navigate(Selection(isSplitScreen)) },
                onNavigateToSettings = { navController.navigate(Settings) },
                onNavigateToStart = { navController.navigate(Start) }
            )
        }
        composable<Selection> { backStackEntry ->
            val selection = backStackEntry.toRoute<Selection>()
            SelectionScreen(onNavigateToRead = { navController.navigate(Read) }, selection.isSplitScreen)
        }
        composable<Settings> {
            SettingsScreen(
                onNavigateToRead = { navController.navigate(Read) },
                onThemeChange = onThemeChange
            )
        }
        composable<Start> { StartScreen(onNavigateToRead = { navController.navigate(Read) }) }
    }
}