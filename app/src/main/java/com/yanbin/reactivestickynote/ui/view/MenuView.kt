package com.yanbin.reactivestickynote.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.yanbin.reactivestickynote.R
import com.yanbin.reactivestickynote.ui.vm.ContextMenuViewModel
import com.yanbin.reactivestickynote.model.YBColor
import org.koin.androidx.viewmodel.ext.android.viewModel

@Composable
fun StatefulMenuView(
    modifier: Modifier = Modifier
) {
    val contextMenuViewModel by LocalViewModelStoreOwner.current!!.viewModel<ContextMenuViewModel>()
    val selectedColor by contextMenuViewModel.selectedColor.subscribeAsState(initial = YBColor.Aquamarine)
    
    MenuView(
        modifier = modifier,
        selectedColor = selectedColor, 
        allColors = contextMenuViewModel.colorOptions,
        onDeleteClicked = contextMenuViewModel::onDeleteClicked, 
        onColorSelected = contextMenuViewModel::onColorSelected,
        onTextClicked = contextMenuViewModel::onEditTextClicked
    ) 
}


@Composable
fun MenuView(
    modifier: Modifier = Modifier,
    selectedColor: YBColor,
    allColors: List<YBColor>,
    onDeleteClicked: () -> Unit,
    onColorSelected: (YBColor) -> Unit,
    onTextClicked: () -> Unit
) {
    var expended by remember {
        mutableStateOf(false)
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        elevation = 4.dp,
        color = MaterialTheme.colors.surface
    ) {

        Row {
            IconButton(onClick = onDeleteClicked ) {
                val painter = painterResource(id = R.drawable.ic_delete)
                Icon(painter = painter, contentDescription = "Delete")
            }

            IconButton(onClick = onTextClicked ) {
                val painter = painterResource(id = R.drawable.ic_text)
                Icon(painter = painter, contentDescription = "Edit text")
            }

            IconButton(onClick = { expended = true }) {
                Box(modifier = Modifier
                    .size(24.dp)
                    .background(Color(selectedColor.color), shape = CircleShape))

                DropdownMenu(expanded = expended, onDismissRequest = { expended = false }) {
                    for (color in allColors) {
                        DropdownMenuItem(onClick = {
                            onColorSelected(color)
                            expended = false
                        }) {
                            Box(modifier = Modifier
                                .size(24.dp)
                                .background(Color(color.color), shape = CircleShape))
                        }
                    }
                }
            }
        }
    }

}