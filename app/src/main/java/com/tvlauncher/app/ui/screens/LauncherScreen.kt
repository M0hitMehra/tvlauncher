package com.tvlauncher.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.tvlauncher.app.data.AppInfo
import com.tvlauncher.app.ui.components.*
import com.tvlauncher.app.ui.theme.LauncherBackground
import com.tvlauncher.app.viewmodel.LauncherUiState

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun LauncherScreen(
    uiState: LauncherUiState,
    onAppClick: (String) -> Unit,
    onAppLongClick: (Int) -> Unit,
    onHideApp: (String) -> Unit,
    onMoveApp: (Int, Int) -> Unit,
    onExitEditMode: () -> Unit,
    onToggleSettings: () -> Unit,
    onToggleHiddenApps: () -> Unit,
    onGridColumnsChange: (Int) -> Unit,
    onShowClockChange: (Boolean) -> Unit,
    onSystemSettings: () -> Unit,
    onUnhideApp: (String) -> Unit,
    onClosePanels: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gridState = rememberLazyGridState()

    // Focus management
    val settingsFocusRequester = remember { FocusRequester() }
    val gridFocusRequester = remember { FocusRequester() }

    // Build label map for hidden apps panel
    val hiddenAppLabels = remember(uiState.allApps) {
        uiState.allApps.associate { it.packageName to it.label }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LauncherBackground)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Main content area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                // Top bar
                TopBar(
                    showClock = uiState.showClock,
                    isEditMode = uiState.isEditMode,
                    settingsFocusRequester = settingsFocusRequester,
                    onToggleSettings = onToggleSettings,
                    onExitEditMode = onExitEditMode
                )

                // App Grid
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Loading apps...",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 18.sp
                        )
                    }
                } else {
                    AppGrid(
                        apps = uiState.visibleApps,
                        columns = uiState.gridColumns,
                        isEditMode = uiState.isEditMode,
                        selectedIndex = uiState.selectedAppIndex,
                        gridState = gridState,
                        gridFocusRequester = gridFocusRequester,
                        onAppClick = onAppClick,
                        onAppLongClick = onAppLongClick,
                        onMoveApp = onMoveApp,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                }
            }

            // Side panels (settings / hidden apps)
            AnimatedVisibility(
                visible = uiState.showSettingsPanel,
                enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
            ) {
                SettingsPanel(
                    gridColumns = uiState.gridColumns,
                    showClock = uiState.showClock,
                    onGridColumnsChange = onGridColumnsChange,
                    onShowClockChange = onShowClockChange,
                    onOpenHiddenApps = onToggleHiddenApps,
                    onSystemSettings = onSystemSettings,
                    modifier = Modifier.fillMaxHeight()
                )
            }

            AnimatedVisibility(
                visible = uiState.showHiddenAppsPanel,
                enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
            ) {
                HiddenAppsPanel(
                    hiddenAppPackages = uiState.hiddenApps,
                    hiddenAppLabels = hiddenAppLabels,
                    onUnhide = onUnhideApp,
                    modifier = Modifier.fillMaxHeight()
                )
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TopBar(
    showClock: Boolean,
    isEditMode: Boolean,
    settingsFocusRequester: FocusRequester,
    onToggleSettings: () -> Unit,
    onExitEditMode: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showClock) {
            ClockWidget()
        } else {
            Spacer(Modifier.weight(1f))
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isEditMode) {
                EditModeIndicator(onExitEditMode = onExitEditMode)
            }
            SettingsButton(
                focusRequester = settingsFocusRequester,
                onClick = onToggleSettings
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun EditModeIndicator(onExitEditMode: () -> Unit) {
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    Box(
        modifier = Modifier
            .padding(4.dp)
            .background(
                Color(0xFFFFD700).copy(alpha = if (isFocused) 1f else 0.8f),
                RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .onKeyEvent { event ->
                if ((event.key == Key.DirectionCenter || event.key == Key.Enter) &&
                    event.type == KeyEventType.KeyUp
                ) {
                    onExitEditMode()
                    true
                } else false
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "✦ EDIT MODE — OK to finish",
            color = Color.Black,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun SettingsButton(
    focusRequester: FocusRequester,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .size(52.dp)
            .background(
                if (isFocused) Color(0xFF1E3A5F) else Color(0xFF1A1A2E),
                RoundedCornerShape(26.dp)
            )
            .then(
                if (isFocused) Modifier.border(
                    2.dp,
                    Color(0xFF29B6F6),
                    RoundedCornerShape(26.dp)
                ) else Modifier
            )
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .onKeyEvent { event ->
                if ((event.key == Key.DirectionCenter || event.key == Key.Enter) &&
                    event.type == KeyEventType.KeyUp
                ) {
                    onClick()
                    true
                } else false
            },
        contentAlignment = Alignment.Center
    ) {
        Text(text = "⚙", fontSize = 24.sp, color = Color.White)
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun AppGrid(
    apps: List<AppInfo>,
    columns: Int,
    isEditMode: Boolean,
    selectedIndex: Int,
    gridState: LazyGridState,
    gridFocusRequester: FocusRequester,
    onAppClick: (String) -> Unit,
    onAppLongClick: (Int) -> Unit,
    onMoveApp: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var focusedIndex by remember { mutableIntStateOf(0) }
    val focusRequesters = remember(apps.size) { Array(apps.size) { FocusRequester() } }

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        state = gridState,
        modifier = modifier
            .focusRequester(gridFocusRequester)
            .padding(horizontal = 24.dp, vertical = 8.dp),
        contentPadding = PaddingValues(bottom = 32.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(apps, key = { _, app -> app.packageName }) { index, app ->
            val isFocused = focusedIndex == index
            val isSelected = isEditMode && selectedIndex == index

            AppCard(
                label = app.label,
                icon = app.icon,
                isFocused = isFocused,
                isSelected = isSelected,
                isEditMode = isEditMode,
                focusRequester = focusRequesters[index],
                onFocusChanged = { focused ->
                    if (focused) focusedIndex = index
                },
                onClick = {
                    if (isEditMode) {
                        if (selectedIndex >= 0 && selectedIndex != index) {
                            onMoveApp(selectedIndex, index)
                        }
                    } else {
                        onAppClick(app.packageName)
                    }
                },
                onLongClick = {
                    onAppLongClick(index)
                },
                modifier = Modifier
                    .aspectRatio(0.85f)
                    // In edit mode: intercept D-pad to move the selected card
                    .onKeyEvent { event ->
                        if (!isEditMode || selectedIndex < 0 || event.type != KeyEventType.KeyDown) {
                            return@onKeyEvent false
                        }
                        val targetIndex = when (event.key) {
                            Key.DirectionRight -> (index + 1).coerceAtMost(apps.lastIndex)
                            Key.DirectionLeft  -> (index - 1).coerceAtLeast(0)
                            Key.DirectionDown  -> (index + columns).coerceAtMost(apps.lastIndex)
                            Key.DirectionUp    -> (index - columns).coerceAtLeast(0)
                            else -> return@onKeyEvent false
                        }
                        if (targetIndex != index && isSelected) {
                            onMoveApp(index, targetIndex)
                            focusedIndex = targetIndex
                            true
                        } else false
                    }
            )
        }
    }
}
