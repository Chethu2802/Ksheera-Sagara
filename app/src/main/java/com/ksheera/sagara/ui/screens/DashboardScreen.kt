package com.ksheera.sagara.ui.screens

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import com.ksheera.sagara.ui.components.ScrollFabsOverlay
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ksheera.sagara.ui.components.PieChart
import com.ksheera.sagara.util.NotificationHelper
import com.ksheera.sagara.viewmodel.FarmViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(nav: NavHostController, farm: FarmViewModel) {

    val __scrState = rememberScrollState()
    val __scrScope = rememberCoroutineScope()
    androidx.compose.foundation.layout.Box(Modifier.fillMaxSize()) {

    val s by farm.dashboard.collectAsState()
    val ctx = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Overview / ಡ್ಯಾಶ್‌ಬೋರ್ಡ್", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton({ nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val msg = "Net balance: ₹${"%.2f".format(s.net)}"
                        NotificationHelper.show(ctx, "Ksheera Sagara Alert", msg)
                    }) { Icon(Icons.Filled.NotificationsActive, contentDescription = "Notify") }
                }
            )
        }
    ) { pad ->
        Column(
            Modifier
                .padding(pad)
                .fillMaxSize()
                .verticalScroll(__scrState)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Hero net card
            HeroNetCard(s.net)

            Spacer(Modifier.height(12.dp))

            // 2-column responsive stat grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp, max = 260.dp)
            ) {
                item { StatTile("Income / ಆದಾಯ", "₹${"%.2f".format(s.totalIncome)}", Color(0xFF1B998B)) }
                item { StatTile("Expense / ವೆಚ್ಚ", "₹${"%.2f".format(s.totalExpense)}", Color(0xFFE56B6F)) }
                item { StatTile("Categories", "${s.expenseByCategory.size}", Color(0xFF6C8AE4)) }
                item {
                    val ratio = if (s.totalIncome > 0) (s.totalExpense / s.totalIncome) * 100 else 0.0
                    StatTile("Spend %", "${"%.0f".format(ratio)}%", Color(0xFFE9A23B))
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "Expense Breakdown",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(Modifier.height(8.dp))

            ElevatedCard(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .heightIn(min = 220.dp)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (s.expenseByCategory.isNotEmpty()) {
                        PieChart(s.expenseByCategory)
                    } else {
                        Text(
                            "No data yet. Add entries from Calendar.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }

        ScrollFabsOverlay(__scrState, __scrScope, Modifier.align(Alignment.BottomEnd))
    }
}

@Composable
private fun HeroNetCard(net: Double) {
    val positive = net >= 0
    val color = if (positive) Color(0xFF0F8B6C) else Color(0xFFB23A48)
    ElevatedCard(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = color)
    ) {
        Column(Modifier.padding(20.dp)) {
            Text("Net Balance / ನಿವ್ವಳ", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
            Spacer(Modifier.height(6.dp))
            Text(
                "₹${"%.2f".format(net)}",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun StatTile(label: String, value: String, accent: Color) {
    ElevatedCard(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Box(
                Modifier
                    .size(width = 28.dp, height = 4.dp)
            ) {
                Surface(color = accent, shape = RoundedCornerShape(4.dp)) {
                    Box(Modifier.fillMaxSize())
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = accent)
        }
    }
}