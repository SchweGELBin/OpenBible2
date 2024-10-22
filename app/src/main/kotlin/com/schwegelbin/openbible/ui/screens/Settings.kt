package com.schwegelbin.openbible.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
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
        Column(modifier = Modifier.padding(innerPadding).padding(horizontal = 12.dp, vertical = 24.dp)) {
            Text(text = stringResource(R.string.index), style = MaterialTheme.typography.titleLarge)
            IndexButton()
            HorizontalDivider(modifier = Modifier.padding(12.dp))
            Text(text = stringResource(R.string.about_us), style = MaterialTheme.typography.titleLarge)
            RepoButton()
        }
    }
}

@Composable
fun IndexButton() {
    val context = LocalContext.current
    OutlinedButton(onClick = {
        saveIndex(context)
        saveChecksum(context)
    }) { Text(stringResource(R.string.update_index)) }
}

@Composable
fun RepoButton() {
    val context = LocalContext.current
    OutlinedButton(onClick = {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/SchweGELBin/OpenBible2"))
        startActivity(context, intent, null)
    }) { Text(stringResource(R.string.source_repo)) }
}