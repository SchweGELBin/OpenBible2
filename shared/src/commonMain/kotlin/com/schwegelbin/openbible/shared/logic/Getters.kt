package com.schwegelbin.openbible.shared.logic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.schwegelbin.openbible.shared.getExternalPath
import com.schwegelbin.openbible.shared.ui.screens.BibleCache.getBible
import com.schwegelbin.openbible.shared.ui.screens.BibleCache.getTranslations
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest
import java.util.Locale

fun getLanguageName(code: String, locale: Locale = Locale.getDefault()): String =
    Locale.forLanguageTag(code).getDisplayLanguage(locale)

fun getTranslationInfo(context: Any?, abbrev: String): String =
    (getTranslations(context)[abbrev]?.let {
        "${it.translation}\n\n${it.about}\n\n${it.license}"
    } ?: getBible(getTranslationPath(context, abbrev))?.let {
        "${it.translation}\n\n${it.about}\n\n${it.license}"
    } ?: "").replace("\\par", "\n")

fun getCount(
    context: Any?, abbrev: String, book: Int
): Pair<Int, Int> {
    val bible = getBible(getTranslationPath(context, abbrev)) ?: return Pair(0, 0)
    val books = bible.books.size - 1
    if (book > books) return Pair(0, bible.books[0].chapters.size - 1)
    return Pair(books, bible.books[book].chapters.size - 1)
}

fun getBookNames(context: Any?, abbrev: String): Array<String> {
    val bible = getBible(getTranslationPath(context, abbrev)) ?: return Array(1) { "ERROR" }
    val num = bible.books.size
    val arr = Array(num) { "" }
    for (i in 0..<num) {
        arr[i] = bible.books[i].name
    }
    return arr
}

fun getChapter(
    context: Any?,
    abbrev: String,
    book: Int,
    chapter: Int,
    showVerseNumbers: Boolean,
    error: String
): Triple<String, String, String> {
    val bible =
        getBible(getTranslationPath(context, abbrev)) ?: return Triple(error, error, "")
    var text = ""
    bible.books[book].chapters[chapter].verses.forEach { verse ->
        text += if (showVerseNumbers) "${verse.verse} ${verse.text}".trim() + "\n"
        else verse.text.trim() + " "
    }
    if (text.isNotEmpty()) text = text.dropLast(1)
    return Triple(bible.translation, bible.books[book].chapters[chapter].name, text)
}

fun getMainThemeOptions(
    context: Any?, themeOption: ThemeOption? = null, schemeOption: SchemeOption? = null
): Triple<Boolean?, Boolean, Boolean> {
    var (theme, scheme) = getColorScheme(context)

    if (themeOption != null) theme = themeOption
    if (schemeOption != null) scheme = schemeOption

    val darkTheme = when (theme) {
        ThemeOption.System -> null
        ThemeOption.Light -> false
        ThemeOption.Dark -> true
        ThemeOption.Amoled -> true
    }
    val dynamicColor = when (scheme) {
        SchemeOption.Dynamic -> true
        SchemeOption.Static -> false
    }
    val amoled = theme == ThemeOption.Amoled

    return Triple(darkTheme, dynamicColor, amoled)
}

fun getAppName(name: String, primary: Color, secondary: Color, tertiary: Color): AnnotatedString {
    val appName = name.split(Regex("(?=[A-Z])")).filter { it.isNotEmpty() }
    val title = buildAnnotatedString {
        appName.forEachIndexed { index, value ->
            when (index) {
                1 -> withStyle(SpanStyle(primary)) { append(value) }
                2 -> withStyle(SpanStyle(secondary)) { append(value) }
                3 -> withStyle(SpanStyle(tertiary)) { append(value) }
                else -> append(value)
            }
        }
    }
    return title
}

fun getList(context: Any?, relPath: String = ""): Array<File> =
    File(getExternalPath(context, relPath)).listFiles() ?: emptyArray()

fun getInstalledTranslations(context: Any?, showCustom: Boolean? = null): Array<File> {
    val list = getList(context).filter { file -> (file.name != "translations.json" && file.isFile) }
    return when (showCustom) {
        null -> list
        true -> list.filter { file -> (file.name.startsWith("ex-")) }
        false -> list.filter { file -> (!file.name.startsWith("ex-")) }
    }.toTypedArray()
}

fun getAvailableTranslations(context: Any?): Map<String, List<Translation>> {
    val map = getTranslations(context)
    return map.values.groupBy { it.lang }.toSortedMap()
}

