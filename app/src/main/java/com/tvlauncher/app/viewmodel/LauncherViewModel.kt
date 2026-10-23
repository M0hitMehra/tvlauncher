package com.tvlauncher.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tvlauncher.app.data.AppInfo
import com.tvlauncher.app.data.AppRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class LauncherUiState(
    val visibleApps: List<AppInfo> = emptyList(),
    val allApps: List<AppInfo> = emptyList(),
    val hiddenApps: Set<String> = emptySet(),
    val appOrder: List<String> = emptyList(),
    val gridColumns: Int = 5,
    val showClock: Boolean = true,
    val isLoading: Boolean = true,
    val isEditMode: Boolean = false,
    val selectedAppIndex: Int = -1,
    val showSettingsPanel: Boolean = false,
    val showHiddenAppsPanel: Boolean = false
)

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository(application)

    private val _uiState = MutableStateFlow(LauncherUiState())
    val uiState: StateFlow<LauncherUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            // Combine all preference flows
            combine(
                repository.gridColumns,
                repository.showClock,
                repository.hiddenApps,
                repository.appOrder
            ) { columns, clock, hidden, order ->
                Quadruple(columns, clock, hidden, order)
            }.collect { (columns, clock, hidden, order) ->
                val allApps = if (_uiState.value.allApps.isEmpty()) {
                    repository.getInstalledApps().also { apps ->
                        _uiState.update { it.copy(allApps = apps, isLoading = false) }
                    }
                } else {
                    _uiState.value.allApps
                }

                val visible = repository.getVisibleApps(allApps, hidden, order)
                _uiState.update { state ->
                    state.copy(
                        visibleApps = visible,
                        hiddenApps = hidden,
                        appOrder = order,
                        gridColumns = columns,
                        showClock = clock,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun refreshApps() {
        viewModelScope.launch {
            val allApps = repository.getInstalledApps()
            val state = _uiState.value
            val visible = repository.getVisibleApps(allApps, state.hiddenApps, state.appOrder)
            _uiState.update { it.copy(allApps = allApps, visibleApps = visible, isLoading = false) }
        }
    }

    fun launchApp(packageName: String) {
        repository.launchApp(packageName)
    }

    fun openSettings() {
        repository.openSettings()
    }

    fun enterEditMode(index: Int) {
        _uiState.update { it.copy(isEditMode = true, selectedAppIndex = index) }
    }

    fun exitEditMode() {
        viewModelScope.launch {
            val state = _uiState.value
            val order = state.visibleApps.map { it.packageName }
            repository.setAppOrder(order)
            _uiState.update { it.copy(isEditMode = false, selectedAppIndex = -1) }
        }
    }

    fun moveApp(fromIndex: Int, toIndex: Int) {
        val apps = _uiState.value.visibleApps.toMutableList()
        if (fromIndex < 0 || toIndex < 0 || fromIndex >= apps.size || toIndex >= apps.size) return
        val app = apps.removeAt(fromIndex)
        apps.add(toIndex, app)
        _uiState.update { it.copy(visibleApps = apps, selectedAppIndex = toIndex) }
    }

    fun hideApp(packageName: String) {
        viewModelScope.launch {
            val current = _uiState.value.hiddenApps
            repository.toggleHideApp(packageName, current)
        }
    }

    fun unhideApp(packageName: String) {
        viewModelScope.launch {
            val current = _uiState.value.hiddenApps
            val updated = current - packageName
            repository.setHiddenApps(updated)
        }
    }

    fun setGridColumns(columns: Int) {
        viewModelScope.launch { repository.setGridColumns(columns) }
    }

    fun setShowClock(show: Boolean) {
        viewModelScope.launch { repository.setShowClock(show) }
    }

    fun toggleSettingsPanel() {
        _uiState.update { it.copy(showSettingsPanel = !it.showSettingsPanel, showHiddenAppsPanel = false) }
    }

    fun toggleHiddenAppsPanel() {
        _uiState.update { it.copy(showHiddenAppsPanel = !it.showHiddenAppsPanel, showSettingsPanel = false) }
    }

    fun closePanels() {
        _uiState.update { it.copy(showSettingsPanel = false, showHiddenAppsPanel = false) }
    }
}

// Helper since Kotlin has no built-in Quadruple
data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
