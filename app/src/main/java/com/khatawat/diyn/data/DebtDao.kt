package com.khatawat.diyn.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface DebtDao {
    @Query("SELECT * FROM debts ORDER BY is_paid ASC, due_date ASC")
    fun getAll(): LiveData<List<Debt>>

    @Query("SELECT * FROM debts WHERE is_paid = 0 ORDER BY due_date ASC")
    fun getActive(): LiveData<List<Debt>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(debt: Debt): Long

    @Update
    suspend fun update(debt: Debt)

    @Delete
    suspend fun delete(debt: Debt)

    @Query("SELECT * FROM debts WHERE id = :id")
    suspend fun getById(id: Long): Debt?
}
