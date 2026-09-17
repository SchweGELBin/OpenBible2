package com.schwegelbin.openbible.shared

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.prefs.Preferences
import javax.swing.JFileChooser

private enum class Platform {
    Linux,
    MacOS,
    Windows
}

private fun getPlatform(): Platform {
    val system = System.getProperty("os.name").lowercase()
    return if (system.contains("win")) Platform.Windows
    else if (system.contains("mac")) Platform.MacOS
    else Platform.Linux
}

actual data class PlatformFile(
    val file: File
)

@Composable
actual fun getContext(): Any? = null

@Composable
actual fun getView(): Any? = null

@Composable
actual fun FilePicker(extension: String, launch: Boolean, onFilesSelected: FileSelected) {
    LaunchedEffect(launch) {
        if (!launch) return@LaunchedEffect
        val fileChooser = JFileChooser(getDownloadsDir()).apply {
            fileSelectionMode = JFileChooser.FILES_ONLY
        }
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            fileChooser.selectedFile?.let { file ->
                onFilesSelected(PlatformFile(file))
            }
        }
    }
}

actual fun copyUriToFile(context: Any?, uri: PlatformFile, file: File) {
    File(uri.file.path).inputStream().use { inputStream ->
        FileOutputStream(file).use { outputStream -> inputStream.copyTo(outputStream) }
    }
}

actual fun checkMaterialYouSupport(): Boolean = false

actual fun openUrl(context: Any?, url: String) =
    java.awt.Desktop.getDesktop().browse(URI(url))

actual fun getExternalPath(context: Any?, relPath: String): String {
    val projects = when (getPlatform()) {
        Platform.Linux -> System.getenv("XDG_DATA_HOME")
            ?: "${System.getProperty("user.home")}/.local/share"

        Platform.MacOS -> System.getenv("XDG_DATA_HOME")
            ?: "${System.getProperty("user.home")}/Library"

        Platform.Windows -> System.getenv("LOCALAPPDATA")
            ?: "${System.getProperty("user.home")}/AppData/Local"
    }
    val path = "${projects}/openbible/${relPath}"
    File(path).mkdirs()
    return path
}

actual fun getPrefsDir(context: Any?): String {
    val projects = when (getPlatform()) {
        Platform.Linux -> System.getenv("XDG_CONFIG_HOME")
            ?: "${System.getProperty("user.home")}/.config"

        Platform.MacOS -> System.getenv("XDG_CONFIG_HOME")
            ?: "${System.getProperty("user.home")}/Library/Preferences"

        Platform.Windows -> System.getenv("APPDATA")
            ?: "${System.getProperty("user.home")}/AppData/Roaming"
    }
    val path = "${projects}/openbible"
    File(path).mkdirs()
    return path
}

actual fun getDownloadsDir(): File = File(System.getProperty("user.home"), "Downloads")

actual fun downloadFile(
    context: Any?,
    url: String,
    name: String,
    relPath: String,
    replace: Boolean,
    title: String
): Long {
    val file = File(getExternalPath(context), name)
    if (replace) file.delete()
    Thread {
        val request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build()
        val response =
            HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofFile(file.toPath()))
        if (response.statusCode() !in 200..299) file.delete()
    }.start()
    return file.hashCode().toLong()
}

actual fun getDynamicColorScheme(context: Any?, darkTheme: Boolean, amoled: Boolean): ColorScheme? =
    null

actual fun setWindowDecorations(view: Any?, darkTheme: Boolean) {}

actual fun showToast(context: Any?, message: String?) {}

actual class SharedPreference actual constructor(context: Any?, name: String?) {
    private val sharedPreferences: Preferences = Preferences.userRoot().node(name)

    actual fun clear() = sharedPreferences.clear()

    actual fun remove(key: String) = sharedPreferences.remove(key)

    actual fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        sharedPreferences.getBoolean(key, defaultValue)

    actual fun setBoolean(key: String, value: Boolean) = sharedPreferences.putBoolean(key, value)

    actual fun getFloat(key: String, defaultValue: Float): Float =
        sharedPreferences.getFloat(key, defaultValue)

    actual fun setFloat(key: String, value: Float) = sharedPreferences.putFloat(key, value)

    actual fun getInt(key: String, defaultValue: Int): Int =
        sharedPreferences.getInt(key, defaultValue)

    actual fun setInt(key: String, value: Int) = sharedPreferences.putInt(key, value)

    actual fun getLong(key: String, defaultValue: Long): Long =
        sharedPreferences.getLong(key, defaultValue)

    actual fun setLong(key: String, value: Long) = sharedPreferences.putLong(key, value)

    actual fun getString(key: String, defaultValue: String): String? =
        sharedPreferences.get(key, defaultValue)

    actual fun setString(key: String, value: String) = sharedPreferences.put(key, value)
}