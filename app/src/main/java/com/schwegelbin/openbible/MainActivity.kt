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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.schwegelbin.openbible.ui.theme.OpenBibleTheme
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.listDirectoryEntries

// Json Serializable data
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
data class Book(
    val name: String,
    val chapters: List<Chapter>
)

@Serializable
data class Bible(
    val books: List<Book>,
    val translation: String
)

@Serializable
data class Translation(
    val translation: String,
    val abbreviation: String,
    val lang: String
)


// Enums
enum class Screen(val title: String) {
    Home("Home"),
    Read("Read")
}

enum class SelectMode() {
    Translation,
    Book,
    Chapter
}

// Default Global Variables
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
    getDefaultFiles(context)

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
        SelectCard(SelectMode.Translation)
        SelectCard(SelectMode.Book)
        SelectCard(SelectMode.Chapter)
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
    var showDialog = remember { mutableStateOf(false) }
    val indexPath = "${context.getExternalFilesDir("Index")}/translations.json"

    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            if (File(indexPath).exists()) showDialog.value = true
        }
    ) {
        Text(
            text = stringResource(R.string.download_translation),
            modifier = Modifier.padding(16.dp)
        )
    }

    if (showDialog.value) {
        val translationMap = remember { getTranslations(context) }
        val translationItems = translationMap?.values?.map {
            it.abbreviation to it.translation
        }

        Dialog(
            onDismissRequest = { showDialog.value = false }
        ) {
            Surface(
                shape = RoundedCornerShape(size = 40.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = stringResource(R.string.download_translation),
                        style = MaterialTheme.typography.titleLarge
                    )

                    Card(
                        modifier = Modifier.verticalScroll(state = rememberScrollState(), enabled = true),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        translationItems?.forEach { (abbreviation, translation) ->
                            TextButton(
                                onClick = {
                                    downloadTranslation(context, abbreviation)
                                    showDialog.value = false
                                }
                            ) {
                                Text("$abbreviation | $translation")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SelectCard(selectMode: SelectMode) {
    val context = LocalContext.current
    var showDialog = remember { mutableStateOf(false) }
    val translationPath = "${context.getExternalFilesDir("Translations")}/${selectedTranslation}.json"

    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            if (Path(translationPath).exists()) showDialog.value = true
        }
    ) {
        when (selectMode) {
            SelectMode.Translation -> {
                Text(
                    text = stringResource(R.string.choose_translation),
                    modifier = Modifier.padding(16.dp)
                )
            }

            SelectMode.Book -> {
                Text(
                    text = stringResource(R.string.choose_book),
                    modifier = Modifier.padding(16.dp)
                )
            }

            SelectMode.Chapter -> {
                Text(
                    text = stringResource(R.string.choose_chapter),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }

    if (showDialog.value) {
        Dialog(
            onDismissRequest = { showDialog.value = false }
        ) {
            Surface(
                shape = RoundedCornerShape(size = 40.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    when (selectMode) {
                        SelectMode.Translation -> {
                            Text(
                                text = stringResource(R.string.choose_translation),
                                style = MaterialTheme.typography.titleLarge
                            )
                            val translationList = Path(
                                context.getExternalFilesDir("Checksums").toString()
                            ).listDirectoryEntries()
                            val translationMap = getTranslations(context)

                            Card(
                                modifier = Modifier.verticalScroll(state = rememberScrollState(), enabled = true),
                                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                            ) {
                                translationList.forEach { abbreviation ->
                                    val abbrev = abbreviation.fileName.toString()
                                    val name = translationMap?.get(abbrev)?.translation
                                    TextButton(
                                        onClick = {
                                            selectedTranslation = abbrev
                                            showDialog.value = false
                                        }
                                    ) {
                                        Text("$abbrev | $name")
                                    }
                                }
                            }
                        }

                        SelectMode.Book -> {
                            Text(
                                text = stringResource(R.string.choose_book),
                                style = MaterialTheme.typography.titleLarge
                            )
                            val names = getBookNames(context, selectedTranslation)

                            Card(
                                modifier = Modifier.verticalScroll(state = rememberScrollState(), enabled = true),
                                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                            ) {
                                for (i in 0..names.size - 1) {
                                    val name = names[i]
                                    TextButton(
                                        onClick = {
                                            selectedBook = i
                                            showDialog.value = false
                                        }
                                    ) {
                                        Text(name)
                                    }
                                }
                            }
                        }

                        SelectMode.Chapter -> {
                            Text(
                                text = stringResource(R.string.choose_chapter),
                                style = MaterialTheme.typography.titleLarge
                            )
                            val num = getChapterNumber(context, selectedTranslation, selectedBook)

                            Card(
                                modifier = Modifier.verticalScroll(state = rememberScrollState(), enabled = true),
                                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                            ) {
                                for (i in 0..num) {
                                    val name = (i + 1).toString()
                                    TextButton(
                                        onClick = {
                                            selectedChapter = i
                                            showDialog.value = false
                                        }
                                    ) {
                                        Text(name)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
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

private fun getTranslations(context: Context): Map<String, Translation>? {
    val dir = context.getExternalFilesDir("Index")
    val path = "${dir}/translations.json"
    if (!File(path).exists()) {
        return null
    }
    val json = File(path).readText()
    val withUnknownKeys = Json { ignoreUnknownKeys = true; }
    val translations: Map<String, Translation> = withUnknownKeys.decodeFromString(json)
    return translations
}

private fun getChecksum(context: Context, abbrev: String): String? {
    val dir = context.getExternalFilesDir("Index")
    val path = "${dir}/checksum.json"
    if (File(path).exists()) {
        var json = File(path).readText()
        var obj = Json.decodeFromString<JsonObject>(json)
        return obj[abbrev].toString()
    } else return "unknown"
}

private fun getChapterNumber(context: Context, abbrev: String, book: Int): Int {
    val dir = context.getExternalFilesDir("Translations")
    val path = "${dir}/${abbrev}.json"
    val json = File(path).readText()
    val withUnknownKeys = Json { ignoreUnknownKeys = true; }
    val obj = withUnknownKeys.decodeFromString<Bible>(json)
    return obj.books[book].chapters.size - 1
}

private fun getBookNames(context: Context, abbrev: String): Array<String> {
    val dir = context.getExternalFilesDir("Translations")
    val path = "${dir}/${abbrev}.json"
    val json = File(path).readText()
    val withUnknownKeys = Json { ignoreUnknownKeys = true; }
    val obj = withUnknownKeys.decodeFromString<Bible>(json)
    val num = obj.books.size
    var arr = Array<String>(num) { "" }
    for (i in 0..num-1) {
        arr[i] = obj.books[i].name
    }
    return arr
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
    val path = "${dir}/${abbrev}"
    File(path).writeText(checksum)
}

private fun checkUpdate(context: Context, abbrev: String): Boolean {
    val dir = context.getExternalFilesDir("Checksums")
    val path = "${dir}/${abbrev}"
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
    if (File(path).exists()) {
        var json = File(path).readText()
        var bible = withUnknownKeys.decodeFromString<Bible>(json)
        val verses = bible.books[book].chapters[chapter].verses
        verses.forEach { verse ->
            text += "${verse.verse} ${verse.text}\n"
        }
    }
    return text
}

private fun getTitle(context: Context, abbrev: String, book: Int, chapter: Int): String? {
    val dir = context.getExternalFilesDir("Translations")
    val path = "${dir}/${abbrev}.json"
    val withUnknownKeys = Json { ignoreUnknownKeys = true; }
    if (!File(path).exists()) return "ERROR"
    var json = File(path).readText()
    var bible = withUnknownKeys.decodeFromString<Bible>(json)
    val translation = bible.translation
    val title = bible.books[book].chapters[chapter].name
    return "$translation | $title"
}

private fun getDefaultFiles(context: Context, defaultTranslation: String = selectedTranslation) {
    selectedTranslation = defaultTranslation
    var path = context.getExternalFilesDir("Index")
    if (!File("${path}/translations.json").exists() || !File("${path}/checksum.json").exists()) {
        saveIndex(context)
        saveChecksum(context)
    }
    path = context.getExternalFilesDir("Translations")
    if (!File("${path}/${defaultTranslation}.json").exists()) downloadTranslation(context, defaultTranslation)
}

private fun downloadFile(
    context: Context,
    url: String,
    name: String,
    relPath: String = "",
    replace: Boolean = true
) {
    if (replace) {
        var dir = context.getExternalFilesDir(relPath)
        var path = "${dir}/${name}"
        if (File(path).exists()) File(path).delete()
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