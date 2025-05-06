package com.schwegelbin.openbible.ui.screens

import android.content.Intent
import android.os.Build
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import com.schwegelbin.openbible.R
import com.schwegelbin.openbible.logic.ReadTextAlignment
import com.schwegelbin.openbible.logic.SchemeOption
import com.schwegelbin.openbible.logic.SplitScreen
import com.schwegelbin.openbible.logic.ThemeOption
import com.schwegelbin.openbible.logic.backupData
import com.schwegelbin.openbible.logic.checkForUpdates
import com.schwegelbin.openbible.logic.downloadTranslation
import com.schwegelbin.openbible.logic.getCheckAtStartup
import com.schwegelbin.openbible.logic.getColorSchemeInt
import com.schwegelbin.openbible.logic.getDownloadNotification
import com.schwegelbin.openbible.logic.getIndexPath
import com.schwegelbin.openbible.logic.getInfiniteScroll
import com.schwegelbin.openbible.logic.getLanguageName
import com.schwegelbin.openbible.logic.getMainThemeOptions
import com.schwegelbin.openbible.logic.getShowVerseNumbers
import com.schwegelbin.openbible.logic.getSplitScreenInt
import com.schwegelbin.openbible.logic.getTextAlignmentInt
import com.schwegelbin.openbible.logic.getTranslation
import com.schwegelbin.openbible.logic.getTranslationList
import com.schwegelbin.openbible.logic.getTranslations
import com.schwegelbin.openbible.logic.saveCheckAtStartup
import com.schwegelbin.openbible.logic.saveColorScheme
import com.schwegelbin.openbible.logic.saveDownloadNotification
import com.schwegelbin.openbible.logic.saveIndex
import com.schwegelbin.openbible.logic.saveInfiniteScroll
import com.schwegelbin.openbible.logic.saveShowVerseNumbers
import com.schwegelbin.openbible.logic.saveSplitScreen
import com.schwegelbin.openbible.logic.saveTextAlignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToRead: () -> Unit,
    onThemeChange: (Boolean?, Boolean?, Boolean?) -> Unit
) {
    val context = LocalContext.current
    Scaffold(topBar = {
        TopAppBar(title = { Text(stringResource(R.string.settings)) }, navigationIcon = {
            IconButton(onClick = { onNavigateToRead() }) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(R.string.close)
                )
            }
        })
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            val styleLarge = MaterialTheme.typography.titleLarge
            val modLarge = Modifier.padding(bottom = 12.dp)
            val styleMedium = MaterialTheme.typography.titleMedium
            Text(stringResource(R.string.translation), style = styleLarge, modifier = modLarge)
            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DownloadTranslationButton()
                DeleteTranslationButton()
                UpdateTranslationsButton()
            }
            CheckBoxField(
                text = stringResource(R.string.check_at_startup),
                initialState = getCheckAtStartup(context),
                saveFunction = { checked ->
                    saveCheckAtStartup(context, checked)
                }
            )

            HorizontalDivider(Modifier.padding(12.dp))
            Text(stringResource(R.string.colors), style = styleLarge, modifier = modLarge)
            Text(stringResource(R.string.color_theme), style = styleMedium)
            ThemeButton(onThemeChange)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Text(stringResource(R.string.color_scheme), style = styleMedium)
                SchemeButton(onThemeChange)
            }

            HorizontalDivider(Modifier.padding(12.dp))
            Text(stringResource(R.string.bible_text), style = styleLarge, modifier = modLarge)
            Text(stringResource(R.string.alignment), style = styleMedium)
            ReadTextAlignmentButton()
            Text(stringResource(R.string.split_screen), style = styleMedium)
            SplitScreenButton()
            CheckBoxField(
                text = stringResource(R.string.show_verse_number),
                initialState = getShowVerseNumbers(context),
                saveFunction = { checked ->
                    saveShowVerseNumbers(context, checked)
                }
            )
            CheckBoxField(
                text = stringResource(R.string.infinite_scroll),
                initialState = getInfiniteScroll(context),
                saveFunction = { checked ->
                    saveInfiniteScroll(context, checked)
                }
            )

            HorizontalDivider(Modifier.padding(12.dp))
            Text(stringResource(R.string.notifications), style = styleLarge, modifier = modLarge)
            CheckBoxField(
                text = stringResource(R.string.download),
                initialState = getDownloadNotification(context),
                saveFunction = { checked ->
                    saveDownloadNotification(context, checked)
                }
            )

            HorizontalDivider(Modifier.padding(12.dp))
            Text(stringResource(R.string.backup), style = styleLarge, modifier = modLarge)
            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BackupButton(isUser = true, text = stringResource(R.string.documents))
                BackupButton(isData = true, text = stringResource(R.string.preferences))
            }
            HorizontalDivider(Modifier.padding(12.dp))
            Text(stringResource(R.string.about_us), style = styleLarge, modifier = modLarge)
            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LinkButton(
                    text = stringResource(R.string.source_repo),
                    url = "https://github.com/SchweGELBin/OpenBible2"
                )
                LinkButton(
                    text = stringResource(R.string.google_play),
                    url = "https://play.google.com/store/apps/details?id=com.schwegelbin.openbible"
                )
                LinkButton(
                    text = stringResource(R.string.source_getbible),
                    url = "https://getbible.net/docs"
                )
            }
        }
    }
}

