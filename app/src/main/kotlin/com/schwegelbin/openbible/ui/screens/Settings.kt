package com.schwegelbin.openbible.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.schwegelbin.openbible.R
import com.schwegelbin.openbible.logic.ReadTextAlignment
import com.schwegelbin.openbible.logic.SchemeOption
import com.schwegelbin.openbible.logic.ThemeOption
import com.schwegelbin.openbible.logic.getColorSchemeInt
import com.schwegelbin.openbible.logic.getReadTextAlignmentInt
import com.schwegelbin.openbible.logic.saveChecksum
import com.schwegelbin.openbible.logic.saveColorScheme
import com.schwegelbin.openbible.logic.saveIndex
import com.schwegelbin.openbible.logic.saveReadTextStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onClose: () -> Unit) {
    Scaffold(topBar = {
        TopAppBar(title = { Text(stringResource(R.string.settings)) }, navigationIcon = {
            IconButton(onClick = { onClose() }) {
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
                .verticalScroll(state = rememberScrollState(), enabled = true)
        ) {
            Text(stringResource(R.string.index), style = MaterialTheme.typography.titleLarge)
            IndexButton()

            HorizontalDivider(Modifier.padding(12.dp))
            Text(stringResource(R.string.color_theme), style = MaterialTheme.typography.titleLarge)
            ThemeButton()

            Spacer(Modifier.padding(12.dp))
            Text(stringResource(R.string.color_scheme), style = MaterialTheme.typography.titleLarge)
            SchemeButton()

            HorizontalDivider(Modifier.padding(12.dp))
            Text(
                stringResource(R.string.bible_text_alignment),
                style = MaterialTheme.typography.titleLarge
            )
            ReadTextAlignmentButton()

            HorizontalDivider(Modifier.padding(12.dp))
            Text(stringResource(R.string.about_us), style = MaterialTheme.typography.titleLarge)
            RepoButton()
        }
    }
}

@Composable
fun IndexButton() {
    val context = LocalContext.current
    OutlinedButton(onClick = {
        saveIndex(context)
        saveChecksum(context)
    }) { Text(stringResource(R.string.update_index)) }
}

@Composable
fun ReadTextAlignmentButton() {
    val context = LocalContext.current
    var selectedIndex = remember { mutableIntStateOf(getReadTextAlignmentInt(context)) }
    val options = ReadTextAlignment.entries

    SingleChoiceSegmentedButtonRow {
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
                    saveReadTextStyle(context, option)
                },
                selected = index == selectedIndex.intValue
            ) { Text(label) }
        }
    }
}

@Composable
fun ThemeButton() {
    val context = LocalContext.current
    var selectedIndex = remember { mutableIntStateOf(getColorSchemeInt(context, true)) }
    val options = ThemeOption.entries

    SingleChoiceSegmentedButtonRow {
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
                    if (option == ThemeOption.Amoled) {
                        saveColorScheme(context, theme = option, scheme = SchemeOption.Static)
                    } else {
                        saveColorScheme(context, theme = option, scheme = null)
                    }
                },
                selected = index == selectedIndex.intValue
            ) { Text(label) }
        }
    }
}

@Composable
fun SchemeButton() {
    val context = LocalContext.current
    var selectedIndex = remember { mutableIntStateOf(getColorSchemeInt(context, false)) }
    val options = SchemeOption.entries

    SingleChoiceSegmentedButtonRow {
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
                    saveColorScheme(context, theme = null, scheme = option)
                    if (option == SchemeOption.Dynamic) {
                        saveColorScheme(context, theme = ThemeOption.System, scheme = option)
                    } else {
                        saveColorScheme(context, theme = null, scheme = option)
                    }
                },
                selected = index == selectedIndex.intValue
            ) { Text(label) }
        }
    }
}

@Composable
fun RepoButton() {
    val context = LocalContext.current
    OutlinedButton(onClick = {
        val intent =
            Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/SchweGELBin/OpenBible2"))
        startActivity(context, intent, null)
    }) { Text(stringResource(R.string.source_repo)) }
}