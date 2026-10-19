package com.khatawat.diyn.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

enum class DebtDirection { OWED_TO_ME, I_OWE }

@Entity(tableName = "debts")
data class Debt(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "person_name") val personName: String,
    @ColumnInfo(name = "phone") val phone: String,
    @ColumnInfo(name = "amount") val amount: Double,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "direction") val direction: DebtDirection,
    @ColumnInfo(name = "due_date") val dueDate: Long,
    @ColumnInfo(name = "is_paid") val isPaid: Boolean = false,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)
