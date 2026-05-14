package com.ksheera.sagara.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [User::class, MilkIncome::class, Expense::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun incomeDao(): IncomeDao
    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        /** v1 → v2: timestamps, ownerUsername, indices, unique email. */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                val now = System.currentTimeMillis()
                db.execSQL("ALTER TABLE users ADD COLUMN createdAt INTEGER NOT NULL DEFAULT $now")
                db.execSQL("ALTER TABLE users ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT $now")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_users_email ON users(email)")
                db.execSQL("ALTER TABLE income ADD COLUMN ownerUsername TEXT")
                db.execSQL("ALTER TABLE income ADD COLUMN createdAt INTEGER NOT NULL DEFAULT $now")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_income_dateMillis ON income(dateMillis)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_income_cowName ON income(cowName)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_income_ownerUsername ON income(ownerUsername)")
                db.execSQL("ALTER TABLE expense ADD COLUMN ownerUsername TEXT")
                db.execSQL("ALTER TABLE expense ADD COLUMN createdAt INTEGER NOT NULL DEFAULT $now")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_expense_dateMillis ON expense(dateMillis)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_expense_category ON expense(category)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_expense_ownerUsername ON expense(ownerUsername)")
            }
        }

        /** v2 → v3: add salt, phone, lastLoginAt, loginCount on users. */
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE users ADD COLUMN salt TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE users ADD COLUMN phone TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE users ADD COLUMN lastLoginAt INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE users ADD COLUMN loginCount INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun get(ctx: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                ctx.applicationContext, AppDatabase::class.java, "ksheera.db"
            )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .fallbackToDestructiveMigrationOnDowngrade()
                .build()
                .also { INSTANCE = it }
        }
    }
}