@Composable
fun CheckBoxField(text: String, initialState: Boolean, saveFunction: (Boolean) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 15.dp)
        )
        val isChecked = remember { mutableStateOf(initialState) }
        Checkbox(checked = isChecked.value, onCheckedChange = {
            isChecked.value = it
            saveFunction(isChecked.value)
        })
    }
}

@Composable
fun LinkButton(text: String, url: String) {
    val context = LocalContext.current
    OutlinedButton(onClick = {
        val intent =
            Intent(Intent.ACTION_VIEW, url.toUri())
        context.startActivity(intent)
    }) { Text(text) }
}

@Composable
fun ReadTextAlignmentButton() {
    val context = LocalContext.current
    val selectedIndex = remember { mutableIntStateOf(getTextAlignmentInt(context)) }
    val options = ReadTextAlignment.entries

    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, option ->
            val label = when (option) {
                ReadTextAlignment.Start -> stringResource(R.string.alignment_start)
                ReadTextAlignment.Justify -> stringResource(R.string.alignment_justify)
            }
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                onClick = {
                    selectedIndex.intValue = index
                    saveTextAlignment(context, option)
                },
                selected = index == selectedIndex.intValue
            ) { Text(label) }
        }
    }
}

@Composable
fun SplitScreenButton() {
    val context = LocalContext.current
    val selectedIndex = remember { mutableIntStateOf(getSplitScreenInt(context)) }
    val options = SplitScreen.entries

    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, option ->
            val label = when (option) {
                SplitScreen.Off -> stringResource(R.string.off)
                SplitScreen.Vertical -> stringResource(R.string.vertical)
                SplitScreen.Horizontal -> stringResource(R.string.horizontal)
            }
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                onClick = {
                    selectedIndex.intValue = index
                    saveSplitScreen(context, option)
                },
                selected = index == selectedIndex.intValue
            ) { Text(label) }
        }
    }
}

@Composable
fun ThemeButton(onThemeChange: (Boolean?, Boolean?, Boolean?) -> Unit) {
    val context = LocalContext.current
    val selectedIndex = remember { mutableIntStateOf(getColorSchemeInt(context, true)) }
    val options = ThemeOption.entries

    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, option ->
            val label = when (option) {
                ThemeOption.System -> stringResource(R.string.theme_system)
                ThemeOption.Dark -> stringResource(R.string.theme_dark)
                ThemeOption.Light -> stringResource(R.string.theme_light)
                ThemeOption.Amoled -> stringResource(R.string.theme_amoled)
            }
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                onClick = {
                    selectedIndex.intValue = index
                    val (darkTheme, dynamicColor, amoled) = getMainThemeOptions(
                        context, themeOption = option
                    )
                    onThemeChange(darkTheme, dynamicColor, amoled)
                    saveColorScheme(context, theme = option)
                },
                selected = index == selectedIndex.intValue
            ) { Text(label) }
        }
    }
}

