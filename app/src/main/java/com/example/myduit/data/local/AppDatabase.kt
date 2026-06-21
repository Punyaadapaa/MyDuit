package com.example.myduit.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Room
import com.example.myduit.data.model.Transaction
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

import com.example.myduit.data.model.Budget
import com.example.myduit.data.model.Category
import java.util.UUID

@Database(entities = [Transaction::class, Budget::class, Category::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao
    abstract fun categoryDao(): com.example.myduit.data.local.CategoryDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add category column to transactions
                db.execSQL("ALTER TABLE transactions ADD COLUMN category TEXT NOT NULL DEFAULT 'Lainnya'")
                // Create budgets table
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `budgets` (`id` TEXT NOT NULL, `category` TEXT NOT NULL, `limitAmount` REAL NOT NULL, `monthYear` TEXT NOT NULL, PRIMARY KEY(`id`))"
                )
            }
        }
        
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `categories` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `iconName` TEXT NOT NULL, PRIMARY KEY(`id`))"
                )
                
                // Prepopulate default categories
                val defaults = listOf(
                    Pair("Makan", "Restaurant"),
                    Pair("Transportasi", "Commute"),
                    Pair("Belanja", "ShoppingBag"),
                    Pair("Tagihan", "ReceiptLong"),
                    Pair("Gaji", "Payments"),
                    Pair("Hiburan", "SportsEsports"),
                    Pair("Kesehatan", "MedicalServices"),
                    Pair("Pendidikan", "School"),
                    Pair("Lainnya", "Category")
                )
                
                defaults.forEach { (name, icon) ->
                    db.execSQL("INSERT INTO categories (id, name, iconName) VALUES ('${UUID.randomUUID()}', '$name', '$icon')")
                }
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE transactions ADD COLUMN isImportant INTEGER NOT NULL DEFAULT 0")
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "myduit_database"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
