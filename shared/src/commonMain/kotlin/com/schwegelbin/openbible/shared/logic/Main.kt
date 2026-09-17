package com.schwegelbin.openbible.shared.logic

import com.schwegelbin.openbible.shared.PlatformFile
import com.schwegelbin.openbible.shared.copyUriToFile
import com.schwegelbin.openbible.shared.downloadFile
import com.schwegelbin.openbible.shared.getDownloadsDir
import com.schwegelbin.openbible.shared.getExternalPath
import com.schwegelbin.openbible.shared.getPrefsDir
import com.schwegelbin.openbible.shared.showToast
import com.schwegelbin.openbible.shared.ui.screens.BibleCache.getBible
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.exception.ZipException
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.model.enums.CompressionLevel
import net.lingala.zip4j.model.enums.CompressionMethod
import java.io.File

fun downloadTranslation(context: Any?, abbrev: String) {
    val safe = sanitizeAbbrev(abbrev)
    downloadFile(
        context = context,
        url = "https://api.getbible.net/v2/${safe}.json",
        name = "${safe}.json",
        title = "Downloading Translation"
    )
}

fun saveIndex(context: Any?) {
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

fun checkForUpdates(context: Any?, install: Boolean, translation: String? = null): Boolean =
    getUpdateList(context, install, translation).isNotEmpty()

fun checkSelection(
    context: Any?,
    selection: Triple<String, Int, Int>
): Triple<String, Int, Int> {
    var (abbrev, book, chapter) = selection
    val (books, chapters) = getCount(context, abbrev, book)
    if (book > books) {
        book = 0
        chapter = 0
    }
    if (chapter > chapters) {
        chapter = 0
    }
    return Triple(abbrev, book, chapter)
}

fun backupData(
    context: Any?,
    user: Boolean = false,
    data: Boolean = false,
    completed: String? = null
) {
    val userDir = getExternalPath(context)
    val dataDir = getPrefsDir(context)
    val download = getDownloadsDir()

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

    if (completed != null) showToast(context, completed)
}

fun restoreBackup(context: Any?, uri: PlatformFile, user: Boolean, onFinished: () -> Unit) {
    if (user) {
        val dir = getExternalPath(context)
        val canonicalDir = File(dir).canonicalPath
        val temp = File(dir, "temp.zip")
        copyUriToFile(context, uri, temp)
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
    }

    /* TODO: Restore Preferences
     * https://github.com/SchweGELBin/OpenBible2/issues/35
    else { }
    */
}

fun turnChapter(
    context: Any?,
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

fun sanitizeAbbrev(abbrev: String?): String = abbrev?.replace(Regex("[^a-zA-Z0-9_-]"), "") ?: ""

fun searchText(context: Any?, query: String, abbrev: String): List<Triple<String, Int, Int>> {
    val result = mutableListOf(Triple("", -1, -1))
    val bible = getBible(getTranslationPath(context, abbrev)) ?: return result
    val (inclusions, exclusions) = splitSearchQuery(query)
    bible.books.forEachIndexed { bookIndex, book ->
        book.chapters.forEachIndexed { chapterIndex, chapter ->
            chapter.verses.forEach { (name, _, text) ->
                val includes = inclusions.all { matchSearchQuery(text, it) }
                val excludes = exclusions.any { matchSearchQuery(text, it) }
                if (includes && !excludes) result += Triple(
                    "${name}\n${text}",
                    bookIndex,
                    chapterIndex
                )
            }
        }
    }
    return result
}

fun splitSearchQuery(query: String): Pair<List<String>, List<String>> {
    val inclusions = mutableListOf<String>()
    val exclusions = mutableListOf<String>()
    val prefixes = Pair("+", "~")
    val pattern =
        Regex("([${prefixes.first}${prefixes.second}])?([^${prefixes.first}${prefixes.second}]+)")
    for (groups in pattern.findAll(query)) {
        val prefix = groups.groupValues[1]
        val frag = groups.groupValues[2]
        if (prefix == prefixes.second) exclusions.add(frag) else inclusions.add(frag)
    }
    return Pair(inclusions, exclusions)
}

fun matchSearchQuery(text: String, query: String): Boolean {
    return when (query.first()) {
        '_' -> text.contains(query.drop(1), ignoreCase = true)
        '=' -> text.contains(query.drop(1), ignoreCase = false)
        else -> {
            val cleaner = Regex("[^A-Za-z0-9 ]")
            text.trim().replace(cleaner, "")
                .contains(query.trim().replace(cleaner, ""), ignoreCase = true)
        }
    }
}

fun saveDeepLink(context: Any?, book: String?, chapter: String?) {
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