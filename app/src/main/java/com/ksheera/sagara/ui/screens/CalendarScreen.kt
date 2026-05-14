package com.ksheera.sagara.ui.screens
import com.ksheera.sagara.ui.components.ScrollFabsLazyOverlay

import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ksheera.sagara.util.tr
import com.ksheera.sagara.viewmodel.FarmViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(nav: NavHostController, farm: FarmViewModel) {

    val __scrScope = rememberCoroutineScope()
    val __lazyState = androidx.compose.foundation.lazy.rememberLazyListState()
    androidx.compose.foundation.layout.Box(Modifier.fillMaxSize()) {

    val today = remember { Calendar.getInstance() }
    var year by remember { mutableStateOf(today.get(Calendar.YEAR)) }
    var month by remember { mutableStateOf(today.get(Calendar.MONTH)) }
    var selectedDay by remember { mutableStateOf(today.get(Calendar.DAY_OF_MONTH)) }

    val monthsEn = listOf("January","February","March","April","May","June","July","August","September","October","November","December")
    val monthsKn = listOf("ಜನವರಿ","ಫೆಬ್ರವರಿ","ಮಾರ್ಚ್","ಏಪ್ರಿಲ್","ಮೇ","ಜೂನ್","ಜುಲೈ","ಆಗಸ್ಟ್","ಸೆಪ್ಟೆಂಬರ್","ಅಕ್ಟೋಬರ್","ನವೆಂಬರ್","ಡಿಸೆಂಬರ್")
    val months = if (com.ksheera.sagara.util.LocaleManager.lang.value == com.ksheera.sagara.util.Lang.EN) monthsEn else monthsKn

    Scaffold(topBar = {
        TopAppBar(title = { Text(tr("Year Calendar", "ವರ್ಷದ ಕ್ಯಾಲೆಂಡರ್")) },
            navigationIcon = { IconButton({ nav.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } })
    }) { pad ->
        Column(Modifier.padding(pad).fillMaxSize().padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton({ year-- }) { Text("◀") }
                Spacer(Modifier.width(8.dp))
                Text("$year", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(8.dp))
                OutlinedButton({ year++ }) { Text("▶") }
            }
            Spacer(Modifier.height(12.dp))

            // Two rows: Jan-Jun, Jul-Dec
            MonthRow(months.subList(0, 6), startIndex = 0, selected = month) { month = it; selectedDay = 1 }
            Spacer(Modifier.height(8.dp))
            MonthRow(months.subList(6, 12), startIndex = 6, selected = month) { month = it; selectedDay = 1 }

            Spacer(Modifier.height(14.dp))
            Text("${months[month]} $year", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            MonthGrid(year, month, selectedDay) { selectedDay = it }
            Spacer(Modifier.height(12.dp))
            val cal = Calendar.getInstance().apply { set(year, month, selectedDay, 12, 0, 0) }
            val ms = cal.timeInMillis
            Text("${tr("Selected", "ಆಯ್ಕೆ")}: $selectedDay ${months[month]} $year", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Row {
                Button({ nav.navigate("addIncome/$ms") }, modifier = Modifier.weight(1f)) { Text(tr("Add Milk Income", "ಹಾಲಿನ ಆದಾಯ")) }
                Spacer(Modifier.width(8.dp))
                OutlinedButton({ nav.navigate("addExpense/$ms") }, modifier = Modifier.weight(1f)) { Text(tr("Add Expense", "ಖರ್ಚು ಸೇರಿಸಿ")) }
            }
            Spacer(Modifier.height(12.dp))
            DayEntries(farm, year, month, selectedDay)
        }
    }

        ScrollFabsLazyOverlay(__lazyState, __scrScope, Modifier.align(Alignment.BottomEnd))
    }
}

@Composable
private fun MonthRow(items: List<String>, startIndex: Int, selected: Int, onSelect: (Int) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        items.forEachIndexed { i, name ->
            val idx = startIndex + i
            val sel = idx == selected
            Box(
                Modifier
                    .weight(1f)
                    .clip(MaterialTheme.shapes.medium)
                    .background(if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onSelect(idx) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    name.take(3),
                    color = if (sel) Color.White else MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (sel) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun MonthGrid(year: Int, month: Int, selected: Int, onSelect: (Int) -> Unit) {
    val cal = Calendar.getInstance().apply { set(year, month, 1) }
    val firstDow = (cal.get(Calendar.DAY_OF_WEEK) + 6) % 7 // Mon=0
    val days = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val cells = (0 until firstDow).map { 0 } + (1..days).toList()
    val rows = cells.chunked(7)
    Column {
        Row { listOf("M","T","W","T","F","S","S").forEach { Text(it, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold) } }
        rows.forEach { week ->
            Row {
                week.forEach { d ->
                    Box(Modifier.weight(1f).aspectRatio(1f).padding(2.dp), contentAlignment = Alignment.Center) {
                        if (d > 0) {
                            val isSel = d == selected
                            Box(
                                Modifier.fillMaxSize().clip(CircleShape)
                                    .background(if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    .clickable { onSelect(d) },
                                contentAlignment = Alignment.Center
                            ) { Text("$d", color = if (isSel) Color.White else Color.Unspecified) }
                        }
                    }
                }
                repeat(7 - week.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun DayEntries(farm: FarmViewModel, y: Int, m: Int, d: Int) {
    val inc by farm.incomes.collectAsState()
    val exp by farm.expenses.collectAsState()
    fun sameDay(ms: Long): Boolean {
        val c = Calendar.getInstance().apply { timeInMillis = ms }
        return c.get(Calendar.YEAR) == y && c.get(Calendar.MONTH) == m && c.get(Calendar.DAY_OF_MONTH) == d
    }
    val di = inc.filter { sameDay(it.dateMillis) }
    val de = exp.filter { sameDay(it.dateMillis) }
    LazyColumn {
        if (di.isEmpty() && de.isEmpty()) item { Text(tr("No entries on this day.", "ಈ ದಿನ ಯಾವುದೇ ನಮೂದು ಇಲ್ಲ.")) }
        items(di) { i ->
            Card(Modifier.fillMaxWidth().padding(vertical = 3.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))) {
                Text("🥛 ${i.cowName}: ${i.liters}L  Fat ${i.fatPercent}%  ₹${"%.0f".format(i.total)}", modifier = Modifier.padding(10.dp))
            }
        }
        items(de) { e ->
            Card(Modifier.fillMaxWidth().padding(vertical = 3.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))) {
                Text("💸 ${e.category}: ₹${"%.0f".format(e.amount)}  ${e.note}", modifier = Modifier.padding(10.dp))
            }
        }
    }
}
