package com.schwegelbin.openbible.logic

import android.content.Context
import androidx.core.content.edit

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

fun getCheckAtStartup(context: Context): Boolean {
    return context.getSharedPreferences("options", Context.MODE_PRIVATE)
        .getBoolean("checkAtStartup", true)
}

fun saveCheckAtStartup(context: Context, check: Boolean) {
    context.getSharedPreferences("options", Context.MODE_PRIVATE).edit {
        putBoolean("checkAtStartup", check)
    }
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


fun getDownloadNotification(context: Context): Boolean {
    return context.getSharedPreferences("options", Context.MODE_PRIVATE)
        .getBoolean("notifyDownload", false)
}

fun saveDownloadNotification(context: Context, enabled: Boolean) {
    context.getSharedPreferences("options", Context.MODE_PRIVATE).edit {
        putBoolean("notifyDownload", enabled)
    }
}


fun getInfiniteScroll(context: Context): Boolean {
    return context.getSharedPreferences("options", Context.MODE_PRIVATE)
        .getBoolean("infiniteScroll", true)
}

fun saveInfiniteScroll(context: Context, shown: Boolean) {
    context.getSharedPreferences("options", Context.MODE_PRIVATE).edit {
        putBoolean("infiniteScroll", shown)
    }
}


fun getSelection(context: Context, isSplitScreen: Boolean): Triple<String, Int, Int> {
    val sharedPref = context.getSharedPreferences("selection", Context.MODE_PRIVATE)
    var translation = "schlachter"
    var book = 42
    var chapter = 2
    if (!isSplitScreen) {
        translation = sharedPref.getString("translation", translation).toString()
        book = sharedPref.getInt("book", book)
        chapter = sharedPref.getInt("chapter", chapter)
    } else {
        translation = sharedPref.getString("translation_split", translation).toString()
        book = sharedPref.getInt("book_split", book)
        chapter = sharedPref.getInt("chapter_split", chapter)
    }
    if (book == 42 && chapter == 2) {
        val bible = deserializeBible(getTranslationPath(context, translation))
        if (bible != null && book > bible.books.size) {
            book = 0
            chapter = 0
        }
    }

    return Triple(translation, book, chapter)
}

fun saveSelection(
    context: Context,
    translation: String? = null,
    book: Int? = null,
    chapter: Int? = null,
    isSplitScreen: Boolean
) {
    val savedSelection = getSelection(context, isSplitScreen)
    val (newTranslation, newBook, newChapter) = checkSelection(context, Triple(translation ?: savedSelection.first, book ?: savedSelection.second, chapter ?: savedSelection.third))
    context.getSharedPreferences("selection", Context.MODE_PRIVATE).edit {
        if (!isSplitScreen) {
            if (translation != null) putString("translation", newTranslation)
            if (book != null) putInt("book", newBook)
            if (chapter != null) putInt("chapter", newChapter)
        } else {
            if (translation != null) putString("translation_split", newTranslation)
            if (book != null) putInt("book_split", newBook)
            if (chapter != null) putInt("chapter_split", newChapter)
        }
    }
}


fun getShowVerseNumbers(context: Context): Boolean {
    return context.getSharedPreferences("options", Context.MODE_PRIVATE)
        .getBoolean("showVerseNumbers", true)
}

fun saveShowVerseNumbers(context: Context, shown: Boolean) {
    context.getSharedPreferences("options", Context.MODE_PRIVATE).edit {
        putBoolean("showVerseNumbers", shown)
    }
}


fun getSplitScreen(context: Context): SplitScreen {
    val sharedPref = context.getSharedPreferences("options", Context.MODE_PRIVATE)
    val splitScreenStr = sharedPref.getString("split", "Start")

    val splitScreen = when (splitScreenStr) {
        SplitScreen.Off.toString() -> SplitScreen.Off
        SplitScreen.Vertical.toString() -> SplitScreen.Vertical
        SplitScreen.Horizontal.toString() -> SplitScreen.Horizontal
        else -> SplitScreen.Off
    }

    return splitScreen
}

fun getSplitScreenInt(context: Context): Int {
    return when (getSplitScreen(context)) {
        SplitScreen.Off -> 0
        SplitScreen.Vertical -> 1
        SplitScreen.Horizontal -> 2
    }
}

fun saveSplitScreen(context: Context, split: SplitScreen) {
    context.getSharedPreferences("options", Context.MODE_PRIVATE).edit {
        putString("split", split.toString())
    }
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

fun getTextAlignmentInt(context: Context): Int {
    return when (getTextAlignment(context)) {
        ReadTextAlignment.Start -> 0
        ReadTextAlignment.Justify -> 1
    }
}

fun saveTextAlignment(context: Context, alignment: ReadTextAlignment) {
    context.getSharedPreferences("options", Context.MODE_PRIVATE).edit {
        putString("textAlignment", alignment.toString())
    }
}

fun getFontSize(context: Context): ClosedFloatingPointRange<Float> {
    val start = context.getSharedPreferences("options", Context.MODE_PRIVATE)
        .getFloat("fontSizeStart", 1f)
    val end = context.getSharedPreferences("options", Context.MODE_PRIVATE)
        .getFloat("fontSizeEnd", 1.8f)
    return start..end
}

fun saveFontSize(context: Context, range: ClosedFloatingPointRange<Float>) {
    context.getSharedPreferences("options", Context.MODE_PRIVATE).edit {
        putFloat("fontSizeStart", range.start)
        putFloat("fontSizeEnd", range.endInclusive)
    }
}

fun getVerseOfTheDay(context: Context): Boolean {
    return context.getSharedPreferences("options", Context.MODE_PRIVATE)
        .getBoolean("verseOfTheDay", false)
}

fun saveVerseOfTheDay(context: Context, shown: Boolean) {
    context.getSharedPreferences("options", Context.MODE_PRIVATE).edit {
        putBoolean("verseOfTheDay", shown)
    }
}