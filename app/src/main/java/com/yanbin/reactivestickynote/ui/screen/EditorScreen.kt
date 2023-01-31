package com.yanbin.reactivestickynote.ui.screen

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.LifecycleOwner
import com.yanbin.reactivestickynote.domain.EditorViewModel
import com.yanbin.reactivestickynote.model.Note
import com.yanbin.reactivestickynote.ui.view.BoardView
import com.yanbin.utils.subscribeBy
import com.yanbin.utils.toMain

@ExperimentalAnimationApi
@Composable
fun EditorScreen(
    viewModel: EditorViewModel,
    openEditTextScreen: (Note) -> Unit
) {
    viewModel.openEditTextScreen
        .toMain()
        .subscribeBy (onNext = openEditTextScreen)

    Surface(color = MaterialTheme.colors.background) {
        Box(
            Modifier.fillMaxSize()
                .pointerInput("Editor") {
                    detectTapGestures { viewModel.tapCanvas() }
                }
        ) {
//            val selectedNote by viewModel.selectingNote.subscribeAsState(initial = Optional.empty())
//            val selectingColor by viewModel.selectingColor.subscribeAsState(initial = YBColor.Aquamarine)
            val allNotes by viewModel.allNotes.observeAsState(initial = emptyList())
            BoardView(
                allNotes,
                null, //selectedNote
                viewModel::moveNote,
                viewModel::tapNote
            )
//            AnimatedVisibility(
//                visible = !selectedNote.isPresent,
//                modifier = Modifier.align(Alignment.BottomEnd)
//            ) {
//                FloatingActionButton(
//                    onClick = { viewModel.addNewNote() },
//                    modifier = Modifier
//                        .padding(8.dp)
//                ) {
//                    val painter = painterResource(id = R.drawable.ic_add)
//                    Icon(painter = painter, contentDescription = "Add")
//                }
//            }
//
//            AnimatedVisibility(
//                visible = selectedNote.isPresent,
//                modifier = Modifier.align(Alignment.BottomCenter)
//            ) {
//                MenuView(
//                    selectedColor = selectingColor,
//                    onDeleteClicked = viewModel::onDeleteClicked,
//                    onColorSelected = viewModel::onColorSelected,
//                    onTextClicked = viewModel::onEditTextClicked
//                )
//            }
        }
    }
}