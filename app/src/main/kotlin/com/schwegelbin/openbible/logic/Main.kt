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

const val defaultTranslation = "schlachter"
const val defaultBook = 42
const val defaultChapter = 2

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
    translation: String,
    book: Int,
    chapter: Int
) {
    val sharedPref = context.getSharedPreferences("selection", Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    editor.putString("translation", translation)
    editor.putInt("book", book)
    editor.putInt("chapter", chapter)
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

fun saveNewIndex(context: Context) {
    var path = context.getExternalFilesDir("Index")
    if (!File("${path}/translations.json").exists() || !File("${path}/checksum.json").exists()) {
        saveIndex(context)
        saveChecksum(context)
    }
}