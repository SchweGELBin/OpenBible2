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
    val map = deserializeTranslations(getIndexPath(context)) ?: return null
    return map.values.groupBy { it.lang }.toSortedMap()
}

fun getLanguageName(code: String, locale: Locale = Locale.getDefault()): String {
    return Locale.forLanguageTag(code).getDisplayLanguage(locale)
}

fun getTranslationInfo(context: Context, abbrev: String): String {
    val map = deserializeTranslations(getIndexPath(context)) ?: return ""
    map.values.forEach { (translation, abbreviation, _, _, about, license) ->
        if (abbreviation == abbrev) {
            val newAbout = about.replace("\\par ", "\n").replace("\\par", "\n")
            return "$translation\n\n$newAbout\n\n$license"
        }
    }
    return ""
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
    if (showVerseNumbers && text.isNotEmpty()) text = text.substring(0, text.length - 1)
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

fun getTranslationList(context: Context): Array<File> {
    return getList(context).filter { file -> (file.name != "translations.json" && file.isFile) }
        .toTypedArray()
}

fun File.getChecksum(): String? {
    return try {
        val md = MessageDigest.getInstance("SHA-1")
        val fis = FileInputStream(this)
        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (fis.read(buffer).also { bytesRead = it } != -1) {
            md.update(buffer, 0, bytesRead)
        }
        fis.close()
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
    return "${getExternalPath(context)}/${abbrev}.json"
}

fun getExternalPath(context: Context, relPath: String = ""): String {
    return context.getExternalFilesDir(relPath).toString()
}