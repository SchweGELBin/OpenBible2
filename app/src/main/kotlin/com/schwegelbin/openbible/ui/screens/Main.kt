package com.schwegelbin.openbible.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.schwegelbin.openbible.logic.checkForUpdates
import com.schwegelbin.openbible.logic.fixLegacy
import com.schwegelbin.openbible.logic.getCheckAtStartup
import com.schwegelbin.openbible.logic.getIndex
import com.schwegelbin.openbible.logic.getTranslationList
import kotlinx.serialization.Serializable

@Serializable
object Bookmarks

@Serializable
object Read

@Serializable
object Search

@Serializable
data class Selection(val isSplitScreen: Boolean)

@Serializable
object Settings

@Serializable
object Start

@Composable
fun App(onThemeChange: (Boolean?, Boolean?, Boolean?) -> Unit) {
    val context = LocalContext.current
    fixLegacy(context)
    val startDestination: Any =
        if (!getIndex(context).exists() ||
            getTranslationList(context).isEmpty() ||
            (getCheckAtStartup(context) && checkForUpdates(context, false))
        ) Start else Read

    val navController = rememberNavController()
    NavHost(navController, startDestination = startDestination) {
        composable<Bookmarks> {
            BookmarksScreen(onNavigateToRead = {
                navController.navigate(Read) {
                    popUpTo(0) { inclusive = true }
                }
            })
        }
        composable<Read> {
            ReadScreen(
                onNavigateToBookmarks = { navController.navigate(Bookmarks) },
                onNavigateToRead = {
                    navController.navigate(Read) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToSearch = { navController.navigate(Search) },
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
        composable<Search> {
            SearchScreen(onNavigateToRead = {
                navController.navigate(Read) {
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