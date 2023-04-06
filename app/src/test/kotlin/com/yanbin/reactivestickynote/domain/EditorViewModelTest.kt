package com.yanbin.reactivestickynote.domain

import com.yanbin.reactivestickynote.data.NoteRepository
import com.yanbin.reactivestickynote.model.Note
import com.yanbin.reactivestickynote.model.Position
import com.yanbin.reactivestickynote.model.YBColor
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test

internal class EditorViewModelTest {

    private val noteRepository = mockk<NoteRepository>(relaxed = true)

    @Test
    fun loadStickyNoteTest() = runTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        every { noteRepository.getAllNotes() } returns flowOf(fakeNotes())

        val viewModel = EditorViewModel(noteRepository)
        val result = viewModel.allNotes.first()
        assertEquals(result, fakeNotes())
    }

    @Test
    fun `move note 1 with delta position (40, 40), expect noteRepository put Note with position (40, 40)`() =
        runTest {
            Dispatchers.setMain(UnconfinedTestDispatcher())
            every { noteRepository.getAllNotes() } returns flowOf(fakeNotes())
            val viewModel = EditorViewModel(noteRepository)
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.positionFlowUpdate()
            }

            viewModel.moveNote("1", Position(40f, 40f))

            verify {
                noteRepository.putNote(
                    Note(
                        id = "1",
                        text = "text1",
                        position = Position(40f, 40f),
                        color = YBColor.Aquamarine
                    )
                )
            }
        }

    @Test
    fun `move note 2 with delta position (40, 40), expect noteRepository put Note with position (50, 50)`() =
        runTest {
            Dispatchers.setMain(UnconfinedTestDispatcher())
            every { noteRepository.getAllNotes() } returns flowOf(fakeNotes())

            val viewModel = EditorViewModel(noteRepository)
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.positionFlowUpdate()
            }

            viewModel.moveNote("2", Position(40f, 40f))

            verify {
                noteRepository.putNote(
                    Note(
                        id = "2",
                        text = "text2",
                        position = Position(50f, 50f),
                        color = YBColor.Gorse
                    )
                )
            }
        }

    @Test
    fun `addNewNote called expect noteRepository add new note`() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        every { noteRepository.getAllNotes() } returns flowOf(emptyList())

        val viewModel = EditorViewModel(noteRepository)
        viewModel.addNewNote()

        verify { noteRepository.createNote(any()) }
    }

    @Test
    fun `tapNote called expect select the tapped note`() = runTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        val tappedNote = fakeNotes()[0]
        every { noteRepository.getAllNotes() } returns flowOf(fakeNotes())
        every { noteRepository.getNoteById(tappedNote.id) } returns flowOf(tappedNote)
        val viewModel = EditorViewModel(noteRepository)

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.tapNote(tappedNote)
        }
        val selectingNote = viewModel.selectingNote.value

        assertEquals(tappedNote.id, selectingNote!!.id)
    }

    @Test
    fun `tapCanvas called expect clear the selected note`() = runTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        val tappedNote = fakeNotes()[0]
        every { noteRepository.getAllNotes() } returns flowOf(fakeNotes())
        every { noteRepository.getNoteById(tappedNote.id) } returns flowOf(tappedNote)
        val viewModel = EditorViewModel(noteRepository)

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.tapNote(tappedNote)
            viewModel.tapCanvas()
        }
        val selectingNote = viewModel.selectingNote.value

        assertEquals(null, selectingNote)
    }

    @Test
    fun `onDeleteClicked called expect clear the selected note`() = runTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        val tappedNote = fakeNotes()[0]
        every { noteRepository.getAllNotes() } returns flowOf(fakeNotes())
        every { noteRepository.getNoteById(tappedNote.id) } returns flowOf(tappedNote)
        val viewModel = EditorViewModel(noteRepository)

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.tapNote(tappedNote)
            viewModel.onDeleteClicked()
        }
        val selectingNote = viewModel.selectingNote.value

        assertEquals(null, selectingNote)
    }

    @Test
    fun `onDeleteClicked called expect delete the note in noteRepository`() = runTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        val tappedNote = fakeNotes()[0]
        every { noteRepository.getAllNotes() } returns flowOf(fakeNotes())
        every { noteRepository.getNoteById(tappedNote.id) } returns flowOf(tappedNote)
        val viewModel = EditorViewModel(noteRepository)

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.tapNote(tappedNote)
            viewModel.onDeleteClicked()
        }

        verify { noteRepository.deleteNote(tappedNote.id) }
    }

//    @Test
//    fun `onEditTextClicked called expect openEditTextScreen`() {
//        every { noteRepository.getAllNotes() } returns Observable.just(fakeNotes())
//
//        val viewModel = EditorViewModel(noteRepository)
//        val openEditTextScreenObserver = viewModel.openEditTextScreen.test()
//        val tappedNote = fakeNotes()[0]
//        viewModel.tapNote(tappedNote)
//        viewModel.onEditTextClicked()
//
//        openEditTextScreenObserver.assertValue(tappedNote)
//    }

    @Test
    fun `tapNote called expect showing correct selectingColor`() = runTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        val tappedNote = fakeNotes()[0]
        every { noteRepository.getAllNotes() } returns flowOf(fakeNotes())
        every { noteRepository.getNoteById(tappedNote.id) } returns flowOf(tappedNote)
        val viewModel = EditorViewModel(noteRepository)

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.tapNote(tappedNote)
        }
        val selectingColor = viewModel.selectingColor.value

        assertEquals(tappedNote.color, selectingColor)
    }

    @Test
    fun `onColorSelected called expect update note with selected color`() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        val tappedNote = fakeNotes()[0]
        every { noteRepository.getAllNotes() } returns flowOf(fakeNotes())
        every { noteRepository.getNoteById(tappedNote.id) } returns flowOf(tappedNote)

        val viewModel = EditorViewModel(noteRepository)
        val selectedColor = YBColor.PaleCanary

        viewModel.tapNote(tappedNote)
        viewModel.onColorSelected(selectedColor)

        verify {
            noteRepository.putNote(
                Note(
                    id = "1",
                    text = "text1",
                    position = Position(0f, 0f),
                    color = YBColor.PaleCanary
                )
            )
        }
    }

    private fun fakeNotes(): List<Note> {
        return listOf(
            Note(id = "1", text = "text1", position = Position(0f, 0f), color = YBColor.Aquamarine),
            Note(id = "2", text = "text2", position = Position(10f, 10f), color = YBColor.Gorse),
            Note(id = "3", text = "text3", position = Position(20f, 20f), color = YBColor.HotPink),
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}

