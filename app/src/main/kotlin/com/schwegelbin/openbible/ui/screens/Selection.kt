package com.schwegelbin.openbible.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.schwegelbin.openbible.R
import com.schwegelbin.openbible.logic.SelectMode
import com.schwegelbin.openbible.logic.TranslationMode
import com.schwegelbin.openbible.logic.getBookNames
import com.schwegelbin.openbible.logic.getCount
import com.schwegelbin.openbible.logic.getList
import com.schwegelbin.openbible.logic.getSelection
import com.schwegelbin.openbible.logic.getTranslationInfo
import com.schwegelbin.openbible.logic.saveSelection
import com.schwegelbin.openbible.logic.shorten

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionScreen(onNavigateToRead: () -> Unit, isSplitScreen: Boolean = false) {
    Scaffold(topBar = {
        TopAppBar(title = { Text(stringResource(R.string.selection)) }, navigationIcon = {
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
                .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Selection(onNavigateToRead, isSplitScreen)
        }
    }
}

@Composable
fun Selection(onNavigateToRead: () -> Unit, isSplitScreen: Boolean) {
    val context = LocalContext.current
    val selection = getSelection(context, isSplitScreen)
    val translation = remember { mutableStateOf(selection.first) }
    val book = remember { mutableIntStateOf(selection.second) }
    val chapter = remember { mutableIntStateOf(selection.third) }
    val selectedIndex = remember { mutableIntStateOf(1) }
    val selectedTIndex = remember { mutableIntStateOf(1) }
    val options = SelectMode.entries
    val tOptions = TranslationMode.entries
    val selectMode = remember { mutableStateOf(options[selectedIndex.intValue]) }
    val selectTMode = remember { mutableStateOf(tOptions[selectedTIndex.intValue]) }

    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, option ->
            val label = when (option) {
                SelectMode.Translation -> stringResource(R.string.translation)
                SelectMode.Book -> stringResource(R.string.book)
                SelectMode.Chapter -> stringResource(R.string.chapter)
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
    if (selectMode.value == SelectMode.Translation) {
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
            tOptions.forEachIndexed { index, option ->
                val label = when (option) {
                    TranslationMode.Info -> stringResource(R.string.information)
                    TranslationMode.Name -> stringResource(R.string.name)
                }
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = tOptions.size),
                    onClick = {
                        selectedTIndex.intValue = index
                        selectTMode.value = option
                    },
                    selected = index == selectedTIndex.intValue
                ) { Text(label) }
            }
        }
    }
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .verticalScroll(state = rememberScrollState(), enabled = true)
            .fillMaxWidth()
    ) {
        when (selectMode.value) {
            SelectMode.Translation -> {
                when (selectTMode.value) {
                    TranslationMode.Info -> Text(
                        getTranslationInfo(
                            LocalContext.current,
                            translation.value
                        )
                    )

                    TranslationMode.Name -> {
                        val translationList =
                            getList(context, "Translations").map { it.nameWithoutExtension }

                        listTranslations(buttonFunction = { abbrev ->
                            translation.value = abbrev

                            val (bookCount, chapterCount) = getCount(
                                context,
                                translation.value,
                                book.intValue
                            )
                            if (book.intValue > bookCount) {
                                book.intValue = 0
                                chapter.intValue = 0
                            }
                            if (chapter.intValue > chapterCount) {
                                chapter.intValue = 0
                            }
                            selectMode.value = SelectMode.Book
                            selectedIndex.intValue = 1
                            saveSelection(
                                context,
                                translation.value,
                                book.intValue,
                                chapter.intValue,
                                isSplitScreen
                            )
                        }, translationList)
                    }
                }
            }

            SelectMode.Book -> {
                val names = getBookNames(context, translation.value)
                val num = names.size - 1
                val buttonsPerRow = 3
                val length = 10

                for (i in 0..num step buttonsPerRow) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (j in 0..<buttonsPerRow) {
                            if (i + j <= num) {
                                var name = names[i + j]
                                name = shorten(name, length)
                                while (name.length < length) name += " "
                                TextButton(onClick = {
                                    book.intValue = i + j

                                    val (_, chapterCount) = getCount(
                                        context,
                                        translation.value,
                                        book.intValue
                                    )
                                    if (chapter.intValue > chapterCount) chapter.intValue = 0
                                    selectMode.value = SelectMode.Chapter
                                    selectedIndex.intValue = 2
                                    saveSelection(
                                        context,
                                        translation.value,
                                        book.intValue,
                                        chapter.intValue,
                                        isSplitScreen
                                    )
                                }) { Text((name)) }
                            }
                        }
                    }
                }
            }

            SelectMode.Chapter -> {
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