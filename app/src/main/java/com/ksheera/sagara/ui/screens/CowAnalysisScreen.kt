package com.ksheera.sagara.ui.screens
import com.ksheera.sagara.ui.components.ScrollFabsLazyOverlay

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import com.ksheera.sagara.ui.components.ScrollFabsOverlay
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ksheera.sagara.viewmodel.FarmViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CowAnalysisScreen(nav: NavHostController, farm: FarmViewModel) {

    val __scrState = rememberScrollState()
    val __scrScope = rememberCoroutineScope()
    val __lazyState = androidx.compose.foundation.lazy.rememberLazyListState()
    androidx.compose.foundation.layout.Box(Modifier.fillMaxSize()) {

    val s by farm.dashboard.collectAsState()
    Scaffold(topBar = {
        TopAppBar(title = { Text("Cow Analysis / ಹಸುಗಳ ವಿಶ್ಲೇಷಣೆ") },
            navigationIcon = { IconButton({ nav.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } })
    }) { pad ->
        if (s.cows.isEmpty()) Box(Modifier.padding(pad).fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text("No cow data. Add milk income first.")
        } else LazyColumn(modifier = Modifier.padding(pad).padding(12.dp), state = __lazyState) {
            items(s.cows) { c ->
                Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Column(Modifier.padding(14.dp)) {
                        Text(c.cow, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Liters: ${"%.1f".format(c.liters)} L")
                        Text("Avg Fat: ${"%.2f".format(c.avgFat)} %")
                        Text("Income: ₹${"%.2f".format(c.income)}")
                    }
                }
            }
        }
    }

        ScrollFabsLazyOverlay(__lazyState, __scrScope, Modifier.align(Alignment.BottomEnd))
    }
}