package com.yanbin.reactivestickynote.domain

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yanbin.reactivestickynote.data.NoteRepository
import com.yanbin.reactivestickynote.model.Note
import com.yanbin.reactivestickynote.model.Position
import com.yanbin.reactivestickynote.model.YBColor
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.launch

class EditorViewModel(
    private val noteRepository: NoteRepository
) : ViewModel() {

    private var selectingNoteId = ""
    private val openEditTextSubject = PublishSubject.create<Note>() //TODO: Modify to live data

    val allNotes: LiveData<List<Note>>
        get() = _allNotes
    private val _allNotes = MutableLiveData<List<Note>>()

    val selectingNote: LiveData<Note>
        get() = _selectingNote
    private val _selectingNote = MutableLiveData<Note>()

    val selectingColor: LiveData<YBColor>
        get() = _selectingColor
    private val _selectingColor = MutableLiveData<YBColor>()

    init {
        viewModelScope.launch {
            noteRepository.getAllNotes().collect {
                Log.d("Fan", "getAllNotes().collect")
                _allNotes.postValue(it)
            }
        }
    }

    val openEditTextScreen: Observable<Note> = openEditTextSubject.hide()

    fun moveNote(noteId: String, positionDelta: Position) { //TODO:Check move note function
        viewModelScope.launch {
            noteRepository.getNoteById(noteId).collect {
                it?.let {
                    noteRepository.putNote(it.copy(position = it.position + positionDelta))
                }
            }
        }
    }

    fun addNewNote() {
        val newNote = Note.createRandomNote()
        noteRepository.createNote(newNote)
    }

    fun tapNote(note: Note) {
        if (selectingNoteId == note.id) {
            removeSelectingNote()
        } else {
            selectingNoteId = note.id
        }
        getSelectingNote()
    }

    fun tapCanvas() {
        removeSelectingNote()
        getSelectingNote()
    }

    fun onDeleteClicked() {
        noteRepository.deleteNote(selectingNoteId)
        removeSelectingNote()
        getSelectingNote()
    }

    fun onColorSelected(color: YBColor) {
        val newNote = _selectingNote.value?.copy(color = color)
        newNote?.let {
            noteRepository.putNote(it)
        }
    }

    fun onEditTextClicked() {
//        runOnSelectingNote { note ->
//            openEditTextSubject.onNext(note)
//        }
    }

    private fun removeSelectingNote() {
        selectingNoteId = ""
        getSelectingNote()
    }

    private fun getSelectingNote() {
        viewModelScope.launch {
            noteRepository.getNoteById(selectingNoteId).collect {
                _selectingNote.postValue(it)
                _selectingColor.postValue(it?.color ?: YBColor.Aquamarine)
            }
        }
    }

}