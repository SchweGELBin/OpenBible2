package com.schwegelbin.openbible.logic

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.exception.ZipException
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.model.enums.CompressionLevel
import net.lingala.zip4j.model.enums.CompressionMethod
import java.io.File
import java.io.FileOutputStream

fun downloadFile(
    context: Context,
    url: String,
    name: String,
    relPath: String = "",
    replace: Boolean = true,
    title: String = "Downloading File"
): Long {
    if (replace) File("${getExternalPath(context, relPath)}/${name}").delete()
    val notify =
        if (getDownloadNotification(context)) DownloadManager.Request.VISIBILITY_VISIBLE
        else DownloadManager.Request.VISIBILITY_HIDDEN
    val request = DownloadManager.Request(url.toUri()).apply {
        setTitle(title)
        setDescription("Downloading $name")
        setNotificationVisibility(notify)
        setDestinationInExternalFilesDir(context, relPath, name)
    }
    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    return downloadManager.enqueue(request)
}

fun downloadTranslation(context: Context, abbrev: String) {
    downloadFile(
        context = context,
        url = "https://api.getbible.net/v2/${abbrev}.json",
        name = "${abbrev}.json",
        title = "Downloading Translation"
    )
}

fun saveIndex(context: Context) {
    val file = getIndex(context)
    val currentTime = System.currentTimeMillis()
    val dayTime = 86_400_000L
    if (!file.exists() || currentTime - file.lastModified() > dayTime) {
        downloadFile(
            context = context,
            url = "https://api.getbible.net/v2/translations.json",
            name = "translations.json",
            title = "Downloading Index"
        )
    }
}

fun checkForUpdates(context: Context, install: Boolean): Boolean {
    var updateAvailable = false
    val installed = getTranslationList(context).map { it.nameWithoutExtension }
    val index = deserializeTranslations(getIndexPath(context)) ?: return false
    index.values.forEach { (_, abbrev, _, _, _, _, sha) ->
        if (installed.contains(abbrev)) {
            if (getTranslation(context, abbrev).getChecksum() != sha) {
                if (install) downloadTranslation(context, abbrev)
                updateAvailable = true
            }
        }
    }
    return updateAvailable
}

fun checkTranslation(
    context: Context,
    abbrev: String,
    onNavigateToStart: () -> Unit,
    isSplitScreen: Boolean
): String {
    if (!getTranslation(context, abbrev).exists()) {
        val list = getTranslationList(context).map { it.nameWithoutExtension }
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
    val userDir = getExternalPath(context)
    val dataDir = "${context.dataDir}/shared_prefs"
    val download = Environment.getExternalStoragePublicDirectory("Download")

    val parameters = ZipParameters().apply {
        compressionMethod = CompressionMethod.DEFLATE
        compressionLevel = CompressionLevel.NORMAL
    }

    if (user) {
        val zip = ZipFile("$download/OpenBible-Documents.zip")
        File(userDir).listFiles()?.forEach { file ->
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
        val dir = getExternalPath(context)
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val temp = File(dir, "temp.zip")
            FileOutputStream(temp).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            try {
                val zip = ZipFile(temp)
                zip.extractAll(File(dir).absolutePath)
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

    /* TODO: Restore Preferences
     * https://github.com/SchweGELBin/OpenBible2/issues/35
    else { }
    */
}

fun turnChapter(
    context: Context,
    next: Boolean,
    isSplitScreen: Boolean,
    onNavigateToRead: () -> Unit
) {
    var (translation, book, chapter) = getSelection(context, isSplitScreen)
    if (next) {
        val (bookCount, chapterCount) = getCount(context, translation, book)
        if (chapter < chapterCount) {
            chapter++
            onNavigateToRead()
        } else if (book < bookCount) {
            book++
            chapter = 0
            onNavigateToRead()
        }
    } else {
        if (chapter > 0) {
            chapter--
            onNavigateToRead()
        } else if (book > 0) {
            book--
            chapter = getCount(context, translation, book).second
            onNavigateToRead()
        }
    }
    saveSelection(context, book = book, chapter = chapter, isSplitScreen = isSplitScreen)
}

fun bytesToHex(bytes: ByteArray): String {
    val hexChars = CharArray(bytes.size * 2)
    for (j in bytes.indices) {
        val v = bytes[j].toInt() and 0xFF
        hexChars[j * 2] = "0123456789abcdef"[v ushr 4]
        hexChars[j * 2 + 1] = "0123456789abcdef"[v and 0x0F]
    }
    return String(hexChars)
}

fun fixLegacy(context: Context) {
    val path = getExternalPath(context)
    File("${path}/Index/translations.json").renameTo(File("${path}/translations.json"))
    File("${path}/Translations").listFiles()?.forEach { translation ->
        translation.renameTo(File("${path}/${translation.name}"))
    }
    File("${path}/Checksums").deleteRecursively()
    File("${path}/Index").deleteRecursively()
    File("${path}/Translations").deleteRecursively()
}