@Composable
fun SchemeButton(onThemeChange: (Boolean?, Boolean?, Boolean?) -> Unit) {
    val context = LocalContext.current
    val selectedIndex = remember { mutableIntStateOf(getColorSchemeInt(context, false)) }
    val options = SchemeOption.entries

    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, option ->
            val label = when (option) {
                SchemeOption.Static -> stringResource(R.string.scheme_static)
                SchemeOption.Dynamic -> stringResource(R.string.scheme_dynamic)
            }
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                onClick = {
                    selectedIndex.intValue = index
                    val (darkTheme, dynamicColor, amoled) = getMainThemeOptions(
                        context, schemeOption = option
                    )
                    onThemeChange(darkTheme, dynamicColor, amoled)
                    saveColorScheme(context, scheme = option)
                },
                selected = index == selectedIndex.intValue
            ) { Text(label) }
        }
    }
}

@Composable
fun DeleteTranslationButton() {
    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(false) }

    OutlinedButton(onClick = {
        showDialog.value = true
    }) { Text(stringResource(R.string.delete)) }

    if (showDialog.value) {
        val translationList =
            getTranslationList(context).map { it.nameWithoutExtension }

        Dialog(onDismissRequest = { showDialog.value = false }) {
            Surface(shape = RoundedCornerShape(size = 40.dp)) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = stringResource(R.string.delete_translation),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Card(
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        showDialog.value = !listTranslations(buttonFunction = { abbrev ->
                            getTranslation(context, abbrev).delete()
                        }, translationList)
                    }
                }
            }
        }
    }
}

@Composable
fun DownloadTranslationButton() {
    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(false) }
    val clicked = remember { mutableStateOf(false) }
    val indexPath = getIndexPath(context)

    OutlinedButton(onClick = {
        clicked.value = true
    }) { Text(stringResource(R.string.download)) }

    if (clicked.value) {
        saveIndex(context)
        WaitForFile(
            file = indexPath,
            onLoaded = { clicked.value = false; showDialog.value = true }
        )
    }

    if (showDialog.value) {
        Dialog(onDismissRequest = { showDialog.value = false }) {
            Surface(shape = RoundedCornerShape(size = 40.dp)) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = stringResource(R.string.download_translation),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Card(
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        showDialog.value = !listTranslations(buttonFunction = { abbrev ->
                            downloadTranslation(context, abbrev)
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun listTranslations(buttonFunction: (String) -> Unit, list: List<String>? = null): Boolean {
    val context = LocalContext.current
    val translations = remember { getTranslations(context) }
    val clicked = remember { mutableStateOf(false) }

    translations?.forEach { (lang, translations) ->
        val showLang = translations.any {
            list == null || list.contains(it.abbreviation)
        }
        if (showLang) {
            val language = getLanguageName(lang)
            Text(
                text = "$lang - $language",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                textAlign = TextAlign.Center
            )
            translations.forEach { translation ->
                val abbrev = translation.abbreviation
                if (list == null || list.contains(abbrev)) {
                    val name = translation.translation
                    TextButton(onClick = {
                        buttonFunction(abbrev)
                        clicked.value = true
                    }) { Text("$abbrev | $name") }
                }
            }
        }
    }
    return clicked.value
}

@Composable
fun UpdateTranslationsButton() {
    val context = LocalContext.current
    val clicked = remember { mutableStateOf(false) }
    OutlinedButton(onClick = { clicked.value = true }) { Text(stringResource(R.string.update)) }
    if (clicked.value) {
        clicked.value = false
        saveIndex(context)
        WaitForFile(
            onLoaded = { checkForUpdates(context, true) },
            file = getIndexPath(context)
        )
    }
}

@Composable
fun BackupButton(isUser: Boolean = false, isData: Boolean = false, text: String) {
    val context = LocalContext.current
    val clicked = remember { mutableStateOf(false) }
    OutlinedButton(onClick = { clicked.value = true }) { Text(text) }
    if (clicked.value) {
        clicked.value = false
        backupData(context, user = isUser, data = isData)
    }
}