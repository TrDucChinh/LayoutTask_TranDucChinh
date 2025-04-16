package com.eco.layouttask

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.eco.layouttask.data.AppDatabase
import com.eco.layouttask.data.Note
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import java.time.LocalDate

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).noteDao()

    val allNotes = dao.getAllNotes().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    suspend fun getNote(date: LocalDate): Note? = dao.getNoteByDate(date)

    fun saveNote(date: LocalDate, content: String) {
        viewModelScope.launch {
            dao.insert(Note(date, content))
        }
    }
}
