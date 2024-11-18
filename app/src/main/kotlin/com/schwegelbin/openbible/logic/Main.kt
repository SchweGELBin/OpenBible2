package com.schwegelbin.openbible.logic

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import java.io.File

enum class Screen() {
    Home, Read
}

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

var selectedBook = 42
var selectedChapter = 2
var selectedTranslation = "schlachter"

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
    val dir = context.getExternalFilesDir("Checksums")
    val path = "${dir}/${abbrev}"
    if (!File(path).exists()) return true
    val latest = getChecksum(context, abbrev)
    val current = File(path).readText()
    return latest != current
}

fun downloadFile(
    context: Context, url: String, name: String, relPath: String = "", replace: Boolean = true
) {
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
    downloadManager.enqueue(request)
}

fun saveSelection(context: Context) {
    val sharedPref = context.getSharedPreferences("selection", Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    editor.putString("translation", selectedTranslation)
    editor.putInt("book", selectedBook)
    editor.putInt("chapter", selectedChapter)
    editor.apply()
}

fun saveColorScheme(context: Context, theme: ThemeOption?, scheme: SchemeOption?) {
    val sharedPref = context.getSharedPreferences("colorscheme", Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    if (theme != null) editor.putString("theme", theme.toString())
    if (scheme != null) editor.putString("scheme", scheme.toString())
    editor.apply()
}

fun saveReadTextStyle(context: Context, textAlignment: ReadTextAlignment) {
    val sharedPref = context.getSharedPreferences("readTextStyle", Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    editor.putString("textAlignment", textAlignment.toString())
    editor.apply()
}