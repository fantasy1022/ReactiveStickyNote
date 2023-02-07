package com.yanbin.reactivestickynote.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.yanbin.reactivestickynote.model.Note
import com.yanbin.reactivestickynote.model.Position
import com.yanbin.reactivestickynote.model.YBColor
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FirebaseNoteRepository : NoteRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val allNotes = MutableStateFlow<List<Note>>(emptyList())
    private val updatingNote = MutableStateFlow<Note?>(null)

    private val query = firestore.collection(COLLECTION_NOTES).limit(100)
    private val scope = MainScope()

    init {
        query.addSnapshotListener { result, e ->
            result?.let { onSnapshotUpdated(it) }
        }

        throttleUpdatingNote()
        debounceUpdateNote()
    }

    override fun getAllNotes(): Flow<List<Note>> {
        return allNotes.combine(updatingNote) { allNotes, optNote ->
            optNote?.let { note ->
                val noteIndex = allNotes.indexOfFirst { it.id == note.id }
                allNotes.subList(0, noteIndex) + note + allNotes.subList(
                    noteIndex + 1,
                    allNotes.size
                )
            } ?: allNotes
        }
    }

    override fun getNoteById(id: String): Flow<Note?> {
        return allNotes.map { notes ->
            notes.find { note -> note.id == id }
        }
    }

    override fun createNote(note: Note) {
        setNoteDocument(note)
    }

    override fun putNote(note: Note) {
        updatingNote.value = note
    }

    override fun deleteNote(noteId: String) {
        firestore.collection(COLLECTION_NOTES)
            .document(noteId)
            .delete()
    }

    private fun onSnapshotUpdated(snapshot: QuerySnapshot) {
        val allNotesFromSnapShot = snapshot
            .map { document -> documentToNotes(document) }
        allNotes.value = allNotesFromSnapShot
    }

    private fun throttleUpdatingNote() {
        scope.launch() {
            updatingNote
                .filterNotNull()
                .sample(1000)
                .collect {
                    setNoteDocument(it)
                }
        }
    }

    @kotlinx.coroutines.FlowPreview
    private fun debounceUpdateNote() {
        scope.launch() {
            updatingNote
                .filterNotNull()
                .debounce(300)
                .collect {
                    updatingNote.value = null
                }
        }
    }

    private fun setNoteDocument(note: Note) {
        val noteData = hashMapOf(
            FIELD_TEXT to note.text,
            FIELD_COLOR to note.color.color,
            FIELD_POSITION_X to note.position.x,
            FIELD_POSITION_Y to note.position.y
        )

        firestore.collection(COLLECTION_NOTES)
            .document(note.id)
            .set(noteData)
    }

    private fun documentToNotes(document: QueryDocumentSnapshot): Note {
        val data: Map<String, Any> = document.data
        val text = data[FIELD_TEXT] as? String? ?: ""
        val color = YBColor(data[FIELD_COLOR] as? Long? ?: 0)
        val positionX = data[FIELD_POSITION_X] as? Double? ?: 0f
        val positionY = data[FIELD_POSITION_Y] as? Double? ?: 0f
        val position = Position(positionX.toFloat(), positionY.toFloat())
        return Note(document.id, text, position, color)
    }

    companion object {
        const val COLLECTION_NOTES = "Notes"
        const val FIELD_TEXT = "text"
        const val FIELD_COLOR = "color"
        const val FIELD_POSITION_X = "positionX"
        const val FIELD_POSITION_Y = "positionY"
    }
}