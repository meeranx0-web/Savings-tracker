package com.savingstracker.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SavingsBox::class], version = 1, exportSchema = false)
abstract class SavingsDatabase : RoomDatabase() {

    abstract fun savingsDao(): SavingsDao

    companion object {
        @Volatile
        private var INSTANCE: SavingsDatabase? = null

        fun getInstance(context: Context): SavingsDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    SavingsDatabase::class.java,
                    "savings_tracker.db"
                ).build().also { INSTANCE = it }
            }
    }
}