fun File.getChecksum(): String? {
    return try {
        val md = MessageDigest.getInstance("SHA-1")
        FileInputStream(this).use { fis ->
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (fis.read(buffer).also { bytesRead = it } != -1) {
                md.update(buffer, 0, bytesRead)
            }
        }
        bytesToHex(md.digest())
    } catch (_: Exception) {
        null
    }
}

fun getIndex(context: Any?): File = File(getIndexPath(context))

fun getIndexPath(context: Any?): String = "${getExternalPath(context)}/translations.json"

fun getTranslation(context: Any?, abbrev: String): File = File(getTranslationPath(context, abbrev))

fun getTranslationPath(context: Any?, abbrev: String): String =
    "${getExternalPath(context)}/${sanitizeAbbrev(abbrev)}.json"

fun getUpdateList(context: Any?, install: Boolean, translation: String? = null): List<String> {
    val updates = mutableListOf<String>()
    val installed =
        getInstalledTranslations(context, showCustom = false).map { it.nameWithoutExtension }
    val index = getTranslations(context)
    index.values.forEach { (abbrev, _, _, _, _, _, sha) ->
        if (installed.contains(abbrev) && (translation == null || abbrev == translation)) {
            if (getTranslation(context, abbrev).getChecksum() != sha) {
                if (install) downloadTranslation(context, abbrev)
                updates.add(abbrev)
            }
        }
    }
    return updates
}

fun getBookAbbreviations(): Array<List<String>> {
    return arrayOf(
        listOf("gen", "1mo"),
        listOf("exo", "2mo"),
        listOf("lev", "3mo"),
        listOf("num", "4mo"),
        listOf("deu", "5mo"),
        listOf("jos"),
        listOf("jug", "ric", "ri"),
        listOf("rut", "ruth", "rt"),
        listOf("1sa", "1sam"),
        listOf("2sa", "2sam"),
        listOf("1ki", "1kö", "1kön"),
        listOf("2ki", "2kö", "2kön"),
        listOf("1ch", "1chr"),
        listOf("2ch", "2chr"),
        listOf("ezr", "esr", "esra"),
        listOf("neh"),
        listOf("est", "esth"),
        listOf("job", "hio", "hi", "hiob"),
        listOf("psa", "ps"),
        listOf("pro", "spr"),
        listOf("ecc", "pre", "pred"),
        listOf("son", "hoh", "hl"),
        listOf("isa", "jes"),
        listOf("jer"),
        listOf("lam", "kla"),
        listOf("eze", "hes"),
        listOf("dan"),
        listOf("hos"),
        listOf("joe", "joel"),
        listOf("amo", "am", "amos"),
        listOf("oba", "ob"),
        listOf("jon", "jona"),
        listOf("mic", "mi"),
        listOf("nah"),
        listOf("hab"),
        listOf("zep", "zef", "zeph"),
        listOf("hag"),
        listOf("zec", "sac", "sach"),
        listOf("mal"),
        listOf("mat", "mt"),
        listOf("mar", "mk"),
        listOf("luk", "lk"),
        listOf("joh"),
        listOf("act", "apo", "apg"),
        listOf("rom", "röm"),
        listOf("1co", "1ko", "1kor"),
        listOf("2co", "2ko", "2kor"),
        listOf("gal"),
        listOf("eph"),
        listOf("php", "phil"),
        listOf("col", "kol"),
        listOf("1th", "1thes"),
        listOf("2th", "2thes"),
        listOf("1ti", "1tim"),
        listOf("2ti", "2tim"),
        listOf("tit"),
        listOf("phm", "phim", "phl"),
        listOf("heb", "hebr"),
        listOf("jam", "jak"),
        listOf("1pe", "1petr", "1pet"),
        listOf("2pe", "2petr", "2pet"),
        listOf("1jo", "1joh"),
        listOf("2jo", "2joh"),
        listOf("3jo", "3joh"),
        listOf("jud"),
        listOf("rev", "off", "offb")
    )
}

fun getReadSelection(
    context: Any?,
    onNavigateToStart: () -> Unit,
    isSplitScreen: Boolean
): Triple<String, Int, Int> {
    var (abbrev, book, chapter) = getSelection(context, isSplitScreen)
    if (!getTranslation(context, abbrev).exists() ||
        getBible(getTranslationPath(context, abbrev)) == null
    ) {
        val list = getInstalledTranslations(context).map { it.nameWithoutExtension }
        if (list.isEmpty()) onNavigateToStart()
        for (item in list) {
            if (getBible(getTranslationPath(context, item)) != null) {
                abbrev = item
                break
            }
        }
    }
    return saveSelection(context, abbrev, book, chapter, isSplitScreen)
}