package com.ksheera.sagara.ui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ScrollFabsOverlay(scroll: ScrollState, scope: CoroutineScope, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(end = 12.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.End
    ) {
        SmallFloatingActionButton(onClick = { scope.launch { scroll.animateScrollTo(0) } }) {
            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Scroll to top")
        }
        SmallFloatingActionButton(onClick = { scope.launch { scroll.animateScrollTo(scroll.maxValue) } }) {
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Scroll to bottom")
        }
    }
}

@Composable
fun ScrollFabsLazyOverlay(state: LazyListState, scope: CoroutineScope, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(end = 12.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.End
    ) {
        SmallFloatingActionButton(onClick = { scope.launch { state.animateScrollToItem(0) } }) {
            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Scroll to top")
        }
        SmallFloatingActionButton(onClick = {
            scope.launch {
                val last = (state.layoutInfo.totalItemsCount - 1).coerceAtLeast(0)
                state.animateScrollToItem(last)
            }
        }) {
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Scroll to bottom")
        }
    }
}
