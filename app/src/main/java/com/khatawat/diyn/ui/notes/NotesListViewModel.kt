package com.khatawat.diyn.ui.notes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.khatawat.diyn.data.Note
import com.khatawat.diyn.repository.KhatawatRepository
import kotlinx.coroutines.launch

class NotesListViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = KhatawatRepository(app)
    val notes: LiveData<List<Note>> = repo.observeNotes()

    fun delete(note: Note) = viewModelScope.launch { repo.deleteNote(note) }
}
