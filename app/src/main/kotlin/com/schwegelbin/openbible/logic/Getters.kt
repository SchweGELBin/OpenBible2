package com.schwegelbin.openbible.logic

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

fun getTranslations(context: Context): Map<String, Translation>? {
    val dir = context.getExternalFilesDir("Index")
    val path = "${dir}/translations.json"
    val map = deserializeTranslations(path)
    return map
}

fun getChecksum(context: Context, abbrev: String): String {
    val dir = context.getExternalFilesDir("Index")
    val path = "${dir}/checksum.json"
    val obj = deserialize(path)
    if (obj == null) return "unknown"
    return obj[abbrev].toString()
}

fun getCount(
    context: Context,
    abbrev: String,
    book: Int
): Pair<Int, Int> {
    val dir = context.getExternalFilesDir("Translations")
    val path = "${dir}/${abbrev}.json"
    val bible = deserializeBible(path)
    if (bible == null) return Pair(0, 0)
    return Pair(bible.books.size - 1, bible.books[book].chapters.size - 1)
}

fun getBookNames(context: Context, abbrev: String): Array<String> {
    val dir = context.getExternalFilesDir("Translations")
    val path = "${dir}/${abbrev}.json"
    val bible = deserializeBible(path)
    if (bible == null) return Array<String>(1) { "ERROR" }
    val num = bible.books.size
    var arr = Array<String>(num) { "" }
    for (i in 0..num - 1) {
        arr[i] = bible.books[i].name
    }
    return arr
}

fun getChapter(
    context: Context,
    abbrev: String,
    book: Int,
    chapter: Int,
    showVerseNumbers: Boolean
): Pair<String, String> {
    val dir = context.getExternalFilesDir("Translations")
    val path = "${dir}/${abbrev}.json"
    val bible = deserializeBible(path)
    if (bible == null) return Pair("ERROR", "Please try again\nCheck your internet connection")
    val translation = bible.translation
    val title = bible.books[book].chapters[chapter].name
    var text = ""
    val verses = bible.books[book].chapters[chapter].verses
    verses.forEach { verse ->
        text += if (showVerseNumbers) "${verse.verse} ${verse.text}".trim() + "\n"
        else verse.text
    }
    if (showVerseNumbers) text = text.substring(0, text.length - 1)
    return Pair("$translation | $title", text)
}

fun getColorScheme(context: Context): Pair<ThemeOption, SchemeOption> {
    val sharedPref = context.getSharedPreferences("options", Context.MODE_PRIVATE)
    val themeStr = sharedPref.getString("theme", "System")
    val schemeStr = sharedPref.getString("scheme", "Dynamic")

    val theme = when (themeStr) {
        ThemeOption.System.toString() -> ThemeOption.System
        ThemeOption.Light.toString() -> ThemeOption.Light
        ThemeOption.Dark.toString() -> ThemeOption.Dark
        ThemeOption.Amoled.toString() -> ThemeOption.Amoled
        else -> ThemeOption.System
    }

    val scheme = when (schemeStr) {
        SchemeOption.Dynamic.toString() -> SchemeOption.Dynamic
        SchemeOption.Static.toString() -> SchemeOption.Static
        else -> SchemeOption.Static
    }

    return Pair(theme, scheme)
}

fun getTextAlignment(context: Context): ReadTextAlignment {
    val sharedPref = context.getSharedPreferences("options", Context.MODE_PRIVATE)
    val textAlignmentStr = sharedPref.getString("textAlignment", "Start")

    val textAlignment = when (textAlignmentStr) {
        ReadTextAlignment.Start.toString() -> ReadTextAlignment.Start
        ReadTextAlignment.Justify.toString() -> ReadTextAlignment.Justify
        else -> ReadTextAlignment.Start
    }

    return textAlignment
}

fun getShowVerseNumbers(context: Context): Boolean {
    val sharedPref = context.getSharedPreferences("options", Context.MODE_PRIVATE)
    val shown = sharedPref.getBoolean("showVerseNumbers", true)
    return shown
}

fun getSelection(context: Context): Triple<String, Int, Int> {
    val sharedPref = context.getSharedPreferences("selection", Context.MODE_PRIVATE)
    var translation = sharedPref.getString("translation", defaultTranslation).toString()
    val book = sharedPref.getInt("book", defaultBook)
    val chapter = sharedPref.getInt("chapter", defaultChapter)

    return Triple(translation, book, chapter)
}

fun getTextAlignmentInt(context: Context): Int {
    val textAlignment = getTextAlignment(context)
    return when (textAlignment) {
        ReadTextAlignment.Start -> 0
        ReadTextAlignment.Justify -> 1
        else -> 0
    }
}

fun getColorSchemeInt(context: Context, isTheme: Boolean): Int {
    val (theme, scheme) = getColorScheme(context)
    if (isTheme) return when (theme) {
        ThemeOption.System -> 0
        ThemeOption.Light -> 1
        ThemeOption.Dark -> 2
        ThemeOption.Amoled -> 3
        else -> 0
    }
    return when (scheme) {
        SchemeOption.Dynamic -> 0
        SchemeOption.Static -> 1
        else -> 0
    }
}

fun getMainThemeOptions(
    context: Context,
    themeOption: ThemeOption? = null,
    schemeOption: SchemeOption? = null
): Triple<Boolean?, Boolean, Boolean> {
    var (theme, scheme) = getColorScheme(context)

    if (themeOption != null) theme = themeOption
    if (schemeOption != null) scheme = schemeOption

    val darkTheme = when (theme) {
        ThemeOption.System -> null
        ThemeOption.Light -> false
        ThemeOption.Dark -> true
        ThemeOption.Amoled -> true
        else -> null
    }
    val dynamicColor = when (scheme) {
        SchemeOption.Dynamic -> true
        SchemeOption.Static -> false
        else -> true
    }
    val amoled = theme == ThemeOption.Amoled

    return Triple(darkTheme, dynamicColor, amoled)
}

fun getAppName(name: String, primary: Color, secondary: Color, tertiary: Color): AnnotatedString {
    val appName =
        name.split(Regex("(?=[A-Z])")).filter { it.isNotEmpty() }
    val title = buildAnnotatedString {
        appName.forEachIndexed { index, value ->
            if (index == 1) withStyle(SpanStyle(primary)) { append(value) }
            else if (index == 2) withStyle(SpanStyle(secondary)) {
                append(
                    value
                )
            }
            else if (index == 3) withStyle(SpanStyle(tertiary)) {
                append(
                    value
                )
            }
            else append(value)
        }
    }
    return title
}

fun getFirstLaunch(context: Context): Boolean {
    var dir = context.getExternalFilesDir("Translations")
    if (dir == null) return true
    val files = dir.listFiles()
    if (files == null || files.isEmpty()) return true
    return false
}