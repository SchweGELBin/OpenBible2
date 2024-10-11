package com.schwegelbin.openbible

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.schwegelbin.openbible.ui.theme.OpenBibleTheme
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import java.io.File

@Serializable
data class Verse(
    val verse: Int,
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
data class Bible(
    val books: List<Book>,
    val translation: String
)

enum class Screen(val title: String) {
    Home("Home"),
    Read("Read")
}

var selectedBook = 42
var selectedChapter = 2
var selectedTranslation = "schlachter"


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
        Surface(modifier = Modifier.fillMaxSize()) {
            App()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val context = LocalContext.current
    val dir = context.getExternalFilesDir("")
    val path = "${dir}/Index"
    if (!File("${path}/translations.json").exists() || !File("${path}/checksum.json").exists()) {
        saveIndex(context)
        saveChecksum(context)
    }

    val currentScreen = remember { mutableStateOf(Screen.Home) }
    val showSettings = remember { mutableStateOf(false) }

    if (showSettings.value) {
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
                                    Screen.Home -> Icon(
                                        Icons.Filled.Home,
                                        contentDescription = stringResource(R.string.screen_home)
                                    )
                                    Screen.Read -> Icon(
                                        Icons.Filled.Star,
                                        contentDescription = stringResource(R.string.screen_read)
                                    )
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
    Column(
        modifier = modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        TranslationCard()
        SelectCard()
    }
}

@Composable
fun ReadScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column(
        modifier = modifier.padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = getTitle(
                    context = context,
                    abbrev = selectedTranslation,
                    book = selectedBook,
                    chapter = selectedChapter
                ).toString(),
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center
            )
        }
        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            modifier = Modifier
                .verticalScroll(state = rememberScrollState(), enabled = true)
                .fillMaxWidth()
        ) {
            Text(
                text = getChapter(
                    context = context,
                    abbrev = selectedTranslation,
                    book = selectedBook,
                    chapter = selectedChapter
                ).toString(),
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Justify
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onClose: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = { onClose() }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.close)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            IndexButton()
            LocaleButton()
            ThemeButton()
            AccentButton()
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
        saveChecksum(context)
    }) {
        Text(stringResource(R.string.update_index))
    }
}

@Composable
fun LocaleButton() {
    Button(onClick = {
        //TODO: Open Dialog to change locale/language
    }) {
        Text(stringResource(R.string.change_locale))
    }
}

@Composable
fun ThemeButton() {
    Button(onClick = {
        //TODO: Open Dialog to change theme (Light/Dark/Amoled)
    }) {
        Text(stringResource(R.string.change_theme))
    }
}

@Composable
fun AccentButton() {
    Button(onClick = {
        //TODO: Open Dialog to change accent color (System/Red/Blue/...)
    }) {
        Text(stringResource(R.string.change_accent))
    }
}

@Composable
fun TranslationCard() {
    val context = LocalContext.current
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth(),
        onClick = {
            // TODO: Open Dialog to chose translation
            downloadTranslation(context, "schlachter")
        }
    ) {
        Text(
            text = stringResource(R.string.download_translation),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun SelectCard() {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth(),
        onClick = {
            // TODO: Open Dialog to chose a book and chapter
        }
    ) {
        Text(
            text = stringResource(R.string.choose_book_chapter),
            modifier = Modifier.padding(16.dp)
        )
    }
}

private fun saveIndex(context: Context) {
    downloadFile(
        context = context,
        url = "https://api.getbible.net/v2/translations.json",
        name = "translations.json",
        relPath = "Index"
    )
}

private fun saveChecksum(context: Context) {
    downloadFile(
        context = context,
        url = "https://api.getbible.net/v2/checksum.json",
        name = "checksum.json",
        relPath = "Index"
    )
}

private fun getChecksum(context: Context, abbrev: String): String? {
    val dir = context.getExternalFilesDir("Index")
    var path = "${dir}/checksum.json"
    var json = File(path).readText()
    var obj = Json.decodeFromString<JsonObject>(json)
    return obj[abbrev].toString()
}

private fun downloadTranslation(context: Context, abbrev: String) {
    downloadFile(
        context = context,
        url = "https://api.getbible.net/v2/${abbrev}.json",
        name = "${abbrev}.json",
        relPath = "Translations"
    )
    var checksum = getChecksum(context, abbrev).toString()
    val dir = context.getExternalFilesDir("Checksums")
    val path = "${dir}/${abbrev}.sum"
    File(path).writeText(checksum)
}

private fun checkUpdate(context: Context, abbrev: String): Boolean {
    val dir = context.getExternalFilesDir("Checksums")
    val path = "${dir}/${abbrev}.sum"
    if (!File(path).exists()) return true
    var latest = getChecksum(context, abbrev)
    var current = File(path).readText()
    return latest != current
}

private fun getChapter(context: Context, abbrev: String, book: Int, chapter: Int): String? {
    val dir = context.getExternalFilesDir("Translations")
    val path = "${dir}/${abbrev}.json"
    val withUnknownKeys = Json { ignoreUnknownKeys = true; }
    var text = ""
    var json = File(path).readText()
    var bible = withUnknownKeys.decodeFromString<Bible>(json)
    val verses = bible.books[book].chapters[chapter].verses
    verses.forEach { verse ->
        text += "${verse.verse} ${verse.text}\n"
    }
    return text
}

private fun getTitle(context: Context, abbrev: String, book: Int, chapter: Int): String? {
    val dir = context.getExternalFilesDir("Translations")
    val path = "${dir}/${abbrev}.json"
    val withUnknownKeys = Json { ignoreUnknownKeys = true; }
    var json = File(path).readText()
    var bible = withUnknownKeys.decodeFromString<Bible>(json)
    val translation = bible.translation
    val title = bible.books[book].chapters[chapter].name
    return "$translation | $title"
}

private fun downloadFile(context: Context, url: String, name: String, relPath: String = "", replace: Boolean = true) {
    if (replace) {
        var dir = context.getExternalFilesDir(relPath)
        var path = "${dir}/${name}"
        File(path).delete()
    }
    val request = DownloadManager.Request(Uri.parse(url)).apply {
        setTitle("Downloading $name")
        setDescription("Downloading $name")
        setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
        setDestinationInExternalFilesDir(context, relPath, name)
    }
    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    downloadManager.enqueue(request)
}