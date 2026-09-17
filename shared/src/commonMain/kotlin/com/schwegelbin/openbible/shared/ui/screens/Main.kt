package com.schwegelbin.openbible.shared.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.schwegelbin.openbible.shared.getContext
import com.schwegelbin.openbible.shared.logic.Bible
import com.schwegelbin.openbible.shared.logic.checkForUpdates
import com.schwegelbin.openbible.shared.logic.deserializeBible
import com.schwegelbin.openbible.shared.logic.getCheckAtStartup
import com.schwegelbin.openbible.shared.logic.getIndex
import com.schwegelbin.openbible.shared.logic.getTranslationList
import com.schwegelbin.openbible.shared.logic.saveDeepLink
import kotlinx.serialization.Serializable

@Serializable
object Bookmarks

@Serializable
data class Read(
    val book: String? = null,
    val chapter: String? = null,
    val verse: String? = null
)

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

object BibleCache {
    private val cache = mutableMapOf<String, Bible>()

    fun getBible(path: String): Bible? {
        return cache[path] ?: run {
            val bible = deserializeBible(path)
            if (bible != null) cache[path] = bible
            bible
        }
    }
}

@Composable
fun App(onThemeChange: (Boolean?, Boolean?, Boolean?) -> Unit) {
    val context = getContext()
    val startDestination = if (!getIndex(context).exists() ||
        getTranslationList(context).isEmpty() ||
        (getCheckAtStartup(context) && checkForUpdates(context, false))
    ) Start else Read()

    val navController = rememberNavController()
    NavHost(navController, startDestination = startDestination) {
        composable<Bookmarks> {
            BookmarksScreen(onNavigateToRead = {
                navController.navigate(Read()) {
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
            val route = backStackEntry.toRoute<Read>()
            if (route.book != null) saveDeepLink(
                context,
                book = route.book,
                chapter = route.chapter
            )
            ReadScreen(
                onNavigateToBookmarks = { navController.navigate(Bookmarks) },
                onNavigateToRead = {
                    navController.navigate(Read()) {
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
                navController.navigate(Read()) {
                    popUpTo(0) { inclusive = true }
                }
            })
        }
        composable<Selection> { backStackEntry ->
            val route = backStackEntry.toRoute<Selection>()
            SelectionScreen(
                onNavigateToRead = {
                    navController.navigate(Read()) {
                        popUpTo(0) { inclusive = true }
                    }
                }, route.isSplitScreen, route.initialIndex
            )
        }
        composable<Settings> {
            SettingsScreen(
                onNavigateToRead = {
                    navController.navigate(Read()) {
                        popUpTo(0) { inclusive = true }
                    }
                }, onThemeChange = onThemeChange
            )
        }
        composable<Start> {
            StartScreen(onNavigateToRead = {
                navController.navigate(Read()) {
                    popUpTo(0) { inclusive = true }
                }
            })
        }
    }
}