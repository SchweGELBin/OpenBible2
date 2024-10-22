package com.schwegelbin.openbible.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.schwegelbin.openbible.R
import com.schwegelbin.openbible.logic.saveChecksum
import com.schwegelbin.openbible.logic.saveIndex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onClose: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = { onClose() }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.close)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            IndexButton()
            LocaleButton()
            ThemeButton()
            AccentButton()
        }
    }
}

@Composable
fun IndexButton() {
    val context = LocalContext.current
    TextButton(onClick = {
        saveIndex(context)
        saveChecksum(context)
    }) { Text(stringResource(R.string.update_index)) }
}

@Composable
fun LocaleButton() {
    TextButton(onClick = {
        //TODO: Open Dialog to change locale/language (System/English/...)
    }) { Text(stringResource(R.string.change_locale)) }
}

@Composable
fun ThemeButton() {
    TextButton(onClick = {
        //TODO: Open Dialog to change theme (System/Light/Dark/Amoled)
    }) { Text(stringResource(R.string.change_theme)) }
}

@Composable
fun AccentButton() {
    TextButton(onClick = {
        //TODO: Open Dialog to change accent color (System/Red/Blue/...)
    }) { Text(stringResource(R.string.change_accent)) }
}