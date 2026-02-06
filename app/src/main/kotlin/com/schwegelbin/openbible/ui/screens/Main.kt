package com.schwegelbin.openbible.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.schwegelbin.openbible.logic.checkForUpdates
import com.schwegelbin.openbible.logic.fixLegacy
import com.schwegelbin.openbible.logic.getCheckAtStartup
import com.schwegelbin.openbible.logic.getIndex
import com.schwegelbin.openbible.logic.getTranslationList
import com.schwegelbin.openbible.logic.saveDeepLink
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

@Serializable
object Bookmarks

@Serializable
object Read

@Serializable
object Search

@Serializable
data class Selection(
    val isSplitScreen: Boolean, val initialIndex: Int
)

@Serializable
object Settings

@Serializable
object Start

@Composable
fun App(onThemeChange: (Boolean?, Boolean?, Boolean?) -> Unit) {
    val context = LocalContext.current
    val startDestination = remember { mutableStateOf<Any?>(null) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            fixLegacy(context)
        }
        val dest: Any = withContext(Dispatchers.IO) {
            if (!getIndex(context).exists() ||
                getTranslationList(context).isEmpty() ||
                (getCheckAtStartup(context) && checkForUpdates(context, false))
            ) Start else Read
        }
        startDestination.value = dest
    }

    val dest = startDestination.value ?: return

    val navController = rememberNavController()
    NavHost(navController, startDestination = dest) {
        composable<Bookmarks> {
            BookmarksScreen(onNavigateToRead = {
                navController.navigate(Read) {
                    popUpTo(0) { inclusive = true }
                }
            })
        }
        composable<Read>(
            deepLinks = listOf(
                navDeepLink { uriPattern = "openbible://{book}" },
                navDeepLink { uriPattern = "openbible://{book}/{chapter}" },
                navDeepLink { uriPattern = "openbible://{book}/{chapter}/{verse}" }
            )
        ) { backStackEntry ->
            val book = backStackEntry.arguments?.getString("book")
            val chapter = backStackEntry.arguments?.getString("chapter")
            if (book != null) saveDeepLink(
                context,
                book = book,
                chapter = chapter
            )
            ReadScreen(
                onNavigateToBookmarks = { navController.navigate(Bookmarks) },
                onNavigateToRead = {
                    navController.navigate(Read) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToSearch = { navController.navigate(Search) },
                onNavigateToSelection = { isSplitScreen, initialIndex ->
                    navController.navigate(Selection(isSplitScreen, initialIndex))
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
            val route = backStackEntry.toRoute<Selection>()
            SelectionScreen(
                onNavigateToRead = {
                    navController.navigate(Read) {
                        popUpTo(0) { inclusive = true }
                    }
                }, route.isSplitScreen, route.initialIndex
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