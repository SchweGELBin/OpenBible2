package com.schwegelbin.openbible.shared.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.schwegelbin.openbible.shared.FilePicker
import com.schwegelbin.openbible.shared.copyUriToFile
import com.schwegelbin.openbible.shared.getContext
import com.schwegelbin.openbible.shared.logic.SelectMode
import com.schwegelbin.openbible.shared.logic.Translation
import com.schwegelbin.openbible.shared.logic.downloadTranslation
import com.schwegelbin.openbible.shared.logic.getBookNames
import com.schwegelbin.openbible.shared.logic.getCount
import com.schwegelbin.openbible.shared.logic.getLanguageName
import com.schwegelbin.openbible.shared.logic.getSelection
import com.schwegelbin.openbible.shared.logic.getTranslation
import com.schwegelbin.openbible.shared.logic.getTranslationInfo
import com.schwegelbin.openbible.shared.logic.getTranslationList
import com.schwegelbin.openbible.shared.logic.getTranslationPath
import com.schwegelbin.openbible.shared.logic.getTranslations
import com.schwegelbin.openbible.shared.logic.getUpdateList
import com.schwegelbin.openbible.shared.logic.sanitizeAbbrev
import com.schwegelbin.openbible.shared.logic.saveSelection
import com.schwegelbin.openbible.shared.resources.Res
import com.schwegelbin.openbible.shared.resources.book
import com.schwegelbin.openbible.shared.resources.chapter
import com.schwegelbin.openbible.shared.resources.close
import com.schwegelbin.openbible.shared.resources.delete
import com.schwegelbin.openbible.shared.resources.file
import com.schwegelbin.openbible.shared.resources.import_additional
import com.schwegelbin.openbible.shared.resources.selection
import com.schwegelbin.openbible.shared.resources.translation
import com.schwegelbin.openbible.shared.resources.update
import com.schwegelbin.openbible.shared.ui.screens.BibleCache.getBible
import org.jetbrains.compose.resources.stringResource
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionScreen(
    onNavigateToRead: () -> Unit,
    isSplitScreen: Boolean = false,
    initialIndex: Int = 1
) {
    Scaffold(topBar = {
        TopAppBar(title = { Text(stringResource(Res.string.selection)) }, navigationIcon = {
            IconButton(onClick = { onNavigateToRead() }) {
                Icon(Icons.Filled.Close, stringResource(Res.string.close))
            }
        })
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Selection(onNavigateToRead, isSplitScreen, initialIndex)
        }
    }
}

