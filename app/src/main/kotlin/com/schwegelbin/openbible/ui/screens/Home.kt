package com.schwegelbin.openbible.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.schwegelbin.openbible.logic.downloadTranslation
import com.schwegelbin.openbible.logic.getBookNames
import com.schwegelbin.openbible.logic.getCount
import com.schwegelbin.openbible.logic.getSelection
import com.schwegelbin.openbible.logic.getTranslations
import com.schwegelbin.openbible.logic.saveSelection
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.listDirectoryEntries

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(horizontal = 20.dp)
            .verticalScroll(state = rememberScrollState(), enabled = true),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        TranslationCard()
        SelectCard(SelectMode.Translation)
        SelectCard(SelectMode.Book)
        SelectCard(SelectMode.Chapter)
    }
}

@Composable
fun TranslationCard() {
    val context = LocalContext.current
    var showDialog = remember { mutableStateOf(false) }
    val indexPath = "${context.getExternalFilesDir("Index")}/translations.json"

    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth(),
        onClick = { if (File(indexPath).exists()) showDialog.value = true }) {
        Text(
            text = stringResource(R.string.download_translation),
            modifier = Modifier.padding(16.dp)
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
fun SelectCard(selectMode: SelectMode) {
    val context = LocalContext.current
    var showDialog = remember { mutableStateOf(false) }
    var (translation, book, chapter) = getSelection(context)
    val translationPath =
        "${context.getExternalFilesDir("Translations")}/${translation}.json"

    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            if (Path(translationPath).exists()) showDialog.value = true
        }) {
        when (selectMode) {
            SelectMode.Translation -> {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = stringResource(R.string.choose_translation)
                )
            }

            SelectMode.Book -> {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = stringResource(R.string.choose_book)
                )
            }

            SelectMode.Chapter -> {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = stringResource(R.string.choose_chapter)
                )
            }
        }
    }

    if (showDialog.value) {
        Dialog(onDismissRequest = { showDialog.value = false }) {
            Surface(
                shape = RoundedCornerShape(size = 40.dp), modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    when (selectMode) {
                        SelectMode.Translation -> {
                            Text(
                                text = stringResource(R.string.choose_translation),
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                            val translationList = Path(
                                context.getExternalFilesDir("Checksums").toString()
                            ).listDirectoryEntries().sorted()
                            val translationMap = getTranslations(context)

                            Card(
                                modifier = Modifier.verticalScroll(rememberScrollState()),
                                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                            ) {
                                translationList.forEach { abbreviation ->
                                    val abbrev = abbreviation.fileName.toString()
                                    val name = translationMap?.get(abbrev)?.translation
                                    TextButton(onClick = {
                                        translation = abbrev
                                        showDialog.value = false

                                        val (bookCount, chapterCount) = getCount(context, translation, book)
                                        if (book > bookCount) {
                                            book = 0
                                            chapter = 0
                                        }
                                        if (chapter > chapterCount) {
                                            chapter = 0
                                        }
                                        saveSelection(context, translation, book, chapter)
                                    }) {
                                        Text(
                                            text = "$abbrev | $name",
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }

                        SelectMode.Book -> {
                            Text(
                                text = stringResource(R.string.choose_book),
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                            val names = getBookNames(context, translation)
                            val num = names.size - 1
                            val buttonsPerRow = 3
                            val length = 7

                            Card(
                                modifier = Modifier.verticalScroll(rememberScrollState()),
                                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                            ) {
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
                                                    showDialog.value = false

                                                    val (_, chapterCount) = getCount(context, translation, book)
                                                    if (chapter > chapterCount) chapter =
                                                        0
                                                    saveSelection(context, translation, book, chapter)
                                                }) { Text((name)) }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        SelectMode.Chapter -> {
                            Text(
                                text = stringResource(R.string.choose_chapter),
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                            val (_, num) = getCount(context, translation, book)
                            val buttonsPerRow = 4

                            Card(
                                modifier = Modifier.verticalScroll(rememberScrollState()),
                                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                            ) {
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
                                                    showDialog.value = false
                                                    saveSelection(context, translation, book, chapter)
                                                }) { Text((name)) }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}