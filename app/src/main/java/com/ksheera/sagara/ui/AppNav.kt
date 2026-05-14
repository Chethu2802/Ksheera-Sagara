package com.ksheera.sagara.ui

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ksheera.sagara.ui.screens.*
import com.ksheera.sagara.viewmodel.AuthViewModel
import com.ksheera.sagara.viewmodel.FarmViewModel

@Composable
fun AppNav() {
    val nav = rememberNavController()
    val auth: AuthViewModel = viewModel()
    val farm: FarmViewModel = viewModel()
    val user by auth.currentUser.collectAsState()
    LaunchedEffect(user) { farm.setOwner(user) }
    val start = if (user == null) "login" else "home"

    NavHost(nav, startDestination = start) {
        composable("login") { LoginScreen(auth, onSignedIn = { nav.navigate("home") { popUpTo("login") { inclusive = true } } }) }
        composable("home") { HomeScreen(nav, auth, farm) }
        composable("dashboard") { DashboardScreen(nav, farm) }
        composable("cows") { CowAnalysisScreen(nav, farm) }
        composable("ai") { SuggestionsScreen(nav, farm) }
        composable("chatbot") { ChatbotScreen(nav, farm) }
        composable("report") { ReportScreen(nav, farm) }
        composable("analytics") { AnalyticsScreen(nav, farm) }
        composable("calendar") { CalendarScreen(nav, farm) }
        composable("addIncome/{date}") { AddIncomeScreen(nav, farm, it.arguments?.getString("date")?.toLongOrNull() ?: System.currentTimeMillis()) }
        composable("addExpense/{date}") { AddExpenseScreen(nav, farm, it.arguments?.getString("date")?.toLongOrNull() ?: System.currentTimeMillis()) }
    }
}
