package com.schwegelbin.openbible.ui.screens

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.schwegelbin.openbible.R
import com.schwegelbin.openbible.logic.SelectMode
import com.schwegelbin.openbible.logic.getBookNames
import com.schwegelbin.openbible.logic.getCount
import com.schwegelbin.openbible.logic.getSelection
import com.schwegelbin.openbible.logic.getTranslationInfo
import com.schwegelbin.openbible.logic.getTranslationList
import com.schwegelbin.openbible.logic.saveSelection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionScreen(
    onNavigateToRead: () -> Unit,
    isSplitScreen: Boolean = false,
    initialIndex: Int = 1
) {
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
            Selection(onNavigateToRead, isSplitScreen, initialIndex)
        }
    }
}

@Composable
fun Selection(onNavigateToRead: () -> Unit, isSplitScreen: Boolean, initialIndex: Int) {
    val context = LocalContext.current
    val selection = getSelection(context, isSplitScreen)
    val translation = remember { mutableStateOf(selection.first) }
    val book = remember { mutableIntStateOf(selection.second) }
    val chapter = remember { mutableIntStateOf(selection.third) }
    val selectedIndex = remember { mutableIntStateOf(initialIndex) }
    val options = SelectMode.entries
    val selectMode = remember { mutableStateOf(options[selectedIndex.intValue]) }

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
    when (selectMode.value) {
        SelectMode.Translation -> {
            ElevatedCard(
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.65f)
            ) {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    val translationList =
                        getTranslationList(context).map { it.nameWithoutExtension }

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
            ElevatedCard(
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
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
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
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
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
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