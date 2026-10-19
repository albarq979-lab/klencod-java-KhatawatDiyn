package com.khatawat.diyn.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Note::class, Debt::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class KhatawatDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun debtDao(): DebtDao

    companion object {
        @Volatile
        private var INSTANCE: KhatawatDatabase? = null

        fun getInstance(context: Context): KhatawatDatabase {
            return INSTANCE ?: synchronized(this) {
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    KhatawatDatabase::class.java,
                    "khatawat.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = db
                db
            }
        }
    }
}
