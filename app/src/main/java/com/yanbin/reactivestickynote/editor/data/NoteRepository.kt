package com.yanbin.reactivestickynote.editor.data

import com.yanbin.reactivestickynote.account.Account
import com.yanbin.reactivestickynote.editor.model.SelectedNote
import com.yanbin.reactivestickynote.editor.model.StickyNote
import io.reactivex.rxjava3.core.Observable

interface NoteRepository {
    fun getAllVisibleNoteIds(): Observable<List<String>>
    fun getAllSelectedNotes(): Observable<List<SelectedNote>>
    fun getNoteById(id: String): Observable<StickyNote>
    fun putNote(stickyNote: StickyNote)
    fun createNote(stickyNote: StickyNote)
    fun deleteNote(noteId: String)

    fun setNoteSelection(noteId: String, account: Account)
    fun removeNoteSelection(noteId: String, account: Account)
}
