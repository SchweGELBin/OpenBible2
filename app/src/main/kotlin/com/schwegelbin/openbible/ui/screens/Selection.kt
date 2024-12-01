package com.schwegelbin.openbible.ui.screens

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
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.schwegelbin.openbible.logic.SelectMode
import com.schwegelbin.openbible.logic.defaultBook
import com.schwegelbin.openbible.logic.defaultChapter
import com.schwegelbin.openbible.logic.downloadTranslation
import com.schwegelbin.openbible.logic.getBookNames
import com.schwegelbin.openbible.logic.getCount
import com.schwegelbin.openbible.logic.getSelection
import com.schwegelbin.openbible.logic.getTranslations
import com.schwegelbin.openbible.logic.saveSelection
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.listDirectoryEntries

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionScreen(onNavigateToRead: () -> Unit) {
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
            Selection(onNavigateToRead)
        }
    }
}

@Composable
fun Selection(onNavigateToRead: () -> Unit) {
    val context = LocalContext.current
    val selection = remember { mutableStateOf(getSelection(context)) }
    var (translation, book, chapter) = selection.value
    var selectedIndex = remember { mutableIntStateOf(1) }
    val options = SelectMode.entries
    var selectMode = remember { mutableStateOf(options[selectedIndex.intValue]) }

    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, option ->
            val label = when (option) {
                SelectMode.Translation -> stringResource(R.string.translation)
                SelectMode.Book -> stringResource(R.string.book)
                SelectMode.Chapter -> stringResource(R.string.chapter)
                else -> ""
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
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .verticalScroll(state = rememberScrollState(), enabled = true)
            .fillMaxWidth()
    ) {
        when (selectMode.value) {
            SelectMode.Translation -> {
                val translationList = Path(
                    context.getExternalFilesDir("Checksums").toString()
                ).listDirectoryEntries().sorted()
                val translationMap = getTranslations(context)

                translationList.forEach { abbreviation ->
                    val abbrev = abbreviation.fileName.toString()
                    val name = translationMap?.get(abbrev)?.translation
                    TextButton(onClick = {
                        translation = abbrev

                        val (bookCount, chapterCount) = getCount(
                            context,
                            translation,
                            book
                        )
                        if (book > bookCount) {
                            book = 0
                            chapter = 0
                        }
                        if (chapter > chapterCount) {
                            chapter = 0
                        }
                        saveSelection(context, translation, book, chapter)
                        selectMode.value = SelectMode.Book
                        selectedIndex.intValue = 1
                    }) {
                        Text(
                            text = "$abbrev | $name",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            SelectMode.Book -> {
                val names = getBookNames(context, translation)
                val num = names.size - 1
                val buttonsPerRow = 3
                val length = 10

                for (i in 0..num step buttonsPerRow) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (j in 0..buttonsPerRow - 1) {
                            if (i + j <= num) {
                                var name = names[i + j].toString()
                                if (name.length > length) name =
                                    name.substring(0, length - 1).trim() + "."
                                while (name.length < length) name += " "
                                TextButton(onClick = {
                                    book = i + j

                                    val (_, chapterCount) = getCount(
                                        context,
                                        translation,
                                        book
                                    )
                                    if (chapter > chapterCount) chapter =
                                        0
                                    saveSelection(
                                        context,
                                        translation,
                                        book,
                                        chapter
                                    )
                                    selectMode.value = SelectMode.Chapter
                                    selectedIndex.intValue = 2
                                }) { Text((name)) }
                            }
                        }
                    }
                }
            }

            SelectMode.Chapter -> {
                val (_, num) = getCount(context, translation, book)
                val buttonsPerRow = 4

                for (i in 0..num step buttonsPerRow) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (j in 0..buttonsPerRow - 1) {
                            if (i + j <= num) {
                                val name = (i + j + 1).toString()
                                TextButton(onClick = {
                                    chapter = i + j
                                    saveSelection(
                                        context,
                                        translation,
                                        book,
                                        chapter
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
fun TranslationButton() {
    val context = LocalContext.current
    var showDialog = remember { mutableStateOf(false) }
    val indexPath = "${context.getExternalFilesDir("Index")}/translations.json"

    OutlinedButton(onClick = {
        if (File(indexPath).exists()) showDialog.value = true
    }) { Text(text = stringResource(R.string.download_translation)) }

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
                        translationItems?.forEach { (abbreviation, translation) ->
                            TextButton(onClick = {
                                downloadTranslation(context, abbreviation)
                                showDialog.value = false
                            }) {
                                Text(
                                    text = "$abbreviation | $translation",
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TranslationCard(onSelected: () -> Unit) {
    val context = LocalContext.current

    val translationMap = remember { getTranslations(context) }
    val translationItems = translationMap?.values?.map {
        it.abbreviation to it.translation
    }

    Text(
        text = stringResource(R.string.download_translation),
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )

    Card(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        translationItems?.forEach { (abbreviation, translation) ->
            TextButton(onClick = {
                downloadTranslation(context, abbreviation)
                saveSelection(
                    context,
                    translation = abbreviation,
                    book = defaultBook,
                    chapter = defaultChapter
                )
                onSelected()
            }) { Text("$abbreviation | $translation") }
        }
    }
}