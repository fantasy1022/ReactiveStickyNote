package com.yanbin.reactivestickynote.domain

import androidx.lifecycle.ViewModel
import com.yanbin.reactivestickynote.data.NoteRepository

class EditTextViewModel(
    private val noteRepository: NoteRepository, private val noteId: String, defaultText: String
) : ViewModel() {

//    val text: LiveData<String>
//        get() = _text
//    private val _text = MutableLiveData(defaultText)
//
//    val leavePage: SingleLiveEvent<Unit>
//        get() = _leavePage
//    private val _leavePage = SingleLiveEvent<Unit>()
//
//    fun onTextChanged(newText: String) {
//        _text.value = newText
//    }
//
//    fun onConfirmClicked() {
//        viewModelScope.launch {
//            noteRepository.getNoteById(noteId).collect {
//                it?.let {
//                    noteRepository.putNote(it.copy(text = _text.value!!))
//                    _leavePage.value = Unit
//                }
//            }
//        }
//    }
//
//    fun onCancelClicked() {
//        _leavePage.value = Unit
//    }

}