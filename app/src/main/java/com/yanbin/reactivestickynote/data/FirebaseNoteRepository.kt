package com.yanbin.reactivestickynote.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.yanbin.reactivestickynote.model.Note
import com.yanbin.reactivestickynote.model.Position
import com.yanbin.reactivestickynote.model.YBColor
import com.yanbin.utils.toIO
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.Observables
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.*
import java.util.concurrent.TimeUnit

class FirebaseNoteRepository: NoteRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val allNotesSubject = BehaviorSubject.create<List<Note>>()
    private val updatingNoteSubject = BehaviorSubject.createDefault(Optional.empty<Note>())

    private val query = firestore.collection(COLLECTION_NOTES)
        .limit(100)

    init {
        query.addSnapshotListener { result, e ->
            result?.let { onSnapshotUpdated(it) }
        }

        updatingNoteSubject
            .throttleLast(1000, TimeUnit.MILLISECONDS)
            .toIO()
            .subscribe { optNote ->
                optNote.ifPresent { setNoteDocument(it) }
            }

        updatingNoteSubject
            .filter { it.isPresent }
            .debounce(1000, TimeUnit.MILLISECONDS)
            .subscribe {
                updatingNoteSubject.onNext(Optional.empty<Note>())
            }
    }

    override fun getAllNotes(): Observable<List<Note>> {
        return Observables.combineLatest(updatingNoteSubject, allNotesSubject)
            .map { (optNote, allNotes) ->
                optNote.map { note ->
                    val noteIndex = allNotes.indexOfFirst { it.id == note.id }
                    allNotes.subList(0, noteIndex) + note + allNotes.subList(noteIndex + 1, allNotes.size)
                }.orElseGet { allNotes }
            }
    }

    override fun getNoteById(id: String): Observable<Note> {
        return allNotesSubject.map { notes ->
            Optional.ofNullable(notes.find { note -> note.id == id })
        }.mapOptional { it }
    }

    override fun createNote(note: Note) {
        setNoteDocument(note)
    }

    override fun putNote(note: Note) {
        updatingNoteSubject.onNext(Optional.of(note))
    }

    override fun deleteNote(noteId: String) {
        firestore.collection(COLLECTION_NOTES)
            .document(noteId)
            .delete()
    }

    private fun onSnapshotUpdated(snapshot: QuerySnapshot) {
        val allNotes = snapshot
            .map { document -> documentToNotes(document) }

        allNotesSubject.onNext(allNotes)
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