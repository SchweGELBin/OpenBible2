package com.schwegelbin.openbible.ui.screens

import android.content.Intent
import android.net.Uri
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
import com.schwegelbin.openbible.R
import com.schwegelbin.openbible.logic.ReadTextAlignment
import com.schwegelbin.openbible.logic.SchemeOption
import com.schwegelbin.openbible.logic.ThemeOption
import com.schwegelbin.openbible.logic.checkForUpdates
import com.schwegelbin.openbible.logic.cleanUpTranslations
import com.schwegelbin.openbible.logic.downloadTranslation
import com.schwegelbin.openbible.logic.getCheckAtStartup
import com.schwegelbin.openbible.logic.getColorSchemeInt
import com.schwegelbin.openbible.logic.getList
import com.schwegelbin.openbible.logic.getMainThemeOptions
import com.schwegelbin.openbible.logic.getShowVerseNumbers
import com.schwegelbin.openbible.logic.getSplitScreen
import com.schwegelbin.openbible.logic.getTextAlignmentInt
import com.schwegelbin.openbible.logic.getTranslations
import com.schwegelbin.openbible.logic.saveCheckAtStartup
import com.schwegelbin.openbible.logic.saveColorScheme
import com.schwegelbin.openbible.logic.saveNewIndex
import com.schwegelbin.openbible.logic.saveShowVerseNumbers
import com.schwegelbin.openbible.logic.saveSplitScreen
import com.schwegelbin.openbible.logic.saveTextStyle
import java.io.File

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
            CheckBoxField(
                text = stringResource(R.string.show_verse_number),
                initialState = getShowVerseNumbers(context),
                saveFunction = { checked ->
                    saveShowVerseNumbers(context, checked)
                }
            )
            CheckBoxField(
                text = stringResource(R.string.split_screen),
                initialState = getSplitScreen(context),
                saveFunction = { checked ->
                    saveSplitScreen(context, checked)
                }
            )

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
            Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }) { Text(text) }
}

@Composable
fun ReadTextAlignmentButton() {
    val context = LocalContext.current
    var selectedIndex = remember { mutableIntStateOf(getTextAlignmentInt(context)) }
    val options = ReadTextAlignment.entries

    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, option ->
            val label = when (option) {
                ReadTextAlignment.Start -> stringResource(R.string.alignment_start)
                ReadTextAlignment.Justify -> stringResource(R.string.alignment_justify)
                else -> ""
            }
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                onClick = {
                    selectedIndex.intValue = index
                    saveTextStyle(context, option)
                },
                selected = index == selectedIndex.intValue
            ) { Text(label) }
        }
    }
}

@Composable
fun ThemeButton(onThemeChange: (Boolean?, Boolean?, Boolean?) -> Unit) {
    val context = LocalContext.current
    var selectedIndex = remember { mutableIntStateOf(getColorSchemeInt(context, true)) }
    val options = ThemeOption.entries

    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, option ->
            val label = when (option) {
                ThemeOption.System -> stringResource(R.string.theme_system)
                ThemeOption.Dark -> stringResource(R.string.theme_dark)
                ThemeOption.Light -> stringResource(R.string.theme_light)
                ThemeOption.Amoled -> stringResource(R.string.theme_amoled)
                else -> ""
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
    var selectedIndex = remember { mutableIntStateOf(getColorSchemeInt(context, false)) }
    val options = SchemeOption.entries

    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, option ->
            val label = when (option) {
                SchemeOption.Static -> stringResource(R.string.scheme_static)
                SchemeOption.Dynamic -> stringResource(R.string.scheme_dynamic)
                else -> ""
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
    var showDialog = remember { mutableStateOf(false) }

    OutlinedButton(onClick = {
        showDialog.value = true
        cleanUpTranslations(context)
    }) { Text(stringResource(R.string.delete)) }

    if (showDialog.value) {
        val translationList =
            getList(context, "Translations").map { it.nameWithoutExtension }
        val translationMap = remember { getTranslations(context) }
        val translationItems = translationMap?.values?.map {
            it.abbreviation to it.translation
        }

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
                        translationItems?.forEach { (abbrev, translation) ->
                            if (abbrev in translationList) {
                                TextButton(onClick = {
                                    File("${context.getExternalFilesDir("Translations")}/${abbrev}.json").delete()
                                    File("${context.getExternalFilesDir("Checksums")}/${abbrev}").delete()
                                    showDialog.value = false
                                }) { Text("$abbrev | $translation") }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DownloadTranslationButton() {
    val context = LocalContext.current
    var showDialog = remember { mutableStateOf(false) }
    var clicked = remember { mutableStateOf(false) }
    val indexPath = "${context.getExternalFilesDir("Index")}/translations.json"

    OutlinedButton(onClick = {
        clicked.value = true
    }) { Text(stringResource(R.string.download)) }

    if (clicked.value) {
        saveNewIndex(context)
        WaitForFile(
            file = indexPath,
            onLoaded = { clicked.value = false; showDialog.value = true }
        )
    }

    if (showDialog.value) {
        val translationMap = remember { getTranslations(context) }
        val translationItems = translationMap?.values?.map {
            it.abbreviation to it.translation
        }

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
                        translationItems?.forEach { (abbrev, translation) ->
                            TextButton(onClick = {
                                downloadTranslation(context, abbrev)
                                showDialog.value = false
                            }) { Text("$abbrev | $translation") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UpdateTranslationsButton() {
    val context = LocalContext.current
    var clicked = remember { mutableStateOf(false) }
    OutlinedButton(onClick = { clicked.value = true }) { Text(stringResource(R.string.update)) }
    if (clicked.value) {
        clicked.value = false
        saveNewIndex(context)
        val path = context.getExternalFilesDir("Index")
        WaitForFile(
            onLoaded = { checkForUpdates(context, true) },
            file = "${path}/translations.json"
        )
    }
}