package com.schwegelbin.openbible.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.schwegelbin.openbible.R
import com.schwegelbin.openbible.logic.checkForUpdates
import com.schwegelbin.openbible.logic.deserialize
import com.schwegelbin.openbible.logic.downloadTranslation
import com.schwegelbin.openbible.logic.getCheckAtStartup
import com.schwegelbin.openbible.logic.getFirstLaunch
import com.schwegelbin.openbible.logic.getSelection
import com.schwegelbin.openbible.logic.restoreBackup
import com.schwegelbin.openbible.logic.saveNewIndex
import com.schwegelbin.openbible.logic.saveSelection
import kotlinx.coroutines.delay
import java.io.File

@Composable
fun StartScreen(onNavigateToRead: () -> Unit) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 80.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        val state = remember { mutableIntStateOf(0) }
        val path = context.getExternalFilesDir("Index")
        val translationsFile = File("${path}/translations.json")
        val checksumFile = File("${path}/checksum.json")
        if (state.intValue == 0 && getCheckAtStartup(context) && translationsFile.exists() && checksumFile.exists())
            state.intValue = 4
        when (state.intValue) {
            0 -> {
                Spacer(Modifier.fillMaxHeight(0.4f))
                Text(
                    text = stringResource(R.string.needs_files),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(
                        onClick = {
                            state.intValue = 1
                        }
                    ) { Text(stringResource(R.string.download)) }
                    RestoreButton(
                        stringResource(R.string.restore),
                        true,
                        onFinished = { onNavigateToRead() })
                }
            }

            1 -> {
                Spacer(Modifier.fillMaxHeight(0.4f))
                Text(
                    text = stringResource(R.string.downloading_index),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                saveNewIndex(context)
                Loading(onLoaded = {
                    if (!getFirstLaunch(context)) onNavigateToRead() else state.intValue = 2
                }, file = "${context.getExternalFilesDir("Index")}/translations.json")
            }

            2 -> {
                TranslationCard(onSelected = { state.intValue = 3 })
            }

            3 -> {
                Spacer(Modifier.fillMaxHeight(0.4f))
                Text(
                    text = stringResource(R.string.downloading_translation),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
                val (translation, _, _) = getSelection(context, false)
                Loading(
                    onLoaded = { onNavigateToRead() },
                    file = "${context.getExternalFilesDir("Translations")}/${translation}.json"
                )
            }

            4 -> {
                Spacer(Modifier.fillMaxHeight(0.4f))
                Text(
                    text = stringResource(R.string.new_files),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Row(
                    Modifier
                        .horizontalScroll(rememberScrollState())
                        .align(Alignment.CenterHorizontally),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(onClick = {
                        checkForUpdates(context, true)
                        state.intValue = 3
                    }) { Text(stringResource(R.string.continue_button)) }
                    OutlinedButton(onClick = { onNavigateToRead() }) { Text(stringResource(R.string.skip)) }
                }
            }
        }
    }
}

@Composable
fun Loading(onLoaded: () -> Unit, file: String, maxSeconds: Int = 20) {
    WaitForFile(onLoaded, file, maxSeconds)
    LinearProgressIndicator(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        color = MaterialTheme.colorScheme.secondary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant
    )
}

@Composable
fun WaitForFile(
    onLoaded: () -> Unit,
    file: String,
    maxSeconds: Int = 20,
    updateInterval: Long = 100
) {
    LaunchedEffect(Unit) {
        var counter: Long = 0
        while (counter < maxSeconds * updateInterval) {
            delay(updateInterval)
            if (deserialize(file) != null) {
                onLoaded()
                break
            }
            counter++
        }
    }
}

@Composable
fun TranslationCard(onSelected: () -> Unit) {
    val context = LocalContext.current

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
        listTranslations(buttonFunction = { abbrev ->
            downloadTranslation(context, abbrev)
            saveSelection(context, abbrev, isSplitScreen = false)
            saveSelection(context, abbrev, isSplitScreen = true)
            onSelected()
        })
    }
}

@Composable
fun RestoreButton(label: String, user: Boolean, onFinished: () -> Unit) {
    val context = LocalContext.current
    val clicked = remember { mutableStateOf(false) }

    val getContentLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { result ->
        result?.let { restoreBackup(context, it, user, onFinished) }
    }

    OutlinedButton(onClick = { clicked.value = true }) { Text(label) }
    if (clicked.value) {
        clicked.value = false
        getContentLauncher.launch("application/zip")
    }
}