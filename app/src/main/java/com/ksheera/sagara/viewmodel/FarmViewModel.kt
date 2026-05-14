package com.ksheera.sagara.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ksheera.sagara.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

data class CowStat(val cow: String, val liters: Double, val income: Double, val avgFat: Double)

data class DashboardState(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val net: Double = 0.0,
    val cows: List<CowStat> = emptyList(),
    val expenseByCategory: Map<String, Double> = emptyMap(),
    val incomeList: List<MilkIncome> = emptyList(),
    val expenseList: List<Expense> = emptyList()
)

class FarmViewModel(app: Application) : AndroidViewModel(app) {
    private val db = AppDatabase.get(app)
    val incomes = db.incomeDao().all().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val expenses = db.expenseDao().all().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val dashboard: StateFlow<DashboardState> = combine(incomes, expenses) { inc, exp ->
        val ti = inc.sumOf { it.total }; val te = exp.sumOf { it.amount }
        val cows = inc.groupBy { it.cowName }.map { (k, v) ->
            CowStat(k, v.sumOf { it.liters }, v.sumOf { it.total }, v.map { it.fatPercent }.average())
        }.sortedByDescending { it.income }
        val cat = exp.groupBy { it.category }.mapValues { it.value.sumOf { e -> e.amount } }
        DashboardState(ti, te, ti - te, cows, cat, inc, exp)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, DashboardState())

    @Volatile private var currentOwner: String? = null
    fun setOwner(username: String?) { currentOwner = username }

    fun addIncome(date: Long, cow: String, liters: Double, fat: Double, price: Double) =
        viewModelScope.launch {
            runCatching {
                db.incomeDao().insert(
                    MilkIncome(dateMillis = date, cowName = cow.trim(), liters = liters,
                        fatPercent = fat, pricePerLiter = price, ownerUsername = currentOwner)
                )
            }
        }

    fun addExpense(date: Long, category: String, amount: Double, note: String) =
        viewModelScope.launch {
            runCatching {
                db.expenseDao().insert(
                    Expense(dateMillis = date, category = category.trim(), amount = amount,
                        note = note.trim(), ownerUsername = currentOwner)
                )
            }
        }

    fun deleteIncome(i: MilkIncome) = viewModelScope.launch { db.incomeDao().delete(i) }
    fun deleteExpense(e: Expense) = viewModelScope.launch { db.expenseDao().delete(e) }

    fun monthSummary(year: Int, month: Int): DashboardState {
        val inc = incomes.value.filter { sameMonth(it.dateMillis, year, month) }
        val exp = expenses.value.filter { sameMonth(it.dateMillis, year, month) }
        val ti = inc.sumOf { it.total }; val te = exp.sumOf { it.amount }
        val cows = inc.groupBy { it.cowName }.map { (k, v) ->
            CowStat(k, v.sumOf { it.liters }, v.sumOf { it.total }, v.map { it.fatPercent }.average())
        }
        return DashboardState(ti, te, ti - te, cows, exp.groupBy { it.category }.mapValues { it.value.sumOf { e -> e.amount } }, inc, exp)
    }

    private fun sameMonth(ms: Long, y: Int, m: Int): Boolean {
        val c = Calendar.getInstance().apply { timeInMillis = ms }
        return c.get(Calendar.YEAR) == y && c.get(Calendar.MONTH) == m
    }
}
