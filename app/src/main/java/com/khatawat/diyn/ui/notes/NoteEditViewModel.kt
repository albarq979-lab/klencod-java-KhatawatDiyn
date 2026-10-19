package com.khatawat.diyn.ui.notes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.khatawat.diyn.data.Note
import com.khatawat.diyn.repository.KhatawatRepository
import kotlinx.coroutines.launch

class NoteEditViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = KhatawatRepository(app)
    val currentNote = MutableLiveData<Note?>(null)

    fun load(id: Long) = viewModelScope.launch {
        if (id == 0L) { currentNote.value = null; return@launch }
        currentNote.value = repo.getNote(id)
    }

    fun save(id: Long, title: String, body: String) = viewModelScope.launch {
        val note = Note(id = id, title = title, body = body)
        if (id == 0L) repo.addNote(note) else repo.updateNote(note)
    }
}
