package com.ksheera.sagara.ui.screens

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import com.ksheera.sagara.ui.components.ScrollFabsOverlay
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ksheera.sagara.viewmodel.AuthViewModel
import com.ksheera.sagara.viewmodel.FarmViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(nav: NavHostController, auth: AuthViewModel, farm: FarmViewModel) {

    val __scrState = rememberScrollState()
    val __scrScope = rememberCoroutineScope()
    androidx.compose.foundation.layout.Box(Modifier.fillMaxSize()) {

    val s by farm.dashboard.collectAsState()
    val user by auth.currentUser.collectAsState()
    Scaffold(topBar = {
        TopAppBar(
            title = {
                Column {
                    Text("Ksheera Sagara", fontWeight = FontWeight.Bold)
                    Text("ಕ್ಷೀರ ಸಾಗರ", fontSize = 12.sp)
                }
            },
            actions = {
                TextButton(onClick = { com.ksheera.sagara.util.LocaleManager.toggle() }) {
                    Icon(Icons.Default.Language, contentDescription = "language", tint = Color.White)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        if (com.ksheera.sagara.util.LocaleManager.lang.value == com.ksheera.sagara.util.Lang.EN) "EN" else "ಕನ್ನಡ",
                        color = Color.White, fontWeight = FontWeight.Bold
                    )
                }
                IconButton({ auth.signOut() }) { Icon(Icons.Default.Logout, "logout", tint = Color.White) }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = Color.White,
                actionIconContentColor = Color.White
            )
        )
    }) { pad ->
        Column(
            Modifier
                .padding(pad)
                .fillMaxSize()
                .verticalScroll(__scrState)
                .padding(16.dp)
        ) {
            Text("${com.ksheera.sagara.util.tr("Hello", "ನಮಸ್ಕಾರ")}, ${user ?: com.ksheera.sagara.util.tr("Farmer", "ರೈತ")} 👋", fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
            Text(com.ksheera.sagara.util.tr("Welcome back to your dairy", "ನಿಮ್ಮ ಡೈರಿಗೆ ಮರಳಿ ಸ್ವಾಗತ"), fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(14.dp))

            // Hero gradient net card
            val isProfit = s.net >= 0
            val grad = if (isProfit)
                Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFF0F766E)))
            else
                Brush.linearGradient(listOf(Color(0xFFEF4444), Color(0xFF991B1B)))
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(grad)
                    .padding(20.dp)
            ) {
                Column {
                    Text(if (isProfit) com.ksheera.sagara.util.tr("PROFIT", "ಲಾಭ") else com.ksheera.sagara.util.tr("LOSS", "ನಷ್ಟ"), color = Color.White.copy(alpha = .9f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text("₹${"%.2f".format(s.net)}", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        MiniStat(com.ksheera.sagara.util.tr("Income", "ಆದಾಯ"), "₹${"%.0f".format(s.totalIncome)}")
                        MiniStat(com.ksheera.sagara.util.tr("Expense", "ಖರ್ಚು"), "₹${"%.0f".format(s.totalExpense)}")
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Text(com.ksheera.sagara.util.tr("Dashboards", "ಡ್ಯಾಶ್‌ಬೋರ್ಡ್‌ಗಳು"), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(10.dp))
            Tile(com.ksheera.sagara.util.tr("Cow Analysis", "ಹಸುಗಳ ವಿಶ್ಲೇಷಣೆ"), com.ksheera.sagara.util.tr("Per-cow yield & performance", "ಹಸುವಿನ ಉತ್ಪಾದನೆ ಮತ್ತು ಸಾಧನೆ"), Icons.Default.Pets, Color(0xFF8B5CF6)) { nav.navigate("cows") }
            Tile(com.ksheera.sagara.util.tr("AI Suggestions", "AI ಸಲಹೆಗಳು"), com.ksheera.sagara.util.tr("Smart farming tips", "ಸ್ಮಾರ್ಟ್ ಕೃಷಿ ಸಲಹೆಗಳು"), Icons.Default.Lightbulb, Color(0xFFF59E0B)) { nav.navigate("ai") }
            Tile(com.ksheera.sagara.util.tr("Chat Assistant", "ಚಾಟ್ ಸಹಾಯಕ"), com.ksheera.sagara.util.tr("Real-time answers about your dairy", "ನಿಮ್ಮ ಡೈರಿ ಬಗ್ಗೆ ತಕ್ಷಣದ ಉತ್ತರಗಳು"), Icons.Default.SmartToy, Color(0xFF06B6D4)) { nav.navigate("chatbot") }
            Tile(com.ksheera.sagara.util.tr("Monthly Report", "ಮಾಸಿಕ ವರದಿ"), com.ksheera.sagara.util.tr("Export PDF / Image", "PDF / ಚಿತ್ರ ರಫ್ತು"), Icons.Default.Description, Color(0xFF0EA5E9)) { nav.navigate("report") }
            Tile(com.ksheera.sagara.util.tr("Analytics", "ವಿಶ್ಲೇಷಣೆ"), com.ksheera.sagara.util.tr("Daily / Weekly / Monthly", "ದೈನಂದಿನ / ಸಾಪ್ತಾಹಿಕ / ಮಾಸಿಕ"), Icons.Default.QueryStats, Color(0xFF10B981)) { nav.navigate("analytics") }
            Tile(com.ksheera.sagara.util.tr("Overview Dashboard", "ಸಮಗ್ರ ಡ್ಯಾಶ್‌ಬೋರ್ಡ್"), com.ksheera.sagara.util.tr("All-in-one summary", "ಎಲ್ಲ ಸಾರಾಂಶ"), Icons.Default.Dashboard, Color(0xFF0F766E)) { nav.navigate("dashboard") }

            Spacer(Modifier.height(20.dp))
            Text(com.ksheera.sagara.util.tr("Entry", "ನಮೂದು"), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(10.dp))
            Tile(com.ksheera.sagara.util.tr("Year Calendar", "ವರ್ಷದ ಕ್ಯಾಲೆಂಡರ್"), com.ksheera.sagara.util.tr("Add entry on any date", "ಯಾವುದೇ ದಿನಾಂಕದಲ್ಲಿ ನಮೂದಿಸಿ"), Icons.Default.CalendarMonth, Color(0xFFEC4899)) { nav.navigate("calendar") }

            Spacer(Modifier.height(28.dp))
        }
    }

        ScrollFabsOverlay(__scrState, __scrScope, Modifier.align(Alignment.BottomEnd))
    }
}

@Composable
private fun MiniStat(label: String, value: String) {
    Column {
        Text(label, color = Color.White.copy(alpha = .85f), fontSize = 11.sp)
        Text(value, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun Tile(t: String, sub: String, icon: ImageVector, accent: Color, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = accent, modifier = Modifier.size(26.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(t, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Text(sub, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
