package com.schwegelbin.openbible.logic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import java.io.File

@Serializable
data class Verse(
    val name: String, val verse: Int, val text: String
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
    val abbreviation: String,
    @SerialName("distribution_about") val about: String,
    @SerialName("distribution_license") val license: String,
    val translation: String,
    val books: List<Book>,
)

@Serializable
data class Translation(
    val abbreviation: String,
    @SerialName("distribution_about") val about: String,
    @SerialName("distribution_license") val license: String,
    val translation: String,
    val lang: String,
    val language: String,
    val sha: String,
)

fun deserializeBible(path: String): Bible? {
    if (!File(path).exists()) return null

    val unknown = Json { ignoreUnknownKeys = true; }
    return try {
        unknown.decodeFromString<Bible>(File(path).readText())
    } catch (_: SerializationException) {
        null
    }
}

fun deserialize(path: String): JsonObject? {
    if (!File(path).exists()) return null

    return try {
        Json.decodeFromString<JsonObject>(File(path).readText())
    } catch (_: SerializationException) {
        null
    }
}

fun deserializeTranslations(path: String): Map<String, Translation>? {
    if (!File(path).exists()) return null

    val unknown = Json { ignoreUnknownKeys = true; }
    return try {
        unknown.decodeFromString<Map<String, Translation>>(File(path).readText())
    } catch (_: SerializationException) {
        null
    }
}

fun Map<String, Translation>?.removeApocrypha(): Map<String, Translation>? {
    val apocryphaList = listOf("kjva", "statenvertalinga")
    return this?.filterKeys { key -> key !in apocryphaList }
}