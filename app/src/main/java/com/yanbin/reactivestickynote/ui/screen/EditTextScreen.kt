package com.yanbin.reactivestickynote.ui.screen

import androidx.compose.runtime.Composable
import androidx.lifecycle.LifecycleOwner
import com.yanbin.reactivestickynote.domain.EditTextViewModel


@Composable
fun EditTextScreen(
    lifecycleOwner: LifecycleOwner,
    editTextViewModel: EditTextViewModel,
    onLeaveScreen: () -> Unit,
) {
//    val text by editTextViewModel.text.observeAsState(initial = "")
//
//    editTextViewModel.leavePage.observe(lifecycleOwner) {
//        onLeaveScreen.invoke()
//    }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.White)
//            .background(TransparentBlack)
//    ) {
//        TextField(
//            value = text,
//            onValueChange = editTextViewModel::onTextChanged,
//            modifier = Modifier
//                .align(Alignment.Center)
//                .fillMaxWidth(fraction = 0.8f),
//            colors = TextFieldDefaults.textFieldColors(
//                backgroundColor = Color.Transparent,
//                textColor = Color.White
//            ),
//            textStyle = MaterialTheme.typography.h5
//        )
//
//        IconButton(
//            modifier = Modifier.align(Alignment.TopStart),
//            onClick = editTextViewModel::onCancelClicked
//        ) {
//            val painter = painterResource(id = R.drawable.ic_close)
//            Icon(painter = painter, contentDescription = "Close", tint = Color.White)
//        }
//
//        IconButton(
//            modifier = Modifier.align(Alignment.TopEnd),
//            onClick = editTextViewModel::onConfirmClicked
//        ) {
//            val painter = painterResource(id = R.drawable.ic_check)
//            Icon(painter = painter, contentDescription = "Delete", tint = Color.White)
//        }
//    }
}
