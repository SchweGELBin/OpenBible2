package com.schwegelbin.openbible.logic

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest
import java.util.Locale

fun getTranslations(context: Context): Map<String, List<Translation>>? {
    val map = deserializeTranslations(getIndexPath(context)).removeApocrypha() ?: return null
    return map.values.groupBy { it.lang }.toSortedMap()
}

fun getLanguageName(code: String, locale: Locale = Locale.getDefault()): String {
    return Locale.forLanguageTag(code).getDisplayLanguage(locale)
}

fun getTranslationInfo(context: Context, abbrev: String): String {
    val map = deserializeTranslations(getIndexPath(context)) ?: return ""
    var info = ""
    map.values.forEach { (abbreviation, about, license, translation, _, _, _) ->
        if (abbreviation == abbrev) {
            info = "$translation\n\n$about\n\n$license"
            return@forEach
        }
    }
    if (info == "") {
        val map = deserializeBible(getTranslationPath(context, abbrev)) ?: return ""
        info = "${map.translation}\n\n${map.about}\n\n${map.license}"
    }
    return info.replace("\\par ", "\n").replace("\\par", "\n")
}

fun getCount(
    context: Context, abbrev: String, book: Int
): Pair<Int, Int> {
    val bible = deserializeBible(getTranslationPath(context, abbrev)) ?: return Pair(0, 0)
    val books = bible.books.size - 1
    if (book > books) return Pair(0, bible.books[0].chapters.size - 1)
    return Pair(books, bible.books[book].chapters.size - 1)
}

fun getBookNames(context: Context, abbrev: String): Array<String> {
    val bible = deserializeBible(getTranslationPath(context, abbrev)) ?: return Array(1) { "ERROR" }
    val num = bible.books.size
    val arr = Array(num) { "" }
    for (i in 0..<num) {
        arr[i] = bible.books[i].name
    }
    return arr
}

fun getChapter(
    context: Context,
    abbrev: String,
    book: Int,
    chapter: Int,
    showVerseNumbers: Boolean,
    error: String
): Triple<String, String, String> {
    val bible =
        deserializeBible(getTranslationPath(context, abbrev)) ?: return Triple(error, error, "")
    var text = ""
    bible.books[book].chapters[chapter].verses.forEach { verse ->
        text += if (showVerseNumbers) "${verse.verse} ${verse.text}".trim() + "\n"
        else verse.text
    }
    if (showVerseNumbers && text.isNotEmpty()) text = text.dropLast(1)
    return Triple(bible.translation, bible.books[book].chapters[chapter].name, text)
}

fun getMainThemeOptions(
    context: Context, themeOption: ThemeOption? = null, schemeOption: SchemeOption? = null
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

fun getList(context: Context, relPath: String = ""): Array<File> {
    return File(getExternalPath(context, relPath)).listFiles() ?: return emptyArray()
}

fun getTranslationList(context: Context, showCustom: Boolean? = null): Array<File> {
    val list = getList(context).filter { file -> (file.name != "translations.json" && file.isFile) }
    return when(showCustom) {
        null -> list
        true -> list.filter { file -> (file.name.startsWith("ex-")) }
        false -> list.filter { file -> (!file.name.startsWith("ex-")) }
    }.toTypedArray()
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

fun getIndex(context: Context): File {
    return File(getIndexPath(context))
}

fun getIndexPath(context: Context): String {
    return "${getExternalPath(context)}/translations.json"
}

fun getTranslation(context: Context, abbrev: String): File {
    return File(getTranslationPath(context, abbrev))
}

fun getTranslationPath(context: Context, abbrev: String): String {
    return "${getExternalPath(context)}/${sanitizeAbbrev(abbrev)}.json"
}

fun getExternalPath(context: Context, relPath: String = ""): String {
    return context.getExternalFilesDir(relPath).toString()
}

fun getUpdateList(context: Context, install: Boolean, translation: String? = null): List<String> {
    val updates = mutableListOf<String>()
    val installed = getTranslationList(context, showCustom = false).map { it.nameWithoutExtension }
    val index = deserializeTranslations(getIndexPath(context)) ?: return emptyList()
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
        listOf("jug", "ric"),
        listOf("rut"),
        listOf("1sa"),
        listOf("2sa"),
        listOf("1ki", "1kö"),
        listOf("2ki", "2kö"),
        listOf("1ch"),
        listOf("2ch"),
        listOf("ezr", "esr"),
        listOf("neh"),
        listOf("est"),
        listOf("job", "hio"),
        listOf("psa"),
        listOf("pro", "spr"),
        listOf("ecc", "pre"),
        listOf("son", "hoh"),
        listOf("isa", "jes"),
        listOf("jer"),
        listOf("lam", "kla"),
        listOf("eze", "hes"),
        listOf("dan"),
        listOf("hos"),
        listOf("joe"),
        listOf("amo"),
        listOf("oba"),
        listOf("jon"),
        listOf("mic"),
        listOf("nah"),
        listOf("hab"),
        listOf("zep", "zef"),
        listOf("hag"),
        listOf("zec", "sac"),
        listOf("mal"),
        listOf("mat"),
        listOf("mar"),
        listOf("luk"),
        listOf("joh"),
        listOf("act", "apo"),
        listOf("rom", "röm"),
        listOf("1co", "1ko"),
        listOf("2co", "2ko"),
        listOf("gal"),
        listOf("eph"),
        listOf("php"),
        listOf("col", "kol"),
        listOf("1th"),
        listOf("2th"),
        listOf("1ti"),
        listOf("2ti"),
        listOf("tit"),
        listOf("phm"),
        listOf("heb"),
        listOf("jam", "jak"),
        listOf("1pe"),
        listOf("2pe"),
        listOf("1jo"),
        listOf("2jo"),
        listOf("3jo"),
        listOf("jud"),
        listOf("rev", "off")
    )
}