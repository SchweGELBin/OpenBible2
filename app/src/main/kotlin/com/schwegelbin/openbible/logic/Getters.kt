package com.schwegelbin.openbible.logic

import android.content.Context
import java.io.File

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
    context: Context, abbrev: String = selectedTranslation, book: Int = selectedBook
): Pair<Int, Int> {
    val dir = context.getExternalFilesDir("Translations")
    val path = "${dir}/${abbrev}.json"
    val bible = deserializeBible(path)
    if (bible == null) return Pair(0, 0)
    return Pair(bible.books.size - 1, bible.books[book].chapters.size - 1)
}

fun getBookNames(context: Context, abbrev: String = selectedTranslation): Array<String> {
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
    abbrev: String = selectedTranslation,
    book: Int = selectedBook,
    chapter: Int = selectedChapter
): Pair<String, String> {
    val dir = context.getExternalFilesDir("Translations")
    val path = "${dir}/${abbrev}.json"
    val bible = deserializeBible(path)
    if (bible == null) return Pair(
        first = "ERROR",
        second = "Please try again\nCheck your internet connection"
    )
    val translation = bible.translation
    val title = bible.books[book].chapters[chapter].name
    var text = ""
    val verses = bible.books[book].chapters[chapter].verses
    verses.forEach { verse ->
        text += "${verse.verse} ${verse.text}\n"
    }
    return Pair("$translation | $title", text)
}

fun getDefaultFiles(context: Context) {
    val sharedPref = context.getSharedPreferences("selection", Context.MODE_PRIVATE)
    selectedTranslation = sharedPref.getString("translation", selectedTranslation).toString()
    selectedBook = sharedPref.getInt("book", selectedBook)
    selectedChapter = sharedPref.getInt("chapter", selectedChapter)
    var path = context.getExternalFilesDir("Index")
    if (!File("${path}/translations.json").exists() || !File("${path}/checksum.json").exists()) {
        saveIndex(context)
        saveChecksum(context)
    }
    path = context.getExternalFilesDir("Translations")
    if (!File("${path}/${selectedTranslation}.json").exists()) {
        downloadTranslation(
            context, selectedTranslation
        )
    }
}