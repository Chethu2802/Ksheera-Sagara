package com.ksheera.sagara.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class User(
    @PrimaryKey val username: String,
    val name: String,
    val email: String,
    val passwordHash: String,
    val secretKey: String,
    val salt: String = "",
    val phone: String = "",
    val lastLoginAt: Long = 0L,
    val loginCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    init {
        require(username.isNotBlank()) { "username required" }
        require(email.isNotBlank()) { "email required" }
        require(passwordHash.isNotBlank()) { "passwordHash required" }
    }
}

@Entity(
    tableName = "income",
    indices = [
        Index("dateMillis"),
        Index("cowName"),
        Index("ownerUsername")
    ]
)
data class MilkIncome(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateMillis: Long,
    val cowName: String,
    val liters: Double,
    val fatPercent: Double,
    val pricePerLiter: Double,
    val ownerUsername: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    init {
        require(cowName.isNotBlank()) { "cowName required" }
        require(liters >= 0) { "liters must be >= 0" }
        require(pricePerLiter >= 0) { "price must be >= 0" }
        require(fatPercent in 0.0..100.0) { "fat % must be 0..100" }
    }
    val total: Double get() = liters * pricePerLiter
}

@Entity(
    tableName = "expense",
    indices = [
        Index("dateMillis"),
        Index("category"),
        Index("ownerUsername")
    ]
)
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateMillis: Long,
    val category: String, // FODDER, MEDICAL, LABOR, OTHER
    val amount: Double,
    val note: String = "",
    val ownerUsername: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    init {
        require(category.isNotBlank()) { "category required" }
        require(amount >= 0) { "amount must be >= 0" }
    }
}
