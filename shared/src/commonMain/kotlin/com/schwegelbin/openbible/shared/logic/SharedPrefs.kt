package com.schwegelbin.openbible.shared.logic

import com.schwegelbin.openbible.shared.SharedPreference
import com.schwegelbin.openbible.shared.ui.screens.BibleCache.getBible

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

fun getCheckAtStartup(context: Any?): Boolean =
    SharedPreference(context, "options").getBoolean("checkAtStartup", true)

fun saveCheckAtStartup(context: Any?, check: Boolean) =
    SharedPreference(context, "options").setBoolean("checkAtStartup", check)


fun getColorScheme(context: Any?): Pair<ThemeOption, SchemeOption> {
    val sharedPref = SharedPreference(context, "options")
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

fun getColorSchemeInt(context: Any?, isTheme: Boolean): Int {
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

fun saveColorScheme(context: Any?, theme: ThemeOption? = null, scheme: SchemeOption? = null) {
    val sharedPref = SharedPreference(context, "options")
    if (theme != null) sharedPref.setString("theme", theme.toString())
    if (scheme != null) sharedPref.setString("scheme", scheme.toString())
}


fun getDownloadNotification(context: Any?): Boolean =
    SharedPreference(context, "options").getBoolean("notifyDownload", false)

fun saveDownloadNotification(context: Any?, enabled: Boolean) =
    SharedPreference(context, "options").setBoolean("notifyDownload", enabled)


fun getFontSize(context: Any?): ClosedFloatingPointRange<Float> {
    val sharedPref = SharedPreference(context, "options")
    val start = sharedPref.getFloat("fontSizeStart", 1f)
    val end = sharedPref.getFloat("fontSizeEnd", 1.8f)
    return start..end
}

fun saveFontSize(context: Any?, range: ClosedFloatingPointRange<Float>) {
    val sharedPref = SharedPreference(context, "options")
    sharedPref.setFloat("fontSizeStart", range.start)
    sharedPref.setFloat("fontSizeEnd", range.endInclusive)
}


fun getInfiniteScroll(context: Any?): Boolean =
    SharedPreference(context, "options").getBoolean("infiniteScroll", true)

fun saveInfiniteScroll(context: Any?, shown: Boolean) =
    SharedPreference(context, "options").setBoolean("infiniteScroll", shown)


fun getSelection(context: Any?, isSplitScreen: Boolean): Triple<String, Int, Int> {
    val sharedPref = SharedPreference(context, "selection")
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
        val bible = getBible(getTranslationPath(context, translation))
        if (bible != null && book > bible.books.size) {
            book = 0
            chapter = 0
        }
    }

    return Triple(translation, book, chapter)
}

fun saveSelection(
    context: Any?,
    translation: String? = null,
    book: Int? = null,
    chapter: Int? = null,
    isSplitScreen: Boolean
): Triple<String, Int, Int> {
    val savedSelection = getSelection(context, isSplitScreen)
    val (newTranslation, newBook, newChapter) = checkSelection(
        context,
        Triple(
            translation ?: savedSelection.first,
            book ?: savedSelection.second,
            chapter ?: savedSelection.third
        )
    )
    val sharedPref = SharedPreference(context, "selection")
    if (!isSplitScreen) {
        if (translation != null) sharedPref.setString("translation", newTranslation)
        if (book != null) sharedPref.setInt("book", newBook)
        if (chapter != null) sharedPref.setInt("chapter", newChapter)
    } else {
        if (translation != null) sharedPref.setString("translation_split", newTranslation)
        if (book != null) sharedPref.setInt("book_split", newBook)
        if (chapter != null) sharedPref.setInt("chapter_split", newChapter)
    }
    return Triple(newTranslation, newBook, newChapter)
}


fun getShowVerseNumbers(context: Any?): Boolean =
    SharedPreference(context, "options").getBoolean("showVerseNumbers", true)

fun saveShowVerseNumbers(context: Any?, shown: Boolean) =
    SharedPreference(context, "options").setBoolean("showVerseNumbers", shown)


fun getSplitScreen(context: Any?): SplitScreen {
    val sharedPref = SharedPreference(context, "options")
    val splitScreenStr = sharedPref.getString("split", "Start")

    val splitScreen = when (splitScreenStr) {
        SplitScreen.Off.toString() -> SplitScreen.Off
        SplitScreen.Vertical.toString() -> SplitScreen.Vertical
        SplitScreen.Horizontal.toString() -> SplitScreen.Horizontal
        else -> SplitScreen.Off
    }

    return splitScreen
}

fun getSplitScreenInt(context: Any?): Int {
    return when (getSplitScreen(context)) {
        SplitScreen.Off -> 0
        SplitScreen.Vertical -> 1
        SplitScreen.Horizontal -> 2
    }
}

fun saveSplitScreen(context: Any?, split: SplitScreen) =
    SharedPreference(context, "options").setString("split", split.toString())


fun getTextAlignment(context: Any?): ReadTextAlignment {
    val sharedPref = SharedPreference(context, "options")
    val textAlignmentStr = sharedPref.getString("textAlignment", "Start")

    val textAlignment = when (textAlignmentStr) {
        ReadTextAlignment.Start.toString() -> ReadTextAlignment.Start
        ReadTextAlignment.Justify.toString() -> ReadTextAlignment.Justify
        else -> ReadTextAlignment.Start
    }

    return textAlignment
}

fun getTextAlignmentInt(context: Any?): Int {
    return when (getTextAlignment(context)) {
        ReadTextAlignment.Start -> 0
        ReadTextAlignment.Justify -> 1
    }
}

fun saveTextAlignment(context: Any?, alignment: ReadTextAlignment) =
    SharedPreference(context, "options").setString("textAlignment", alignment.toString())


fun getVerseOfTheDay(context: Any?): Boolean =
    SharedPreference(context, "options").getBoolean("verseOfTheDay", false)

fun saveVerseOfTheDay(context: Any?, shown: Boolean) =
    SharedPreference(context, "options").setBoolean("verseOfTheDay", shown)