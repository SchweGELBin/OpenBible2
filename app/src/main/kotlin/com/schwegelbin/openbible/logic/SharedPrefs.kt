package com.schwegelbin.openbible.logic

import android.content.Context
import androidx.core.content.edit

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
        val bible = deserializeBible(
            "${context.getExternalFilesDir("Translations")}/${translation}.json"
        )
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
    context.getSharedPreferences("selection", Context.MODE_PRIVATE).edit {
        if (!isSplitScreen) {
            if (translation != null) putString("translation", translation)
            if (book != null) putInt("book", book)
            if (chapter != null) putInt("chapter", chapter)
        } else {
            if (translation != null) putString("translation_split", translation)
            if (book != null) putInt("book_split", book)
            if (chapter != null) putInt("chapter_split", chapter)
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

fun saveTextAlignment(context: Context, alignment: ReadTextAlignment) {
    context.getSharedPreferences("options", Context.MODE_PRIVATE).edit {
        putString("textAlignment", alignment.toString())
    }
}