@Composable
fun Selection(onNavigateToRead: () -> Unit, isSplitScreen: Boolean, initialIndex: Int) {
    val context = getContext()
    val selection = getSelection(context, isSplitScreen)
    val translation = remember { mutableStateOf(selection.first) }
    val book = remember { mutableIntStateOf(selection.second) }
    val chapter = remember { mutableIntStateOf(selection.third) }
    val selectedIndex = remember { mutableIntStateOf(initialIndex) }
    val options = SelectMode.entries
    val selectMode = remember { mutableStateOf(options[selectedIndex.intValue]) }
    val openDocumentLauncher = remember { mutableStateOf(false) }

    fun select(abbrev: String) {
        val newSelection = saveSelection(context, abbrev, isSplitScreen = isSplitScreen)
        translation.value = newSelection.first
        book.intValue = newSelection.second
        chapter.intValue = newSelection.third
    }

    val custom = remember { getTranslationList(context, true) }
    if (openDocumentLauncher.value) {
        openDocumentLauncher.value = false
        FilePicker("json", true) { uri ->
            uri?.let {
                val temp = getTranslation(context, "ex-tmp")
                copyUriToFile(context, it, temp)
                val name = sanitizeAbbrev(getBible(temp.path)?.abbreviation)
                if (name.isNotEmpty()) {
                    temp.copyTo(getTranslation(context, "/ex-$name"), overwrite = true)
                    select(name)
                }
                temp.delete()
            }
        }
    }

    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, option ->
            val label = when (option) {
                SelectMode.Translation -> stringResource(Res.string.translation)
                SelectMode.Book -> stringResource(Res.string.book)
                SelectMode.Chapter -> stringResource(Res.string.chapter)
            }
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                onClick = {
                    selectedIndex.intValue = index
                    selectMode.value = option
                },
                selected = index == selectedIndex.intValue
            ) { Text(label) }
        }
    }
    when (selectMode.value) {
        SelectMode.Translation -> {
            ElevatedCard(
                elevation = CardDefaults.cardElevation(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.65f)
            ) {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    ListTranslations(onSelect = { abbrev ->
                        if (!getTranslation(context, abbrev).exists())
                            downloadTranslation(context, abbrev)
                        select(abbrev)
                    })

                    HorizontalDivider(Modifier.padding(12.dp))
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        Text(
                            stringResource(Res.string.import_additional),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .weight(1f)
                                .padding(top = 12.dp)
                        )
                        /* TODO: Import translations via link
                        IconButton(onClick = {
                            val url = "https://example-link.to/custom.json" // Input Dialog
                            downloadFile(
                                context = context,
                                url = url,
                                name = "ex-tmp.json",
                                title = "Downloading Translation"
                            )
                            val temp = getTranslation(context, "ex-tmp")
                            val name = sanitizeAbbrev(getBible(temp.path)?.abbreviation)
                            if (name.isNotEmpty()) {
                                temp.copyTo(getTranslation(context, "/ex-$name"), overwrite = true)
                                File(getExternalPath(context)+"/custom-links.txt").appendText("$name->$url\n")
                                select(name)
                            }
                            temp.delete()
                        }) {
                            Icon(Icons.Filled.Download, stringResource(R.string.link))
                        }
                        */
                        IconButton(onClick = {
                            openDocumentLauncher.value = true
                        }) {
                            Icon(Icons.Filled.Upload, stringResource(Res.string.file))
                        }
                    }
                    ListTranslationsPart(
                        context,
                        onSelect = { abbrev -> select(abbrev) },
                        custom,
                        null
                    )
                }
            }
            ElevatedCard(
                elevation = CardDefaults.cardElevation(6.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                SelectionContainer {
                    Text(
                        text = getTranslationInfo(context, translation.value),
                        modifier = Modifier
                            .padding(10.dp)
                            .verticalScroll(rememberScrollState())
                    )
                }
            }
        }

        SelectMode.Book -> {
            ElevatedCard(
                elevation = CardDefaults.cardElevation(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                val names = getBookNames(context, translation.value)
                val num = names.size - 1
                val buttonsPerRow = 3

                for (i in 0..num step buttonsPerRow) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (j in 0..<buttonsPerRow) {
                            if (i + j <= num) {
                                val name = names[i + j]
                                Box(
                                    modifier = Modifier.weight(1f),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    TextButton(
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = {
                                            book.intValue = i + j
                                            val (_, chapterCount) = getCount(
                                                context,
                                                translation.value,
                                                book.intValue
                                            )
                                            if (chapter.intValue > chapterCount) chapter.intValue =
                                                0
                                            selectMode.value = SelectMode.Chapter
                                            selectedIndex.intValue = 2
                                            saveSelection(
                                                context,
                                                translation.value,
                                                book.intValue,
                                                chapter.intValue,
                                                isSplitScreen
                                            )
                                        }
                                    ) {
                                        Text(
                                            text = name,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        SelectMode.Chapter -> {
            ElevatedCard(
                elevation = CardDefaults.cardElevation(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                val (_, num) = getCount(context, translation.value, book.intValue)
                val buttonsPerRow = 4

                for (i in 0..num step buttonsPerRow) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (j in 0..<buttonsPerRow) {
                            if (i + j <= num) {
                                val name = (i + j + 1).toString()
                                TextButton(onClick = {
                                    chapter.intValue = i + j
                                    saveSelection(
                                        context,
                                        translation.value,
                                        book.intValue,
                                        chapter.intValue,
                                        isSplitScreen
                                    )
                                    onNavigateToRead()
                                }) { Text((name)) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ListTranslations(onSelect: (String) -> Unit) {
    val context = getContext()
    val translations = remember { getTranslations(context) }
    val installed = remember { getTranslationList(context, false) }

    ListTranslationsPart(context, onSelect, installed, translations, true)
    HorizontalDivider(Modifier.padding(12.dp))
    ListTranslationsPart(context, onSelect, installed, translations, false)
}

@Composable
fun ListTranslationsPart(
    context: Any?,
    onSelect: (String) -> Unit,
    list: Array<File>,
    translations: Map<String, List<Translation>>? = mapOf(),
    showInstalled: Boolean = true
) {
    val names = list.map { it.nameWithoutExtension }
    if (translations != null) {
        translations.forEach { (lang, translations) ->
            if ((showInstalled && translations.any { names.contains(it.abbreviation) }) || !showInstalled) {
                Text(
                    text = "$lang - ${getLanguageName(lang)}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    textAlign = TextAlign.Center
                )
                translations.forEach { translation ->
                    val abbrev = translation.abbreviation
                    if ((showInstalled && names.contains(abbrev)) ||
                        (!showInstalled && !names.contains(abbrev))
                    ) {
                        val name = translation.translation
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                        ) {
                            TextButton(
                                onClick = { onSelect(abbrev) },
                                modifier = Modifier.weight(1f)
                            )
                            { Text("$abbrev | $name") }
                            if (names.contains(abbrev)) {
                                if (getUpdateList(context, false).contains(abbrev)) {
                                    IconButton(onClick = { downloadTranslation(context, abbrev) }) {
                                        Icon(Icons.Filled.Update, stringResource(Res.string.update))
                                    }
                                }
                                IconButton(onClick = {
                                    if (getTranslationList(context, false).size > 1)
                                        getTranslation(context, abbrev).delete()
                                }) {
                                    Icon(Icons.Filled.Delete, stringResource(Res.string.delete))
                                }
                            }
                        }
                    }
                }
            }
        }
    } else {
        names.forEach { abbrev ->
            val name = getBible(getTranslationPath(context, abbrev))?.translation
            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                TextButton(
                    onClick = { onSelect(abbrev) },
                    modifier = Modifier.weight(1f)
                )
                { Text("$abbrev | $name") }
                IconButton(onClick = {
                    if (getTranslationList(context, false).size > 1)
                        getTranslation(context, abbrev).delete()
                }) {
                    Icon(Icons.Filled.Delete, stringResource(Res.string.delete))
                }
            }
        }
    }
}