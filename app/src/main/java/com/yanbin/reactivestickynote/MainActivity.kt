package com.yanbin.reactivestickynote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yanbin.reactivestickynote.account.AccountService
import com.yanbin.reactivestickynote.domain.EditTextViewModel
import com.yanbin.reactivestickynote.login.LoginScreen
import com.yanbin.reactivestickynote.login.LoginViewModel
import com.yanbin.reactivestickynote.ui.vm.EditorViewModel
import com.yanbin.reactivestickynote.ui.screen.EditTextScreen
import com.yanbin.reactivestickynote.ui.screen.StickyNoteEditorScreen
import com.yanbin.reactivestickynote.ui.screen.Screen
import com.yanbin.reactivestickynote.ui.theme.ReactiveStickyNoteTheme
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

@ExperimentalAnimationApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            ReactiveStickyNoteTheme {
                val accountService by inject<AccountService>()
                val startDestination = if (accountService.hasAccount()) {
                    Screen.Editor.route
                } else {
                    Screen.Login.route
                }

                NavHost(navController, startDestination = startDestination) {
                    composable(Screen.Login.route) {
                        val viewModel by viewModel<LoginViewModel>()
                        LoginScreen(
                            loginViewModel = viewModel,
                            toEditorPage = { navController.navigate(Screen.Editor.route) }
                        )
                    }

                    composable(Screen.Editor.route) {
                        val viewModel by viewModel<EditorViewModel>()
                        StickyNoteEditorScreen(
                            viewModel = viewModel,
                            openEditTextScreen = { note ->
                                navController.navigate(Screen.EditText.buildRoute(note.id, note.text))
                            }
                        )
                    }

                    composable(
                        Screen.EditText.route
                    ) { backStackEntry ->
                        val viewModel by backStackEntry.viewModel<EditTextViewModel> {
                            parametersOf(
                                backStackEntry.arguments?.getString(Screen.EditText.KEY_NOTE_ID),
                                backStackEntry.arguments?.getString(Screen.EditText.KEY_DEFAULT_TEXT),
                            )
                        }
                        EditTextScreen(viewModel, onLeaveScreen = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}

