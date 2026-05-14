package com.ksheera.sagara.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ksheera.sagara.ai.Chatbot
import com.ksheera.sagara.util.Lang
import com.ksheera.sagara.util.LocaleManager
import com.ksheera.sagara.viewmodel.FarmViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class ChatMsg(val text: String, val fromUser: Boolean, val ts: Long = System.currentTimeMillis())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatbotScreen(nav: NavHostController, farm: FarmViewModel) {
    val inc by farm.incomes.collectAsState()
    val exp by farm.expenses.collectAsState()
    val msgs = remember {
        mutableStateListOf(
            ChatMsg("Hello! I'm your dairy assistant. Ask me anything.  ·  ನಮಸ್ಕಾರ! ನಾನು ನಿಮ್ಮ ಡೈರಿ ಸಹಾಯಕ. ಏನಾದರೂ ಕೇಳಿ.", false)
        )
    }
    var input by remember { mutableStateOf("") }
    var typing by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val lang = LocaleManager.lang.value

    fun send(text: String) {
        val q = text.trim()
        if (q.isEmpty()) return
        msgs += ChatMsg(q, true)
        input = ""
        typing = true
        scope.launch {
            listState.animateScrollToItem(msgs.size)
            delay(450) // simulate real-time typing
            val r = Chatbot.reply(q, inc, exp)
            r.lines.forEach { msgs += ChatMsg(it, false) }
            typing = false
            delay(50)
            listState.animateScrollToItem(msgs.size)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SmartToy, null, tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("Dairy Assistant", color = Color.White, fontWeight = FontWeight.Bold)
                            Text(if (typing) "typing…" else "online",
                                color = Color.White.copy(alpha = .85f),
                                style = MaterialTheme.typography.labelSmall)
                        }
                    }
                },
                navigationIcon = {
                    IconButton({ nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        bottomBar = {
            Surface(tonalElevation = 3.dp) {
                Column(Modifier.fillMaxWidth().padding(8.dp)) {
                    // Quick suggestion chips
                    Row(
                        Modifier.fillMaxWidth().horizontalScroll(rememberScrollStateCompat()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Chatbot.quickQuestions.forEach { (en, kn) ->
                            AssistChip(
                                onClick = { send(if (lang == Lang.EN) en else kn) },
                                label = { Text(if (lang == Lang.EN) en else kn) }
                            )
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = input,
                            onValueChange = { input = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text(if (lang == Lang.EN) "Ask about milk, cows, profit…" else "ಹಾಲು, ಹಸು, ಲಾಭದ ಬಗ್ಗೆ ಕೇಳಿ…") },
                            singleLine = true
                        )
                        Spacer(Modifier.width(6.dp))
                        FilledIconButton(onClick = { send(input) }, enabled = input.isNotBlank() && !typing) {
                            Icon(Icons.AutoMirrored.Filled.Send, "send")
                        }
                    }
                }
            }
        }
    ) { pad ->
        LazyColumn(
            state = listState,
            modifier = Modifier.padding(pad).fillMaxSize().padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 10.dp)
        ) {
            items(msgs) { m -> Bubble(m) }
            if (typing) item { TypingBubble() }
        }
    }
}

@Composable
private fun rememberScrollStateCompat() = androidx.compose.foundation.rememberScrollState()

@Composable
private fun Bubble(m: ChatMsg) {
    val isUser = m.fromUser
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        val bg = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
        val fg = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface
        Box(
            Modifier
                .widthIn(max = 320.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp, topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .background(bg)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(m.text, color = fg)
        }
    }
}

@Composable
private fun TypingBubble() {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        Box(
            Modifier
                .clip(RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            val dots by produceState(initialValue = 1) {
                while (true) { delay(350); value = (value % 3) + 1 }
            }
            Text(".".repeat(dots), color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
