package com.tvlauncher.app

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import com.tvlauncher.app.ui.screens.LauncherScreen
import com.tvlauncher.app.ui.theme.TVLauncherTheme
import com.tvlauncher.app.viewmodel.LauncherViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: LauncherViewModel by viewModels()

    // Long-press detection state
    private var centerKeyDownTime = 0L
    private var longPressTriggered = false
    private val longPressThresholdMs = 600L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TVLauncherTheme {
                val uiState by viewModel.uiState.collectAsState()

                LauncherScreen(
                    uiState = uiState,
                    onAppClick = { pkg ->
                        if (!uiState.isEditMode) {
                            viewModel.launchApp(pkg)
                        }
                    },
                    onAppLongClick = { index ->
                        if (uiState.isEditMode) {
                            viewModel.exitEditMode()
                        } else {
                            viewModel.enterEditMode(index)
                        }
                    },
                    onHideApp = { pkg ->
                        viewModel.hideApp(pkg)
                        viewModel.exitEditMode()
                    },
                    onMoveApp = { from, to ->
                        viewModel.moveApp(from, to)
                    },
                    onExitEditMode = { viewModel.exitEditMode() },
                    onToggleSettings = { viewModel.toggleSettingsPanel() },
                    onToggleHiddenApps = { viewModel.toggleHiddenAppsPanel() },
                    onGridColumnsChange = { viewModel.setGridColumns(it) },
                    onShowClockChange = { viewModel.setShowClock(it) },
                    onSystemSettings = { viewModel.openSettings() },
                    onUnhideApp = { viewModel.unhideApp(it) },
                    onClosePanels = { viewModel.closePanels() }
                )
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
            if (event?.repeatCount == 0) {
                centerKeyDownTime = System.currentTimeMillis()
                longPressTriggered = false
            } else if (!longPressTriggered &&
                System.currentTimeMillis() - centerKeyDownTime >= longPressThresholdMs
            ) {
                longPressTriggered = true
                // Signal long press to ViewModel via the focused index
                val state = viewModel.uiState.value
                if (state.isEditMode) {
                    viewModel.exitEditMode()
                } else {
                    // The composable tracks focused index; pass -1 to use composable-level focus
                    // Long press is also handled directly inside AppCard via onLongClick callback
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val state = viewModel.uiState.value
            when {
                state.isEditMode -> {
                    viewModel.exitEditMode()
                    return true
                }
                state.showSettingsPanel || state.showHiddenAppsPanel -> {
                    viewModel.closePanels()
                    return true
                }
                else -> {
                    // On Back at home, do nothing (prevent accidental exit)
                    return true
                }
            }
        }
        return super.onKeyUp(keyCode, event)
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshApps()
    }
}
