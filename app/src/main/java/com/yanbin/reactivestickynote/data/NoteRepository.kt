package com.yanbin.reactivestickynote.data

import com.yanbin.reactivestickynote.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getAllNotes(): Flow<List<Note>>
    fun getNoteById(id: String): Flow<Note?>
    fun putNote(note: Note)
    fun createNote(note: Note)
    fun deleteNote(noteId: String)
}
