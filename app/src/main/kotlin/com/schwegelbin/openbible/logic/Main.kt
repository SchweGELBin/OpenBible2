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
    val safe = sanitizeAbbrev(abbrev)
    downloadFile(
        context = context,
        url = "https://api.getbible.life/v2/${safe}.json",
        name = "${safe}.json",
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
            url = "https://api.getbible.life/v2/translations.json",
            name = "translations.json",
            title = "Downloading Index"
        )
    }
}

fun checkForUpdates(context: Context, install: Boolean, translation: String? = null): Boolean {
    return getUpdateList(context, install, translation).isNotEmpty()
}

fun checkTranslation(
    context: Context,
    abbrev: String,
    onNavigateToStart: () -> Unit,
    isSplitScreen: Boolean
): String {
    if (!getTranslation(context, abbrev).exists() ||
        deserializeBible(getTranslationPath(context, abbrev)) == null
    ) {
        val list = getTranslationList(context).map { it.nameWithoutExtension }
        if (list.isNotEmpty()) {
            var newTranslation = abbrev
            for (item in list) {
                if (deserializeBible(getTranslationPath(context, item)) != null) {
                    newTranslation = item
                    break
                }
            }
            saveSelection(context, newTranslation, isSplitScreen = isSplitScreen)
            return newTranslation
        } else onNavigateToStart()
    }
    return abbrev
}

fun checkSelection(
    context: Context,
    selection: Triple<String, Int, Int>
): Triple<String, Int, Int> {
    var (abbrev, book, chapter) = selection
    val (books, chapters) = getCount(context, abbrev, book)
    book = book.coerceIn(0, books)
    chapter = chapter.coerceIn(0, chapters)
    return Triple(abbrev, book, chapter)
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
        val canonicalDir = File(dir).canonicalPath
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val temp = File(dir, "temp.zip")
            FileOutputStream(temp).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            try {
                val zip = ZipFile(temp)
                zip.fileHeaders.forEach { header ->
                    val targetFile = File(dir, header.fileName).canonicalFile
                    if (!targetFile.path.startsWith(canonicalDir)) {
                        throw SecurityException("Zip entry outside target dir: ${header.fileName}")
                    }
                }
                zip.extractAll(canonicalDir)
            } catch (e: ZipException) {
                e.printStackTrace()
            } catch (e: SecurityException) {
                e.printStackTrace()
            } finally {
                temp.delete()
                onFinished()
            }
        } ?: run {
            android.util.Log.e("OpenBible", "Failed to open input stream for restore.")
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

fun sanitizeAbbrev(abbrev: String?): String {
    return abbrev?.replace(Regex("[^a-zA-Z0-9_-]"), "") ?: return ""
}

fun searchText(context: Context, abbrev: String, query: String): List<Triple<String, Int, Int>> {
    val result = mutableListOf(Triple("", -1, -1))
    val bible = deserializeBible(getTranslationPath(context, abbrev)) ?: return result
    bible.books.forEachIndexed { bookIndex, book ->
        book.chapters.forEachIndexed { chapterIndex, chapter ->
            chapter.verses.forEach { (name, _, text) ->
                if (text.contains(query)) result += Triple(
                    "${name}\n${text}",
                    bookIndex,
                    chapterIndex
                )
            }
        }
    }
    return result
}

fun saveDeepLink(context: Context, book: String?, chapter: String?) {
    var bookInt = book?.toIntOrNull()
    if (bookInt != null && bookInt > 0) bookInt--
    val bookCount = getBookNames(context, getSelection(context, false).first).size
    val bookIndex =
        if (bookCount == 66) getBookAbbreviations().indexOfFirst { list -> list.contains(book) }
        else -1
    if (bookIndex >= 0) bookInt = bookIndex
    var chapterInt = chapter?.toIntOrNull()
    if (chapterInt == null) chapterInt = 0 else if (chapterInt > 0) chapterInt--
    saveSelection(context, book = bookInt, chapter = chapterInt, isSplitScreen = false)
}

fun setTranslation(context: Context, abbrev: String, isSplitScreen: Boolean): Triple<String, Int, Int> {
    var (translation, book, chapter) = getSelection(context, isSplitScreen)
    translation = abbrev

    val (bookCount, chapterCount) = getCount(
        context,
        translation,
        book
    )
    if (book > bookCount) {
        book = 0
        chapter = 0
    }
    if (chapter > chapterCount) {
        chapter = 0
    }
    saveSelection(
        context,
        translation,
        book,
        chapter,
        isSplitScreen
    )
    return Triple(translation, book, chapter)
}