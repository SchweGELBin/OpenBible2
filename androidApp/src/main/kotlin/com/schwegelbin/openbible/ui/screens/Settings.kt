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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.schwegelbin.openbible.logic.ReadTextAlignment
import com.schwegelbin.openbible.logic.SchemeOption
import com.schwegelbin.openbible.logic.SplitScreen
import com.schwegelbin.openbible.logic.ThemeOption
import com.schwegelbin.openbible.logic.backupData
import com.schwegelbin.openbible.logic.getCheckAtStartup
import com.schwegelbin.openbible.logic.getColorSchemeInt
import com.schwegelbin.openbible.logic.getDownloadNotification
import com.schwegelbin.openbible.logic.getFontSize
import com.schwegelbin.openbible.logic.getMainThemeOptions
import com.schwegelbin.openbible.logic.getShowVerseNumbers
import com.schwegelbin.openbible.logic.getSplitScreenInt
import com.schwegelbin.openbible.logic.getTextAlignmentInt
import com.schwegelbin.openbible.logic.saveCheckAtStartup
import com.schwegelbin.openbible.logic.saveColorScheme
import com.schwegelbin.openbible.logic.saveDownloadNotification
import com.schwegelbin.openbible.logic.saveFontSize
import com.schwegelbin.openbible.logic.saveShowVerseNumbers
import com.schwegelbin.openbible.logic.saveSplitScreen
import com.schwegelbin.openbible.logic.saveTextAlignment
import com.schwegelbin.openbible.shared.resources.Res
import com.schwegelbin.openbible.shared.resources.about_us
import com.schwegelbin.openbible.shared.resources.alignment
import com.schwegelbin.openbible.shared.resources.alignment_justify
import com.schwegelbin.openbible.shared.resources.alignment_start
import com.schwegelbin.openbible.shared.resources.backup
import com.schwegelbin.openbible.shared.resources.backup_completed
import com.schwegelbin.openbible.shared.resources.bible_text
import com.schwegelbin.openbible.shared.resources.check_at_startup
import com.schwegelbin.openbible.shared.resources.close
import com.schwegelbin.openbible.shared.resources.color_scheme
import com.schwegelbin.openbible.shared.resources.color_theme
import com.schwegelbin.openbible.shared.resources.colors
import com.schwegelbin.openbible.shared.resources.contact
import com.schwegelbin.openbible.shared.resources.documents
import com.schwegelbin.openbible.shared.resources.download
import com.schwegelbin.openbible.shared.resources.font_size
import com.schwegelbin.openbible.shared.resources.google_play
import com.schwegelbin.openbible.shared.resources.horizontal
import com.schwegelbin.openbible.shared.resources.notifications
import com.schwegelbin.openbible.shared.resources.off
import com.schwegelbin.openbible.shared.resources.preferences
import com.schwegelbin.openbible.shared.resources.scheme_dynamic
import com.schwegelbin.openbible.shared.resources.scheme_static
import com.schwegelbin.openbible.shared.resources.settings
import com.schwegelbin.openbible.shared.resources.show_verse_number
import com.schwegelbin.openbible.shared.resources.source_getbible
import com.schwegelbin.openbible.shared.resources.source_repo
import com.schwegelbin.openbible.shared.resources.split_screen
import com.schwegelbin.openbible.shared.resources.theme_amoled
import com.schwegelbin.openbible.shared.resources.theme_dark
import com.schwegelbin.openbible.shared.resources.theme_light
import com.schwegelbin.openbible.shared.resources.theme_system
import com.schwegelbin.openbible.shared.resources.translation
import com.schwegelbin.openbible.shared.resources.vertical
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToRead: () -> Unit,
    onThemeChange: (Boolean?, Boolean?, Boolean?) -> Unit
) {
    val context = LocalContext.current
    Scaffold(topBar = {
        TopAppBar(title = { Text(stringResource(Res.string.settings)) }, navigationIcon = {
            IconButton(onClick = { onNavigateToRead() }) {
                Icon(
                    Icons.Filled.Close, stringResource(Res.string.close)
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
            Text(stringResource(Res.string.translation), style = styleLarge, modifier = modLarge)
            SettingsField(
                text = stringResource(Res.string.check_at_startup),
                initialState = getCheckAtStartup(context),
                saveFunction = { checked ->
                    saveCheckAtStartup(context, checked)
                }
            )

            /* TODO: Implement Language Change
             * https://github.com/SchweGELBin/OpenBible2/issues/13
            HorizontalDivider(Modifier.padding(12.dp))
            Text(stringResource(R.string.locale), style = styleLarge, modifier = modLarge)
            Text(stringResource(R.string.language), style = styleMedium)
            LanguageButton(onLanguageChange)
            */

            HorizontalDivider(Modifier.padding(12.dp))
            Text(stringResource(Res.string.colors), style = styleLarge, modifier = modLarge)
            Text(stringResource(Res.string.color_theme), style = styleMedium)
            ThemeButton(onThemeChange)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Text(stringResource(Res.string.color_scheme), style = styleMedium)
                SchemeButton(onThemeChange)
            }

            HorizontalDivider(Modifier.padding(12.dp))
            Text(stringResource(Res.string.bible_text), style = styleLarge, modifier = modLarge)
            Text(stringResource(Res.string.alignment), style = styleMedium)
            ReadTextAlignmentButton()
            Text(stringResource(Res.string.split_screen), style = styleMedium)
            SplitScreenButton()
            Text(stringResource(Res.string.font_size), style = styleMedium)
            FontSizeSlider()
            SettingsField(
                text = stringResource(Res.string.show_verse_number),
                initialState = getShowVerseNumbers(context),
                saveFunction = { checked ->
                    saveShowVerseNumbers(context, checked)
                }
            )
            /* TODO: Implement Infinite Scroll
             * https://github.com/SchweGELBin/OpenBible2/issues/16
            SettingsField(
                text = stringResource(R.string.infinite_scroll),
                initialState = getInfiniteScroll(context),
                saveFunction = { checked ->
                    saveInfiniteScroll(context, checked)
                }
            )
             */

            HorizontalDivider(Modifier.padding(12.dp))
            Text(stringResource(Res.string.notifications), style = styleLarge, modifier = modLarge)
            SettingsField(
                text = stringResource(Res.string.download),
                initialState = getDownloadNotification(context),
                saveFunction = { checked ->
                    saveDownloadNotification(context, checked)
                }
            )
            /* TODO: Implement Verse of the Day
             * https://github.com/SchweGELBin/OpenBible2/issues/19
            SettingsField(
                text = stringResource(R.string.verse_of_the_day),
                initialState = getVerseOfTheDay(context),
                saveFunction = { checked ->
                    saveVerseOfTheDay(context, checked)
                }
            )
             */

            HorizontalDivider(Modifier.padding(12.dp))
            Text(stringResource(Res.string.backup), style = styleLarge, modifier = modLarge)
            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BackupButton(isUser = true, text = stringResource(Res.string.documents))
                BackupButton(isData = true, text = stringResource(Res.string.preferences))
            }
            HorizontalDivider(Modifier.padding(12.dp))
            Text(stringResource(Res.string.about_us), style = styleLarge, modifier = modLarge)
            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LinkButton(
                    text = stringResource(Res.string.source_repo),
                    url = "https://github.com/SchweGELBin/OpenBible2"
                )
                LinkButton(
                    text = stringResource(Res.string.google_play),
                    url = "https://play.google.com/store/apps/details?id=com.schwegelbin.openbible"
                )
                LinkButton(
                    text = stringResource(Res.string.contact),
                    url = "mailto:schwegelbin@gmail.com"
                )
                LinkButton(
                    text = stringResource(Res.string.source_getbible),
                    url = "https://getbible.life/docs"
                )
            }
        }
    }
}

@Composable
fun SettingsField(text: String, initialState: Boolean, saveFunction: (Boolean) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(top = 15.dp)
                .weight(1f)
        )
        val isChecked = remember { mutableStateOf(initialState) }
        Switch(checked = isChecked.value, onCheckedChange = {
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
                ReadTextAlignment.Start -> stringResource(Res.string.alignment_start)
                ReadTextAlignment.Justify -> stringResource(Res.string.alignment_justify)
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
                SplitScreen.Off -> stringResource(Res.string.off)
                SplitScreen.Vertical -> stringResource(Res.string.vertical)
                SplitScreen.Horizontal -> stringResource(Res.string.horizontal)
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
                ThemeOption.System -> stringResource(Res.string.theme_system)
                ThemeOption.Dark -> stringResource(Res.string.theme_dark)
                ThemeOption.Light -> stringResource(Res.string.theme_light)
                ThemeOption.Amoled -> stringResource(Res.string.theme_amoled)
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
                SchemeOption.Static -> stringResource(Res.string.scheme_static)
                SchemeOption.Dynamic -> stringResource(Res.string.scheme_dynamic)
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
fun FontSizeSlider() {
    val context = LocalContext.current
    val sliderPosition = remember { mutableStateOf(getFontSize(context)) }
    RangeSlider(
        value = sliderPosition.value,
        steps = 9,
        onValueChange = { range -> sliderPosition.value = range },
        valueRange = 1f..2f,
        onValueChangeFinished = { saveFontSize(context, sliderPosition.value) },
    )
}

@Composable
fun BackupButton(isUser: Boolean = false, isData: Boolean = false, text: String) {
    val context = LocalContext.current
    val clicked = remember { mutableStateOf(false) }
    OutlinedButton(onClick = { clicked.value = true }) { Text(text) }
    if (clicked.value) {
        clicked.value = false
        backupData(
            context,
            user = isUser,
            data = isData,
            stringResource(Res.string.backup_completed)
        )
    }
}