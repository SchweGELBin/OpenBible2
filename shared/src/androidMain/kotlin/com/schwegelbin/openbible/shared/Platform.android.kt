package com.schwegelbin.openbible.shared

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import com.schwegelbin.openbible.shared.logic.getDownloadNotification
import com.schwegelbin.openbible.shared.ui.theme.AmoledColorScheme
import java.io.File
import java.io.FileOutputStream

actual data class PlatformFile(
    val uri: Uri?
)

@Composable
actual fun getContext(): Any? = LocalContext.current

@Composable
actual fun getView(): Any? = LocalView.current

@Composable
actual fun FilePicker(extension: String, launch: Boolean, onFilesSelected: FileSelected) {
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { result ->
            onFilesSelected(PlatformFile(result))
        }
    val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    LaunchedEffect(launch) {
        if (launch && mimeType != null) launcher.launch(arrayOf(mimeType))
    }
}

actual fun copyUriToFile(context: Any?, uri: PlatformFile, file: File) {
    if (uri.uri != null) {
        (context as Context).contentResolver.openInputStream(uri.uri)?.use { inputStream ->
            FileOutputStream(file).use { outputStream -> inputStream.copyTo(outputStream) }
        }
    }
}

actual fun checkMaterialYouSupport(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

actual fun openUrl(context: Any?, url: String) =
    (context as Context).startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))

actual fun getExternalPath(context: Any?, relPath: String): String =
    (context as Context).getExternalFilesDir(relPath).toString()

actual fun getPrefsDir(context: Any?): String = "${(context as Context).dataDir}/shared_prefs"

actual fun getDownloadsDir(): File = Environment.getExternalStoragePublicDirectory("Download")

actual fun downloadFile(
    context: Any?,
    url: String,
    name: String,
    relPath: String,
    replace: Boolean,
    title: String
): Long {
    val context = context as Context
    if (replace) File("${getExternalPath(context, relPath)}/${name}").delete()
    val notify =
        if (getDownloadNotification(context)) DownloadManager.Request.VISIBILITY_VISIBLE
        else DownloadManager.Request.VISIBILITY_HIDDEN
    val request = DownloadManager.Request(url.toUri()).apply {
        setTitle(title)
        setDescription("Downloading $name")
        setNotificationVisibility(notify)
        setDestinationInExternalFilesDir(context, relPath, name)
    }
    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    return downloadManager.enqueue(request)
}

actual fun getDynamicColorScheme(context: Any?, darkTheme: Boolean, amoled: Boolean): ColorScheme? {
    var colorScheme = AmoledColorScheme
    if (checkMaterialYouSupport()) {
        colorScheme =
            if (darkTheme) dynamicDarkColorScheme(context as Context) else dynamicLightColorScheme(
                context as Context
            )
        if (amoled) colorScheme = colorScheme.copy(
            background = AmoledColorScheme.background,
            surface = AmoledColorScheme.surface,
            surfaceContainer = AmoledColorScheme.surfaceContainer,
            surfaceContainerLow = AmoledColorScheme.surfaceContainerLow
        )
    }
    return colorScheme
}

actual fun setWindowDecorations(view: Any?, darkTheme: Boolean) {
    val view = view as View
    if (!view.isInEditMode) {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
    }
}

actual fun showToast(context: Any?, message: String?) {
    Toast.makeText(context as Context, message, Toast.LENGTH_SHORT).show()
}

actual class SharedPreference actual constructor(context: Any?, name: String?) {
    private val sharedPreferences: SharedPreferences by lazy {
        (context as Context).getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    actual fun clear() = sharedPreferences.edit { clear() }

    actual fun remove(key: String) = sharedPreferences.edit { remove(key) }

    actual fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        sharedPreferences.getBoolean(key, defaultValue)

    actual fun setBoolean(key: String, value: Boolean) =
        sharedPreferences.edit { putBoolean(key, value) }

    actual fun getFloat(key: String, defaultValue: Float): Float =
        sharedPreferences.getFloat(key, defaultValue)

    actual fun setFloat(key: String, value: Float) = sharedPreferences.edit { putFloat(key, value) }

    actual fun getInt(key: String, defaultValue: Int): Int =
        sharedPreferences.getInt(key, defaultValue)

    actual fun setInt(key: String, value: Int) = sharedPreferences.edit { putInt(key, value) }

    actual fun getLong(key: String, defaultValue: Long): Long =
        sharedPreferences.getLong(key, defaultValue)

    actual fun setLong(key: String, value: Long) = sharedPreferences.edit { putLong(key, value) }

    actual fun getString(key: String, defaultValue: String): String? =
        sharedPreferences.getString(key, defaultValue)

    actual fun setString(key: String, value: String) =
        sharedPreferences.edit { putString(key, value) }
}