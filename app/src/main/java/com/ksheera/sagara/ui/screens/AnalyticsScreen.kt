package com.ksheera.sagara.ui.screens

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import com.ksheera.sagara.ui.components.ScrollFabsOverlay
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ksheera.sagara.ui.components.PieChart
import com.ksheera.sagara.viewmodel.FarmViewModel
import java.text.SimpleDateFormat
import java.util.*

private enum class Period(val label: String, val kn: String) {
    DAILY("Daily", "ದೈನಂದಿನ"),
    WEEKLY("Weekly", "ಸಾಪ್ತಾಹಿಕ"),
    MONTHLY("Monthly", "ಮಾಸಿಕ"),
    YEARLY("Yearly", "ವಾರ್ಷಿಕ")
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AnalyticsScreen(nav: NavHostController, farm: FarmViewModel) {

    val __scrState = rememberScrollState()
    val __scrScope = rememberCoroutineScope()
    androidx.compose.foundation.layout.Box(Modifier.fillMaxSize()) {

    val s by farm.dashboard.collectAsState()
    var period by remember { mutableStateOf(Period.MONTHLY) }

    Scaffold(topBar = {
        TopAppBar(title = { Text(com.ksheera.sagara.util.tr("Analytics", "ವಿಶ್ಲೇಷಣೆ")) },
            navigationIcon = { IconButton({ nav.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = Color.White, navigationIconContentColor = Color.White)
        )
    }) { pad ->
        Column(Modifier.padding(pad).fillMaxSize().verticalScroll(__scrState).padding(16.dp)) {

            // Period selector
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Period.entries.forEach { p ->
                    FilterChip(selected = p == period, onClick = { period = p },
                        label = { Text(com.ksheera.sagara.util.tr(p.label, p.kn)) })
                }
            }
            Spacer(Modifier.height(16.dp))

            val buckets = bucketize(s.incomeList.map { it.dateMillis to it.total } +
                                    s.expenseList.map { it.dateMillis to 0.0 }, period)
            val incomeBuckets = bucketize(s.incomeList.map { it.dateMillis to it.total }, period)
            val expenseBuckets = bucketize(s.expenseList.map { it.dateMillis to it.amount }, period)
            val keys = (incomeBuckets.keys + expenseBuckets.keys).distinct().sorted()

            // Totals card for the current period grain
            val totalInc = incomeBuckets.values.sum()
            val totalExp = expenseBuckets.values.sum()
            Card(colors = CardDefaults.cardColors(containerColor = if (totalInc - totalExp >= 0) Color(0xFFE8F5E9) else Color(0xFFFFEBEE))) {
                Column(Modifier.padding(16.dp)) {
                    Text("${com.ksheera.sagara.util.tr(period.label, period.kn)} ${com.ksheera.sagara.util.tr("totals", "ಒಟ್ಟು")}", fontWeight = FontWeight.SemiBold)
                    Text("${com.ksheera.sagara.util.tr("Income", "ಆದಾಯ")}: ₹${"%.0f".format(totalInc)}")
                    Text("${com.ksheera.sagara.util.tr("Expense", "ಖರ್ಚು")}: ₹${"%.0f".format(totalExp)}")
                    Text("${com.ksheera.sagara.util.tr("Net", "ನಿವ್ವಳ")}: ₹${"%.0f".format(totalInc - totalExp)}", fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(16.dp))

            // Pie chart: where the money is going (Feed vs Meds vs others)
            Text(com.ksheera.sagara.util.tr("Where money is spent", "ಎಲ್ಲಿ ಖರ್ಚಾಗುತ್ತಿದೆ"), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))
            val expenseByCat = s.expenseByCategory
            if (expenseByCat.isEmpty()) {
                Text(com.ksheera.sagara.util.tr("No expense data yet.", "ಇನ್ನೂ ಖರ್ಚು ದತ್ತಾಂಶ ಇಲ್ಲ."))
            } else {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        PieChart(expenseByCat, Modifier.fillMaxWidth())
                        Spacer(Modifier.height(8.dp))
                        val feed = expenseByCat["FODDER"] ?: 0.0
                        val meds = expenseByCat["MEDICAL"] ?: 0.0
                        val total = expenseByCat.values.sum().takeIf { it > 0 } ?: 1.0
                        Text("Feed (FODDER): ${"%.1f".format(feed / total * 100)}% — ₹${"%.0f".format(feed)}")
                        Text("Meds (MEDICAL): ${"%.1f".format(meds / total * 100)}% — ₹${"%.0f".format(meds)}")
                    }
                }
            }
            Spacer(Modifier.height(20.dp))

            // Per-bucket breakdown
            Text("${com.ksheera.sagara.util.tr(period.label, period.kn)} ${com.ksheera.sagara.util.tr("breakdown", "ವಿವರಣೆ")}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))
            if (keys.isEmpty()) Text(com.ksheera.sagara.util.tr("No data yet.", "ಇನ್ನೂ ದತ್ತಾಂಶ ಇಲ್ಲ.")) else keys.forEach { k ->
                val inc = incomeBuckets[k] ?: 0.0
                val exp = expenseBuckets[k] ?: 0.0
                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text(k, fontWeight = FontWeight.SemiBold)
                        Text("${com.ksheera.sagara.util.tr("Income", "ಆದಾಯ")}: ₹${"%.0f".format(inc)}")
                        Text("${com.ksheera.sagara.util.tr("Expense", "ಖರ್ಚು")}: ₹${"%.0f".format(exp)}")
                        Text("${com.ksheera.sagara.util.tr("Net", "ನಿವ್ವಳ")}: ₹${"%.0f".format(inc - exp)}",
                            color = if (inc - exp >= 0) Color(0xFF1B5E20) else Color(0xFFB71C1C))
                    }
                }
            }
        }
    }

        ScrollFabsOverlay(__scrState, __scrScope, Modifier.align(Alignment.BottomEnd))
    }
}

private fun bucketize(items: List<Pair<Long, Double>>, period: Period): Map<String, Double> {
    val fmt = when (period) {
        Period.DAILY -> SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        Period.WEEKLY -> SimpleDateFormat("'Wk' w, yyyy", Locale.getDefault())
        Period.MONTHLY -> SimpleDateFormat("MMM yyyy", Locale.getDefault())
        Period.YEARLY -> SimpleDateFormat("yyyy", Locale.getDefault())
    }
    val out = linkedMapOf<String, Double>()
    items.sortedBy { it.first }.forEach { (ts, v) ->
        val k = fmt.format(Date(ts))
        out[k] = (out[k] ?: 0.0) + v
    }
    return out
}
