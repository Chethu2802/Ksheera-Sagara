package com.ksheera.sagara.ui.screens

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import com.ksheera.sagara.ui.components.ScrollFabsOverlay
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
fun AddIncomeScreen(nav: NavHostController, farm: FarmViewModel, dateMillis: Long) {

    val __scrState = rememberScrollState()
    val __scrScope = rememberCoroutineScope()
    androidx.compose.foundation.layout.Box(Modifier.fillMaxSize()) {

    var cow by remember { mutableStateOf("") }
    var liters by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    val df = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Add Milk Income", fontWeight = FontWeight.SemiBold) },
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
            OutlinedTextField(cow, { cow = it }, label = { Text("Cow Name / ಹಸುವಿನ ಹೆಸರು") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(liters, { liters = it }, label = { Text("Liters / ಲೀಟರ್") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(fat, { fat = it }, label = { Text("Fat %") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(price, { price = it }, label = { Text("Price per Liter (₹)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    farm.addIncome(dateMillis, cow.ifBlank { "Cow" }, liters.toDoubleOrNull() ?: 0.0, fat.toDoubleOrNull() ?: 0.0, price.toDoubleOrNull() ?: 0.0)
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