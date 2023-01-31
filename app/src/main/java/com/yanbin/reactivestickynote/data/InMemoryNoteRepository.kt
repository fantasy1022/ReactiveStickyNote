package com.yanbin.reactivestickynote.data

import com.yanbin.reactivestickynote.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.concurrent.ConcurrentHashMap

//TODO:Use flow to modify
class InMemoryNoteRepository: NoteRepository {

    private val notes = mutableListOf<Note>()
    private val noteMap = ConcurrentHashMap<String, Note>()

    override fun getAllNotes(): Flow<List<Note>> = flowOf(emptyList())

    override fun putNote(note: Note) {
        noteMap[note.id] = note
        notes.add(noteMap.elements().nextElement())
    }

    override fun createNote(note: Note) {
        noteMap[note.id] = note
    }

    override fun deleteNote(noteId: String) {
        noteMap.remove(noteId)
    }

    override fun getNoteById(id: String): Flow<Note?> {
        return flowOf(null)
    }

    init {
        Note.createRandomNote().let { note -> noteMap[note.id] = note }
        Note.createRandomNote().let { note -> noteMap[note.id] = note }
//        notes.onNext(noteMap.elements().toList())
    }
}