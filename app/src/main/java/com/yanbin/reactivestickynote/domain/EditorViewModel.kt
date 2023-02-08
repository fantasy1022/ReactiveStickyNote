package com.yanbin.reactivestickynote.domain

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yanbin.reactivestickynote.data.NoteRepository
import com.yanbin.reactivestickynote.model.Note
import com.yanbin.reactivestickynote.model.Position
import com.yanbin.reactivestickynote.model.YBColor
import com.yanbin.utils.withLatestFrom
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class EditorViewModel(
    private val noteRepository: NoteRepository
) : ViewModel() {

    private var selectingNoteId = ""

    //TODO: Use state to store
//    val openEditTextEvent: State<Note>
//        get() = _openEditTextEvent.receiveAsFlow()
//    private val _openEditTextEvent = Channel<Note>(Channel.BUFFERED)

    var allNotes = noteRepository.getAllNotes()

    val selectingNote: State<Note?>
        get() = _selectingNote
    private val _selectingNote = mutableStateOf<Note?>(null)

    val selectingColor: State<YBColor>
        get() = _selectingColor
    private val _selectingColor = mutableStateOf(YBColor.Aquamarine)

    private val positionFlow = MutableStateFlow(Pair("", Position(0f, 0f)))

    init {
        positionFlow.withLatestFrom(allNotes) { (noteId, positionDelta), notes ->
            val currentNote = notes.find { note -> note.id == noteId }
            currentNote?.copy(position = currentNote.position + positionDelta)
        }.filterNotNull().onEach {
            noteRepository.putNote(it)
        }.launchIn(viewModelScope)
    }

    fun moveNote(noteId: String, positionDelta: Position) {
        positionFlow.value = Pair(noteId, positionDelta)
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
        val newNote = selectingNote.value?.copy(color = color)
        newNote?.let {
            noteRepository.putNote(it)
        }
    }

    fun onEditTextClicked() {
//        selectingNote.value?.let {
//            _openEditTextEvent.value = it
//        }
    }

    private fun removeSelectingNote() {
        selectingNoteId = ""
        getSelectingNote()
    }

    private fun getSelectingNote() {
        viewModelScope.launch {
            noteRepository.getNoteById(selectingNoteId).collect {
                _selectingNote.value = it
                _selectingColor.value = it?.color ?: YBColor.Aquamarine
            }
        }
    }

}