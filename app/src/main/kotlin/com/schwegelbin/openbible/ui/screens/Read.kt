package com.schwegelbin.openbible.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
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
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.schwegelbin.openbible.R
import com.schwegelbin.openbible.logic.ReadTextAlignment
import com.schwegelbin.openbible.logic.SplitScreen
import com.schwegelbin.openbible.logic.checkTranslation
import com.schwegelbin.openbible.logic.getAppName
import com.schwegelbin.openbible.logic.getChapter
import com.schwegelbin.openbible.logic.getFontSize
import com.schwegelbin.openbible.logic.getSelection
import com.schwegelbin.openbible.logic.getShowVerseNumbers
import com.schwegelbin.openbible.logic.getSplitScreen
import com.schwegelbin.openbible.logic.getTextAlignment
import com.schwegelbin.openbible.logic.turnChapter
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadScreen(
    onNavigateToBookmarks: () -> Unit,
    onNavigateToRead: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToSelection: (Boolean, Int) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToStart: () -> Unit,
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
                HamburgerMenu(
                    onNavigateToBookmarks,
                    onNavigateToSearch,
                    onNavigateToSettings
                )
            }
        )
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, bottom = 12.dp)
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                ReadCard(onNavigateToSelection, onNavigateToStart, onNavigateToRead, split, false)
                if (split == SplitScreen.Vertical)
                    ReadCard(
                        onNavigateToSelection,
                        onNavigateToStart,
                        onNavigateToRead,
                        split,
                        true
                    )
            }
            if (split == SplitScreen.Horizontal)
                ReadCard(onNavigateToSelection, onNavigateToStart, onNavigateToRead, split, true)
        }
    }
}

@Composable
fun ReadCard(
    onNavigateToSelection: (Boolean, Int) -> Unit,
    onNavigateToStart: () -> Unit,
    onNavigateToRead: () -> Unit,
    split: SplitScreen,
    isSplitScreen: Boolean
) {
    val context = LocalContext.current
    val selection = remember { mutableStateOf(getSelection(context, isSplitScreen)) }
    val (abbrev, book, chapter) = selection.value
    val translation = checkTranslation(context, abbrev, onNavigateToStart, isSplitScreen)
    val showVerseNumbers = remember { mutableStateOf(getShowVerseNumbers(context)) }
    val textAlignment = getTextAlignment(context)
    val (translationName, chapterName, text) = getChapter(
        context,
        translation,
        book,
        chapter,
        showVerseNumbers.value,
        stringResource(R.string.error)
    )
    val mod = Modifier.fillMaxWidth()
    var outer = mod
    val fontSize = getFontSize(context)
    val textScale = remember { mutableFloatStateOf(fontSize.start) }
    val zoomState = rememberTransformableState { zoomChange, _, _ ->
        textScale.floatValue =
            min(max(textScale.floatValue * zoomChange, fontSize.start), fontSize.endInclusive)
    }
    val textStyle = MaterialTheme.typography.bodyLarge
    val textMod = Modifier
        .padding(8.dp)
        .verticalScroll(rememberScrollState())
    if (!isSplitScreen && split == SplitScreen.Vertical) outer = Modifier.fillMaxWidth(0.5f)
    if (!isSplitScreen && split == SplitScreen.Horizontal) outer = mod.fillMaxHeight(0.5f)
    Column(outer, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = if (isSplitScreen && split == SplitScreen.Horizontal) mod.padding(
                top = 12.dp
            ) else mod,
            onClick = { onNavigateToSelection(isSplitScreen, 1) }
        ) {
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TurnButton(false, isSplitScreen, onNavigateToRead)
                TextButton(
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateToSelection(isSplitScreen, 0) }
                ) {
                    Text(
                        text = translationName,
                        textAlign = TextAlign.Center
                    )
                }
                Text(
                    text = chapterName,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                TurnButton(true, isSplitScreen, onNavigateToRead)
            }
        }
        if (split != SplitScreen.Horizontal) Spacer(Modifier)
        ElevatedCard(
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .transformable(zoomState)
                .pointerInput(Unit) {
                    detectTapGestures(onDoubleTap = {
                        textScale.floatValue =
                            if (textScale.floatValue != fontSize.start) fontSize.start else fontSize.endInclusive
                    })
                }
        ) {
            when (textAlignment) {
                ReadTextAlignment.Start -> {
                    SelectionContainer {
                        Text(
                            text = text,
                            modifier = textMod,
                            fontSize = (textScale.floatValue * textStyle.fontSize.value).sp,
                            lineHeight = (textScale.floatValue * textStyle.lineHeight.value).sp
                        )
                    }
                }

                ReadTextAlignment.Justify -> {
                    Text(
                        text = text,
                        modifier = textMod,
                        fontSize = (textScale.floatValue * textStyle.fontSize.value).sp,
                        lineHeight = (textScale.floatValue * textStyle.lineHeight.value).sp,
                        textAlign = TextAlign.Justify
                    )
                }
            }
        }
    }
}


@Composable
fun TurnButton(next: Boolean, isSplitScreen: Boolean, onNavigateToRead: () -> Unit) {
    val context = LocalContext.current
    IconButton(onClick = { turnChapter(context, next, isSplitScreen, onNavigateToRead) }) {
        if (next) {
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = stringResource(R.string.next)
            )
        } else {
            Icon(
                imageVector = Icons.Filled.ChevronLeft,
                contentDescription = stringResource(R.string.previous)
            )
        }
    }
}

@Composable
fun HamburgerMenu(
    onNavigateToBookmarks: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val expanded = remember { mutableStateOf(false) }
    IconButton(onClick = { expanded.value = !expanded.value }) {
        Icon(
            imageVector = Icons.Filled.Menu,
            contentDescription = stringResource(R.string.menu)
        )
    }
    DropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false },
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface)
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(R.string.settings)) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = null
                )
            },
            onClick = { expanded.value = false; onNavigateToSettings() }
        )
        /* TODO: Implement Bookmarks
        DropdownMenuItem(
            text = { Text(stringResource(R.string.bookmarks)) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.Bookmarks,
                    contentDescription = null
                )
            },
            onClick = { expanded.value = false; onNavigateToBookmarks() }
        )
        */
        DropdownMenuItem(
            text = { Text(stringResource(R.string.search)) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null
                )
            },
            onClick = { expanded.value = false; onNavigateToSearch() }
        )
    }
}