package com.schwegelbin.openbible.shared

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import java.io.File

expect class PlatformFile

typealias FileSelected = (PlatformFile?) -> Unit

@Composable
expect fun getContext(): Any?

@Composable
expect fun getView(): Any?

@Composable
expect fun FilePicker(extension: String, launch: Boolean, onFilesSelected: FileSelected)

expect fun copyUriToFile(context: Any?, uri: PlatformFile, file: File)

expect fun checkMaterialYouSupport(): Boolean

expect fun openUrl(context: Any?, url: String)

expect fun getExternalPath(context: Any?, relPath: String = ""): String

expect fun getPrefsDir(context: Any?): String

expect fun getDownloadsDir(): File

expect fun downloadFile(
    context: Any?,
    url: String,
    name: String,
    relPath: String = "",
    replace: Boolean = true,
    title: String = "Downloading File"
): Long

expect fun getDynamicColorScheme(context: Any?, darkTheme: Boolean, amoled: Boolean): ColorScheme?

expect fun setWindowDecorations(view: Any?, darkTheme: Boolean)

expect fun showToast(context: Any?, message: String?)

expect class SharedPreference(context: Any?, name: String? = null) {
    fun clear()
    fun remove(key: String)
    fun getBoolean(key: String, defaultValue: Boolean): Boolean
    fun setBoolean(key: String, value: Boolean)
    fun getFloat(key: String, defaultValue: Float): Float
    fun setFloat(key: String, value: Float)
    fun getInt(key: String, defaultValue: Int): Int
    fun setInt(key: String, value: Int)
    fun getLong(key: String, defaultValue: Long): Long
    fun setLong(key: String, value: Long)
    fun getString(key: String, defaultValue: String): String?
    fun setString(key: String, value: String)
}