package com.schwegelbin.openbible.logic

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.edit
import androidx.core.net.toUri
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.exception.ZipException
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.model.enums.CompressionLevel
import net.lingala.zip4j.model.enums.CompressionMethod
import java.io.File
import java.io.FileOutputStream


enum class SelectMode {
    Translation, Book, Chapter
}

enum class ThemeOption {
    System, Light, Dark, Amoled
}

enum class SchemeOption {
    Dynamic, Static
}

enum class ReadTextAlignment {
    Start, Justify
}

enum class SplitScreen {
    Off, Vertical, Horizontal
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
    File(
        "${context.getExternalFilesDir("Checksums")}/${abbrev}"
    ).writeText(getChecksum(context, abbrev))
}

fun checkUpdate(context: Context, abbrev: String): Boolean {
    if (!File(
            "${context.getExternalFilesDir("Translations")}/${abbrev}.json"
        ).exists()
    ) return true
    val path = "${context.getExternalFilesDir("Checksums")}/${abbrev}"
    if (!File(path).exists()) return true
    val latest = getChecksum(context, abbrev)
    val current = File(path).readText()
    return latest != current
}

fun downloadFile(
    context: Context, url: String, name: String, relPath: String = "", replace: Boolean = true
): Long {
    if (replace) File("${context.getExternalFilesDir(relPath)}/${name}").delete()
    val notify =
        if (getDownloadNotification(context)) DownloadManager.Request.VISIBILITY_VISIBLE
        else DownloadManager.Request.VISIBILITY_HIDDEN
    val request = DownloadManager.Request(url.toUri()).apply {
        setTitle("Downloading $name")
        setDescription("Downloading $name")
        setNotificationVisibility(notify)
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
    context.getSharedPreferences("selection", Context.MODE_PRIVATE).edit {
        if (!isSplitScreen) {
            if (translation != null) putString("translation", translation)
            if (book != null) putInt("book", book)
            if (chapter != null) putInt("chapter", chapter)
        } else {
            if (translation != null) putString("translation_split", translation)
            if (book != null) putInt("book_split", book)
            if (chapter != null) putInt("chapter_split", chapter)
        }
    }
}

fun saveColorScheme(
    context: Context,
    theme: ThemeOption? = null,
    scheme: SchemeOption? = null
) {
    context.getSharedPreferences("options", Context.MODE_PRIVATE).edit {
        if (theme != null) putString("theme", theme.toString())
        if (scheme != null) putString("scheme", scheme.toString())
    }
}

fun saveTextStyle(context: Context, alignment: ReadTextAlignment) {
    context.getSharedPreferences("options", Context.MODE_PRIVATE).edit {
        putString("textAlignment", alignment.toString())
    }
}

fun saveShowVerseNumbers(context: Context, shown: Boolean) {
    context.getSharedPreferences("options", Context.MODE_PRIVATE).edit {
        putBoolean("showVerseNumbers", shown)
    }
}

fun saveCheckAtStartup(context: Context, check: Boolean) {
    context.getSharedPreferences("options", Context.MODE_PRIVATE).edit {
        putBoolean("checkAtStartup", check)
    }
}

fun saveSplitScreen(context: Context, split: SplitScreen) {
    context.getSharedPreferences("options", Context.MODE_PRIVATE).edit {
        putString("split", split.toString())
    }
}

fun saveDownloadNotification(context: Context, enabled: Boolean) {
    context.getSharedPreferences("options", Context.MODE_PRIVATE).edit {
        putBoolean("notifyDownload", enabled)
    }
}

fun saveNewIndex(context: Context) {
    val path = context.getExternalFilesDir("Index")
    val translationsFile = File("${path}/translations.json")
    val checksumFile = File("${path}/checksum.json")
    val currentTime = System.currentTimeMillis()
    val dayTime = 86_400_000L
    if (
        !translationsFile.exists() ||
        !checksumFile.exists() ||
        currentTime - translationsFile.lastModified() > dayTime ||
        currentTime - checksumFile.lastModified() > dayTime
    ) {
        saveIndex(context)
        saveChecksum(context)
    }
}

fun checkForUpdates(context: Context, update: Boolean): Boolean {
    var updateAvailable = false
    cleanUpTranslations(context)
    getList(context, "Checksums").map { it.name }.forEach { abbrev ->
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
    if (!File("${context.getExternalFilesDir("Translations")}/${abbrev}.json").exists()) {
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

fun backupData(context: Context, user: Boolean = false, data: Boolean = false) {
    val userDir = context.getExternalFilesDir("")
    val dataDir = "${context.dataDir}/shared_prefs"
    val download = Environment.getExternalStoragePublicDirectory("Download")

    val parameters = ZipParameters().apply {
        compressionMethod = CompressionMethod.DEFLATE
        compressionLevel = CompressionLevel.NORMAL
    }

    if (user) {
        val zip = ZipFile("$download/OpenBible-Documents.zip")
        userDir?.listFiles()?.forEach { file ->
            if (file.isDirectory) zip.addFolder(file, parameters)
            else zip.addFile(file, parameters)
        }
    }

    if (data) {
        val zip = ZipFile("$download/OpenBible-Preferences.zip")
        zip.addFiles(File(dataDir).listFiles()?.toList(), parameters)
    }
}

fun restoreBackup(context: Context, uri: Uri, user: Boolean, onFinished: () -> Unit) {
    if (user) {
        val dir = context.getExternalFilesDir("")
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val temp = File(dir, "temp.zip")
            FileOutputStream(temp).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            try {
                val zip = ZipFile(temp)
                zip.extractAll(dir?.absolutePath)
            } catch (e: ZipException) {
                e.printStackTrace()
            } finally {
                temp.delete()
                onFinished()
            }
        } ?: run {
            println("Failed to open input stream.")
        }
    }

    if (!user) {
        // TODO: Restore Preferences
    }
}