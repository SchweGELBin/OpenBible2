package com.schwegelbin.openbible.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
                .padding(innerPadding)
                .padding(start = 8.dp, end = 8.dp, bottom = 12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row {
                ReadCard(onNavigateToSelection, onNavigateToStart, false)
                if (getSplitScreen(LocalContext.current))
                    ReadCard(onNavigateToSelection, onNavigateToStart, true)
            }
        }
    }
}

@Composable
fun ReadCard(
    onNavigateToSelection: (Boolean) -> Unit,
    onNavigateToStart: () -> Unit,
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
    var mod = Modifier.fillMaxWidth()
    if (!isSplitScreen && getSplitScreen(LocalContext.current)) mod = Modifier.fillMaxWidth(0.5f)
    Column(mod) {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier.fillMaxWidth(),
            onClick = { onNavigateToSelection(isSplitScreen) }
        ) {
            Text(
                text = title,
                modifier = Modifier
                    .padding(12.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
        ElevatedCard(
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .verticalScroll(state = rememberScrollState(), enabled = true)
                .fillMaxWidth()
        ) {
            when (textAlignment) {
                ReadTextAlignment.Start -> {
                    SelectionContainer {
                        Text(
                            text = text,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                ReadTextAlignment.Justify -> {
                    Text(
                        text = text,
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Justify
                    )
                }
            }
        }
    }
}