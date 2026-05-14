package com.ksheera.sagara.ui.screens

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import com.ksheera.sagara.ui.components.ScrollFabsOverlay
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ksheera.sagara.viewmodel.FarmViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(nav: NavHostController, farm: FarmViewModel, dateMillis: Long) {

    val __scrState = rememberScrollState()
    val __scrScope = rememberCoroutineScope()
    androidx.compose.foundation.layout.Box(Modifier.fillMaxSize()) {

    val cats = listOf("FODDER", "MEDICAL", "LABOR", "EQUIPMENT", "OTHER")
    var cat by remember { mutableStateOf(cats[0]) }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    val df = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Add Expense", fontWeight = FontWeight.SemiBold) },
            navigationIcon = { IconButton({ nav.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
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
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Date / ದಿನಾಂಕ", fontSize = 12.sp)
                    Text(df.format(Date(dateMillis)), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(16.dp))
            Text("Category / ವರ್ಗ", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            Row(Modifier.horizontalScroll(rememberScrollState())) {
                cats.forEach { c ->
                    FilterChip(selected = cat == c, onClick = { cat = c }, label = { Text(c) }, modifier = Modifier.padding(end = 6.dp))
                }
            }
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(amount, { amount = it }, label = { Text("Amount (₹)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(note, { note = it }, label = { Text("Note / ಟಿಪ್ಪಣಿ") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    farm.addExpense(dateMillis, cat, amount.toDoubleOrNull() ?: 0.0, note)
                    nav.popBackStack()
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) { Text("Save / ಉಳಿಸಿ", fontSize = 16.sp, fontWeight = FontWeight.SemiBold) }
            Spacer(Modifier.height(24.dp))
        }
    }

        ScrollFabsOverlay(__scrState, __scrScope, Modifier.align(Alignment.BottomEnd))
    }
}