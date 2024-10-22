package com.schwegelbin.openbible.logic

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import java.io.File
import kotlin.collections.Map

@Serializable
data class Verse(
    val verse: Int, val text: String
)

@Serializable
data class Chapter(
    val name: String, val verses: List<Verse>
)

@Serializable
data class Book(
    val name: String, val chapters: List<Chapter>
)

@Serializable
data class Bible(
    val books: List<Book>, val translation: String
)

@Serializable
data class Translation(
    val translation: String, val abbreviation: String, val lang: String
)

fun deserializeBible(path: String): Bible? {
    if (!File(path).exists()) return null

    val json = File(path).readText()
    val unknown = Json { ignoreUnknownKeys = true; }
    val bible = try {
        unknown.decodeFromString<Bible>(json)
    } catch (_: SerializationException) {
        null
    }

    return bible
}

fun deserialize(path: String): JsonObject? {
    if (!File(path).exists()) return null

    val json = File(path).readText()
    val obj = try {
        Json.decodeFromString<JsonObject>(json)
    } catch (_: SerializationException) {
        null
    }

    return obj
}

fun deserializeTranslations(path: String): Map<String, Translation>? {
    if (!File(path).exists()) return null

    val json = File(path).readText()
    val unknown = Json { ignoreUnknownKeys = true; }
    val map = try {
        unknown.decodeFromString<Map<String, Translation>>(json)
    } catch (_: SerializationException) {
        null
    }

    return map
}