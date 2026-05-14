package com.ksheera.sagara.ui.screens
import com.ksheera.sagara.ui.components.ScrollFabsLazyOverlay

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import com.ksheera.sagara.ui.components.ScrollFabsOverlay
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ksheera.sagara.ai.SuggestionEngine
import com.ksheera.sagara.viewmodel.FarmViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuggestionsScreen(nav: NavHostController, farm: FarmViewModel) {

    val __scrState = rememberScrollState()
    val __scrScope = rememberCoroutineScope()
    val __lazyState = androidx.compose.foundation.lazy.rememberLazyListState()
    androidx.compose.foundation.layout.Box(Modifier.fillMaxSize()) {

    val inc by farm.incomes.collectAsState()
    val exp by farm.expenses.collectAsState()
    val tips = remember(inc, exp) { SuggestionEngine.suggest(inc, exp) }
    Scaffold(topBar = {
        TopAppBar(title = { Text("AI Suggestions / ಸಲಹೆಗಳು") },
            navigationIcon = { IconButton({ nav.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } })
    }) { pad ->
        LazyColumn(modifier = Modifier.padding(pad).padding(12.dp), state = __lazyState) {
            items(tips) { t ->
                Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lightbulb, null, tint = MaterialTheme.colorScheme.secondary)
                        Spacer(Modifier.width(12.dp)); Text(t)
                    }
                }
            }
        }
    }

        ScrollFabsLazyOverlay(__lazyState, __scrScope, Modifier.align(Alignment.BottomEnd))
    }
}