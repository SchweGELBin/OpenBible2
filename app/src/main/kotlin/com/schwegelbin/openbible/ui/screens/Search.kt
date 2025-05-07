package com.schwegelbin.openbible.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import com.schwegelbin.openbible.R
import com.schwegelbin.openbible.logic.getSelection
import com.schwegelbin.openbible.logic.saveSelection
import com.schwegelbin.openbible.logic.searchText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(onNavigateToRead: () -> Unit) {
    Scaffold(topBar = {
        TopAppBar(title = { Text(stringResource(R.string.search)) }, navigationIcon = {
            IconButton(onClick = { onNavigateToRead() }) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(R.string.close)
                )
            }
        })
    }) { innerPadding ->
        val context = LocalContext.current
        val selection = remember { mutableStateOf(getSelection(context, false)) }
        val query = remember { mutableStateOf("") }
        val results = remember { mutableStateOf(listOf(Triple("", -1, -1))) }
        TextSearchBar(
            Modifier
                .padding(innerPadding)
                .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
            query.value,
            { s -> query.value = s },
            { results.value = searchText(context, selection.value.first, query.value) },
            results.value,
            { (book, chapter) ->
                if (book >= 0 && chapter >= 0) saveSelection(
                    context,
                    book = book,
                    chapter = chapter,
                    isSplitScreen = false
                )
                onNavigateToRead()
            },
            stringResource(R.string.search_for_text)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextSearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    searchResults: List<Triple<String, Int, Int>>,
    onResultClick: (Pair<Int, Int>) -> Unit,
    placeholderText: String,
) {
    Box(
        modifier
            .fillMaxSize()
            .semantics { isTraversalGroup = true }
    ) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .semantics { traversalIndex = 0f },
            inputField = {
                SearchBarDefaults.InputField(
                    query = query,
                    onQueryChange = onQueryChange,
                    onSearch = onSearch,
                    expanded = true,
                    onExpandedChange = { },
                    placeholder = { Text(placeholderText) },
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = "Search"
                        )
                    }
                )
            },
            expanded = true,
            onExpandedChange = { },
            colors = SearchBarDefaults.colors(Color.Transparent),
            windowInsets = WindowInsets(0.dp)
        ) {
            LazyColumn {
                items(count = searchResults.size) { index ->
                    val resultText = searchResults[index].first
                    if (resultText != "") {
                        ListItem(
                            headlineContent = { Text(resultText) },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                            modifier = Modifier
                                .clickable {
                                    onResultClick(
                                        Pair(
                                            searchResults[index].second,
                                            searchResults[index].third
                                        )
                                    )
                                }
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}