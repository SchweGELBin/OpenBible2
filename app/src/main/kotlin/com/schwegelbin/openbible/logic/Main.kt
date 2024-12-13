package com.schwegelbin.openbible.logic

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import java.io.File

enum class SelectMode() {
    Translation, Book, Chapter
}

enum class ThemeOption() {
    System, Light, Dark, Amoled
}

enum class SchemeOption() {
    Dynamic, Static
}

enum class ReadTextAlignment() {
    Start, Justify
}

fun saveIndex(context: Context) {
    downloadFile(
        context = context,
        url = "https://api.getbible.net/v2/translations.json",
        name = "translations.json",
        relPath = "Index"
    )
}

fun saveChecksum(context: Context) {
    downloadFile(
        context = context,
        url = "https://api.getbible.net/v2/checksum.json",
        name = "checksum.json",
        relPath = "Index"
    )
}

fun downloadTranslation(context: Context, abbrev: String) {
    downloadFile(
        context = context,
        url = "https://api.getbible.net/v2/${abbrev}.json",
        name = "${abbrev}.json",
        relPath = "Translations"
    )
    val checksum = getChecksum(context, abbrev)
    val dir = context.getExternalFilesDir("Checksums")
    val path = "${dir}/${abbrev}"
    File(path).writeText(checksum)
}

fun checkUpdate(context: Context, abbrev: String): Boolean {
    var dir = context.getExternalFilesDir("Translations")
    var path = "${dir}/${abbrev}.json"
    if (!File(path).exists()) return true
    dir = context.getExternalFilesDir("Checksums")
    path = "${dir}/${abbrev}"
    if (!File(path).exists()) return true
    val latest = getChecksum(context, abbrev)
    val current = File(path).readText()
    return latest != current
}

fun downloadFile(
    context: Context, url: String, name: String, relPath: String = "", replace: Boolean = true
): Long {
    if (replace) {
        val dir = context.getExternalFilesDir(relPath)
        val path = "${dir}/${name}"
        File(path).delete()
    }
    val request = DownloadManager.Request(Uri.parse(url)).apply {
        setTitle("Downloading $name")
        setDescription("Downloading $name")
        setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
        setDestinationInExternalFilesDir(context, relPath, name)
    }
    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    return downloadManager.enqueue(request)
}

fun saveSelection(
    context: Context,
    translation: String? = null,
    book: Int? = null,
    chapter: Int? = null,
    isSplitScreen: Boolean
) {
    val sharedPref = context.getSharedPreferences("selection", Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    if (!isSplitScreen) {
        if (translation != null) editor.putString("translation", translation)
        if (book != null) editor.putInt("book", book)
        if (chapter != null) editor.putInt("chapter", chapter)
    } else {
        if (translation != null) editor.putString("translation_split", translation)
        if (book != null) editor.putInt("book_split", book)
        if (chapter != null) editor.putInt("chapter_split", chapter)
    }
    editor.apply()
}

fun saveColorScheme(
    context: Context,
    theme: ThemeOption? = null,
    scheme: SchemeOption? = null
) {
    val sharedPref = context.getSharedPreferences("options", Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    if (theme != null) editor.putString("theme", theme.toString())
    if (scheme != null) editor.putString("scheme", scheme.toString())
    editor.apply()
}

fun saveTextStyle(context: Context, alignment: ReadTextAlignment) {
    val sharedPref = context.getSharedPreferences("options", Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    editor.putString("textAlignment", alignment.toString())
    editor.apply()
}

fun saveShowVerseNumbers(context: Context, shown: Boolean) {
    val sharedPref = context.getSharedPreferences("options", Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    editor.putBoolean("showVerseNumbers", shown)
    editor.apply()
}

fun saveCheckAtStartup(context: Context, check: Boolean) {
    val sharedPref = context.getSharedPreferences("options", Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    editor.putBoolean("checkAtStartup", check)
    editor.apply()
}

fun saveSplitScreen(context: Context, enabled: Boolean) {
    val sharedPref = context.getSharedPreferences("options", Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    editor.putBoolean("splitScreen", enabled)
    editor.apply()
}

fun saveNewIndex(context: Context) {
    var path = context.getExternalFilesDir("Index")
    val translationsFile = File("${path}/translations.json")
    val checksumFile = File("${path}/checksum.json")
    if (
        !translationsFile.exists() ||
        !checksumFile.exists() ||
        System.currentTimeMillis() - translationsFile.lastModified() > 86400000L ||
        System.currentTimeMillis() - checksumFile.lastModified() > 86400000L
    ) {
        saveIndex(context)
        saveChecksum(context)
    }
}

fun checkForUpdates(context: Context, update: Boolean): Boolean {
    var updateAvailable = false
    cleanUpTranslations(context)
    val translationList = getList(context, "Checksums").map { it.name }
    translationList.forEach { abbrev ->
        if (checkUpdate(context, abbrev)) {
            if (update) downloadTranslation(context, abbrev)
            updateAvailable = true
        }
    }
    return updateAvailable
}

fun cleanUpTranslations(context: Context) {
    val checksums = getList(context, "Checksums").map { it.name }
    val translations = getList(context, "Translations").map { it.nameWithoutExtension }
    checksums.forEach { sum ->
        if (sum !in translations) File("${context.getExternalFilesDir("Checksums")}/${sum}").delete()
    }
    translations.forEach { abbrev ->
        if (abbrev !in checksums) File("${context.getExternalFilesDir("Checksums")}/${abbrev}").writeText(
            "unknown"
        )
    }
}

fun checkTranslation(
    context: Context,
    abbrev: String,
    onNavigateToStart: () -> Unit,
    isSplitScreen: Boolean
): String {
    val dir = context.getExternalFilesDir("Translations")
    if (!File("${dir}/${abbrev}.json").exists()) {
        val list = getList(context, "Translations").map { it.nameWithoutExtension }
        if (list.isNotEmpty()) {
            val newTranslation = list.first()
            saveSelection(context, newTranslation, isSplitScreen = isSplitScreen)
            return newTranslation
        } else onNavigateToStart()
    }
    return abbrev
}

fun shorten(str: String, max: Int): String {
    return if (max >= 2 && str.length > max)
        str.substring(0, max - 1).trim() + '.'
    else str
}