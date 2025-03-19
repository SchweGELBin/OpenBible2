package com.schwegelbin.openbible.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.schwegelbin.openbible.R
import com.schwegelbin.openbible.logic.ReadTextAlignment
import com.schwegelbin.openbible.logic.SplitScreen
import com.schwegelbin.openbible.logic.checkTranslation
import com.schwegelbin.openbible.logic.getAppName
import com.schwegelbin.openbible.logic.getChapter
import com.schwegelbin.openbible.logic.getSelection
import com.schwegelbin.openbible.logic.getShowVerseNumbers
import com.schwegelbin.openbible.logic.getSplitScreen
import com.schwegelbin.openbible.logic.getTextAlignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadScreen(
    onNavigateToSelection: (Boolean) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToStart: () -> Unit
) {
    val appTitle = getAppName(
        stringResource(R.string.app_name),
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary
    )
    val split = getSplitScreen(LocalContext.current)

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(appTitle) },
            actions = {
                IconButton(onClick = { onNavigateToSettings() }) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = stringResource(R.string.settings)
                    )
                }
            })
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, bottom = 12.dp)
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                ReadCard(onNavigateToSelection, onNavigateToStart, split, false)
                if (split == SplitScreen.Vertical)
                    ReadCard(onNavigateToSelection, onNavigateToStart, split, true)
            }
            if (split == SplitScreen.Horizontal)
                ReadCard(onNavigateToSelection, onNavigateToStart, split, true)
        }
    }
}

@Composable
fun ReadCard(
    onNavigateToSelection: (Boolean) -> Unit,
    onNavigateToStart: () -> Unit,
    split: SplitScreen,
    isSplitScreen: Boolean
) {
    val context = LocalContext.current
    val selection = remember { mutableStateOf(getSelection(context, isSplitScreen)) }
    val (abbrev, book, chapter) = selection.value
    val translation = checkTranslation(context, abbrev, onNavigateToStart, isSplitScreen)
    val showVerseNumbers = remember { mutableStateOf(getShowVerseNumbers(context)) }
    val textAlignment = getTextAlignment(context)
    val (title, text) = getChapter(
        context,
        translation,
        book,
        chapter,
        showVerseNumbers.value,
        stringResource(R.string.error)
    )
    val mod = Modifier.fillMaxWidth()
    var outer = mod
    val textMod = Modifier
        .padding(8.dp)
        .verticalScroll(rememberScrollState())
    if (!isSplitScreen && split == SplitScreen.Vertical) outer = Modifier.fillMaxWidth(0.5f)
    if (!isSplitScreen && split == SplitScreen.Horizontal) outer = mod.fillMaxHeight(0.5f)
    Column(outer, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        var innerMod = Modifier.fillMaxWidth()
        if (isSplitScreen && split == SplitScreen.Horizontal) innerMod =
            innerMod.padding(top = 12.dp)
        ElevatedCard(
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = if (isSplitScreen && split == SplitScreen.Horizontal) mod.padding(top = 12.dp) else mod,
            onClick = { onNavigateToSelection(isSplitScreen) }
        ) {
            Text(
                text = title,
                modifier = Modifier
                    .padding(12.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
        if (split != SplitScreen.Horizontal) Spacer(Modifier)
        ElevatedCard(
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            when (textAlignment) {
                ReadTextAlignment.Start -> {
                    SelectionContainer {
                        Text(
                            text = text,
                            modifier = textMod
                        )
                    }
                }

                ReadTextAlignment.Justify -> {
                    Text(
                        text = text,
                        modifier = textMod,
                        textAlign = TextAlign.Justify
                    )
                }
            }
        }
    }
}