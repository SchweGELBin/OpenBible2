package com.schwegelbin.openbible

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.schwegelbin.openbible.ui.theme.OpenBibleTheme
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import java.io.File

@Serializable
data class Verse(
    val verse: String,
    val text: String
)

@Serializable
data class Chapter(
    val name: String,
    val verses: List<Verse>
)

@Serializable
data class Book(val chapters: List<Chapter>)

@Serializable
data class Bible(val books: List<Book>)

enum class Screen(val title: String) {
    Home("Home"),
    Read("Read")
}

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
    OpenBibleTheme {
        Surface(modifier = Modifier.fillMaxSize()){
            App()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val context = LocalContext.current
    val path = context.getExternalFilesDir("")
    if (!File("${path}/translations.json").exists() || !File("${path}/checksum.json").exists()) {
        saveIndex(context)
        saveChecksum(context)
    }

    val currentScreen = remember { mutableStateOf(Screen.Home) }
    val showSettings = remember { mutableStateOf(false) }

    if (showSettings.value){
        SettingsScreen(onClose = { showSettings.value = false })
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                      titleContentColor = MaterialTheme.colorScheme.primary
                    ),
                    title = { Text(stringResource(R.string.app_name)) },
                    actions = {
                        IconButton(onClick = { showSettings.value = true }) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "Settings"
                            )
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    Screen.entries.forEach { screen ->
                        NavigationBarItem(
                            icon = {
                                when (screen) {
                                    Screen.Home -> Icon(Icons.Filled.Home, contentDescription = "Home")
                                    Screen.Read -> Icon(Icons.Filled.Star, contentDescription = "Read")
                                    else -> {}
                                }
                            },
                            label = { Text(screen.title) },
                            selected = currentScreen.value == screen,
                            onClick = { currentScreen.value = screen },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            when (currentScreen.value) {
                Screen.Home -> HomeScreen(modifier = Modifier.padding(innerPadding))
                Screen.Read -> ReadScreen(modifier = Modifier.padding(innerPadding))
                else -> {}
            }
        }
    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Column {
        Text(text = "Home Screen", modifier = modifier)
        TranslationButton()
    }
}

@Composable
fun ReadScreen(modifier: Modifier = Modifier) {
    Text(text = "Read Screen", modifier = modifier)
    /*
    val context = LocalContext.current
    Column {
        Text(getChapter(context = context, abbrev = "schlachter", book = 18, chapter = 118).toString())
        Text(getChapter(context = context, abbrev = "schlachter", book = 18, chapter = 118).toString())
    }
    */
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onClose: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { onClose() }){
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column {
            Text("Settings Screen", modifier = Modifier.padding(innerPadding))
            IndexButton()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MainApp()
}

@Composable
fun IndexButton() {
    val context = LocalContext.current
    Button(onClick = {
        saveIndex(context)
    }){
        Text("Update Index")
    }
}

@Composable
fun TranslationButton() {
    val context = LocalContext.current
    Button(onClick = {
        downloadTranslation(context, "schlachter")
    }){
        Text("Download Schlachter Translation")
    }
}

private fun saveIndex(context: Context) {
    downloadFile(
        context = context,
        url = "https://api.getbible.net/v2/translations.json",
        name = "translations.json"
    )
}

private fun saveChecksum(context: Context) {
    val dir = context.getExternalFilesDir("")
    val path = "${dir}/checksum.json"
    File(path).delete()
    downloadFile(
        context = context,
        url = "https://api.getbible.net/v2/checksum.json",
        name = "checksum.json"
    )
}

private fun getChecksum(context: Context, abbrev: String): String? {
    val dir = context.getExternalFilesDir("")
    var path = "${dir}/checksum.json"
    var json = File(path).readText()
    var obj = Json.decodeFromString<JsonObject>(json)
    return obj[abbrev].toString()
}

private fun downloadTranslation(context: Context, abbrev: String) {
    val dir = context.getExternalFilesDir("")
    var path = "${dir}/${abbrev}.json"
    File(path).delete()
    downloadFile(
        context = context,
        url = String.format("https://api.getbible.net/v2/%s.json", abbrev),
        name = String.format("%s.json", abbrev)
    )
    path = "${dir}/${abbrev}.sum"
    var checksum = getChecksum(context, abbrev).toString()
    File(path).writeText(checksum)
}

private fun checkUpdate(context: Context, abbrev: String): Boolean {
    val dir = context.getExternalFilesDir("")
    val path = "${dir}/${abbrev}.sum"
    if (!File(path).exists()) return true
    var latest = getChecksum(context, abbrev)
    var current = File(path).readText()
    return latest != current
}

private fun getChapter(context: Context, abbrev: String, book: Int, chapter: Int): String? {
    val dir = context.getExternalFilesDir("")
    val path = "${dir}/${abbrev}.json"
    var text = ""
    var json = File(path).readText()
    var bible = Json.decodeFromString<Bible>(json)
    val verses = bible.books[book].chapters[chapter].verses
    verses.forEach { verse ->
        text += "${verse.verse} ${verse.text}\n"
    }
    return text
}

private fun getTitle(context: Context, abbrev: String, book: Int, chapter: Int): String? {
    val dir = context.getExternalFilesDir("")
    val path = "${dir}/${abbrev}.json"
    var json = File(path).readText()
    var bible = Json.decodeFromString<Bible>(json)
    val title = bible.books[book].chapters[chapter].name
    return "${abbrev}\n${title}"
}

private fun downloadFile(context: Context, url: String, name: String) {
    val request = DownloadManager.Request(Uri.parse(url)).apply {
        setTitle("Downloading $name")
        setDescription("Downloading $name")
        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        setDestinationInExternalFilesDir(context, "", name)
    }
    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    downloadManager.enqueue(request)
}