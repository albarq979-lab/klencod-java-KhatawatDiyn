package com.khatawat.diyn.repository

import android.content.Context
import com.khatawat.diyn.data.Debt
import com.khatawat.diyn.data.DebtDao
import com.khatawat.diyn.data.KhatawatDatabase
import com.khatawat.diyn.data.Note
import com.khatawat.diyn.data.NoteDao

class KhatawatRepository(context: Context) {

    private val noteDao: NoteDao = KhatawatDatabase.getInstance(context).noteDao()
    private val debtDao: DebtDao = KhatawatDatabase.getInstance(context).debtDao()

    fun observeNotes() = noteDao.getAll()
    fun observeDebts() = debtDao.getAll()
    fun observeActiveDebts() = debtDao.getActive()

    suspend fun addNote(note: Note) = noteDao.insert(note)
    suspend fun updateNote(note: Note) = noteDao.update(note)
    suspend fun deleteNote(note: Note) = noteDao.delete(note)
    suspend fun getNote(id: Long) = noteDao.getById(id)

    suspend fun addDebt(debt: Debt) = debtDao.insert(debt)
    suspend fun updateDebt(debt: Debt) = debtDao.update(debt)
    suspend fun deleteDebt(debt: Debt) = debtDao.delete(debt)
    suspend fun getDebt(id: Long) = debtDao.getById(id)
}
