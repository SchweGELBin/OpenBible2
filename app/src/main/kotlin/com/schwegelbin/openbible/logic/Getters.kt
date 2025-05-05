package com.schwegelbin.openbible.logic

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import java.io.File
import java.util.Locale

fun getTranslations(context: Context): Map<String, List<Translation>>? {
    val map = deserializeTranslations(
        "${context.getExternalFilesDir("Index")}/translations.json"
    ) ?: return null
    return map.values.groupBy { it.lang }.toSortedMap()
}

fun getLanguageName(code: String, locale: Locale = Locale.getDefault()): String {
    return Locale(code).getDisplayLanguage(locale)
}

fun getChecksum(context: Context, abbrev: String): String {
    val obj = deserialize(
        "${context.getExternalFilesDir("Index")}/checksum.json"
    ) ?: return "unknown"
    return obj[abbrev].toString()
}

fun getTranslationInfo(context: Context, abbrev: String): String {
    val map = deserializeTranslations(
        "${context.getExternalFilesDir("Index")}/translations.json"
    ) ?: return ""
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
    val bible = deserializeBible(
        "${context.getExternalFilesDir("Translations")}/${abbrev}.json"
    ) ?: return Pair(0, 0)
    val books = bible.books.size - 1
    if (book > books) return Pair(0, bible.books[0].chapters.size - 1)
    return Pair(books, bible.books[book].chapters.size - 1)
}

fun getBookNames(context: Context, abbrev: String): Array<String> {
    val bible = deserializeBible(
        "${context.getExternalFilesDir("Translations")}/${abbrev}.json"
    ) ?: return Array(1) { "ERROR" }
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
): Pair<String, String> {
    val bible = deserializeBible(
        "${context.getExternalFilesDir("Translations")}/${abbrev}.json"
    ) ?: return Pair(error, "")
    var text = ""
    bible.books[book].chapters[chapter].verses.forEach { verse ->
        text += if (showVerseNumbers) "${verse.verse} ${verse.text}".trim() + "\n"
        else verse.text
    }
    if (showVerseNumbers) text = text.substring(0, text.length - 1)
    return Pair("${bible.translation} | ${bible.books[book].chapters[chapter].name}", text)
}

fun getTextAlignmentInt(context: Context): Int {
    return when (getTextAlignment(context)) {
        ReadTextAlignment.Start -> 0
        ReadTextAlignment.Justify -> 1
    }
}

fun getSplitScreenInt(context: Context): Int {
    return when (getSplitScreen(context)) {
        SplitScreen.Off -> 0
        SplitScreen.Vertical -> 1
        SplitScreen.Horizontal -> 2
    }
}

fun getColorSchemeInt(context: Context, isTheme: Boolean): Int {
    val (theme, scheme) = getColorScheme(context)
    if (isTheme) return when (theme) {
        ThemeOption.System -> 0
        ThemeOption.Light -> 1
        ThemeOption.Dark -> 2
        ThemeOption.Amoled -> 3
    }
    return when (scheme) {
        SchemeOption.Dynamic -> 0
        SchemeOption.Static -> 1
    }
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

fun getFirstLaunch(context: Context): Boolean {
    var dir = context.getExternalFilesDir("Index")
    if (!File("${dir}/translations.json").exists() || !File("${dir}/checksum.json").exists()) return true
    dir = context.getExternalFilesDir("Translations")
    if (dir == null) return true
    val files = dir.listFiles()
    return files == null || files.isEmpty()
}

fun getList(context: Context, relPath: String): Array<File> {
    return File(
        context.getExternalFilesDir(relPath).toString()
    ).listFiles() ?: return emptyArray()
}