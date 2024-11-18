package com.schwegelbin.openbible.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.schwegelbin.openbible.logic.ReadTextAlignment
import com.schwegelbin.openbible.logic.getChapter
import com.schwegelbin.openbible.logic.getReadTextAlignment

@Composable
fun ReadScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val (title, chapter) = getChapter(context)
    val textAlignment = getReadTextAlignment(context)
    Column(
        modifier = modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
        ElevatedCard(
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .verticalScroll(state = rememberScrollState(), enabled = true)
                .fillMaxWidth()
        ) {
            when (textAlignment) {
                ReadTextAlignment.Start -> {
                    SelectionContainer {
                        Text(
                            text = chapter,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
                ReadTextAlignment.Justify -> {
                    Text(
                        text = chapter,
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Justify
                    )
                }
            }
        }
    }
}