package com.schwegelbin.openbible.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.schwegelbin.openbible.R
import com.schwegelbin.openbible.logic.deserialize
import com.schwegelbin.openbible.logic.getSelection
import kotlinx.coroutines.delay

@Composable
fun StartScreen(onNavigateToRead: () -> Unit) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 160.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        val state = remember { mutableIntStateOf(0) }
        when (state.intValue) {
            0 -> {
                Text(
                    text = stringResource(R.string.downloading_index),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                val path = context.getExternalFilesDir("Index")
                Loading(onLoaded = { state.intValue = 1 }, file = "${path}/translations.json")
            }

            1 -> {
                TranslationCard(onSelected = { state.intValue = 2 })
            }

            2 -> {
                Text(
                    text = stringResource(R.string.downloading_translation),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                val (translation, _, _) = getSelection(context)
                val path = context.getExternalFilesDir("Translations")
                Loading(onLoaded = { onNavigateToRead() }, file = "${path}/${translation}.json")
            }
        }
    }
}

@Composable
fun Loading(onLoaded: () -> Unit, file: String) {
    LaunchedEffect(Unit) {
        val max = 200
        var counter = 0
        while (counter < max) {
            delay(100L)
            if (deserialize(file) != null) {
                onLoaded()
                break
            }
            counter++
        }
    }
    LinearProgressIndicator(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        color = MaterialTheme.colorScheme.secondary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant
    )
}