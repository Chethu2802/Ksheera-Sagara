package com.ksheera.sagara.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT) suspend fun insert(u: User)
    @Update suspend fun update(u: User)
    @Query("SELECT * FROM users WHERE username = :u LIMIT 1") suspend fun find(u: String): User?
    @Query("SELECT * FROM users WHERE email = :e LIMIT 1") suspend fun findByEmail(e: String): User?
    @Query("UPDATE users SET passwordHash = :hash, salt = :salt, updatedAt = :ts WHERE username = :u")
    suspend fun updatePassword(u: String, hash: String, salt: String, ts: Long = System.currentTimeMillis())
    @Query("UPDATE users SET lastLoginAt = :ts, loginCount = loginCount + 1 WHERE username = :u")
    suspend fun recordLogin(u: String, ts: Long = System.currentTimeMillis())
    @Query("SELECT COUNT(*) FROM users") suspend fun count(): Int
}

@Dao
interface IncomeDao {
    @Insert suspend fun insert(i: MilkIncome): Long
    @Update suspend fun update(i: MilkIncome)
    @Delete suspend fun delete(i: MilkIncome)
    @Query("SELECT * FROM income ORDER BY dateMillis DESC") fun all(): Flow<List<MilkIncome>>
    @Query("SELECT * FROM income WHERE dateMillis BETWEEN :from AND :to ORDER BY dateMillis DESC")
    fun range(from: Long, to: Long): Flow<List<MilkIncome>>
    @Query("SELECT COALESCE(SUM(liters * pricePerLiter), 0.0) FROM income WHERE dateMillis BETWEEN :from AND :to")
    suspend fun totalIncome(from: Long, to: Long): Double
    @Query("SELECT COALESCE(SUM(liters), 0.0) FROM income WHERE cowName = :cow")
    suspend fun litersByCow(cow: String): Double
    @Query("DELETE FROM income") suspend fun clear()
}

@Dao
interface ExpenseDao {
    @Insert suspend fun insert(e: Expense): Long
    @Update suspend fun update(e: Expense)
    @Delete suspend fun delete(e: Expense)
    @Query("SELECT * FROM expense ORDER BY dateMillis DESC") fun all(): Flow<List<Expense>>
    @Query("SELECT * FROM expense WHERE dateMillis BETWEEN :from AND :to ORDER BY dateMillis DESC")
    fun range(from: Long, to: Long): Flow<List<Expense>>
    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM expense WHERE dateMillis BETWEEN :from AND :to")
    suspend fun totalExpense(from: Long, to: Long): Double
    @Query("SELECT category, COALESCE(SUM(amount), 0.0) AS total FROM expense GROUP BY category")
    suspend fun byCategory(): List<CategoryTotal>
    @Query("DELETE FROM expense") suspend fun clear()
}

data class CategoryTotal(val category: String, val total: Double)
