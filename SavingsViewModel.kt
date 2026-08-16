package com.savingstracker.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.savingstracker.app.data.SavingsBox
import com.savingstracker.app.data.SavingsDatabase
import com.savingstracker.app.data.SavingsRepository
import com.savingstracker.app.data.SavingsSummary
import com.savingstracker.app.data.SettingsDataStore
import com.savingstracker.app.data.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class SavingsUiState(
    val boxes: List<SavingsBox> = emptyList(),
    val summary: SavingsSummary = SavingsSummary(0, 0, 0, 0, 0, 0.0, 0, 0, 0, 0.0),
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val deadline: LocalDate = SettingsDataStore.DEFAULT_DEADLINE,
    val isLoading: Boolean = true
)

class SavingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SavingsRepository(
        SavingsDatabase.getInstance(application).savingsDao()
    )
    private val settings = SettingsDataStore(application)

    private val _showResetDialog = MutableStateFlow(false)
    val showResetDialog: StateFlow<Boolean> = _showResetDialog

    val uiState: StateFlow<SavingsUiState> = combine(
        repository.boxesFlow,
        settings.themeMode,
        settings.deadline
    ) { boxes, theme, deadline ->
        SavingsUiState(
            boxes = boxes,
            summary = repository.buildSummary(boxes, deadline),
            themeMode = theme,
            deadline = deadline,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SavingsUiState()
    )

    init {
        viewModelScope.launch {
            settings.ensureStartDateSet()
            repository.ensureSeeded()
        }
    }

    fun toggleBox(box: SavingsBox) {
        viewModelScope.launch { repository.toggleBox(box) }
    }

    fun smartPickSuggestion(boxes: List<SavingsBox>): SavingsBox? = repository.smartPick(boxes)

    fun saveSmartPick(box: SavingsBox) {
        viewModelScope.launch { repository.saveSpecificBox(box.id, uiState.value.boxes) }
    }

    fun todaysBoxes(boxes: List<SavingsBox>) = repository.todaysBoxes(boxes)

    fun groupByDate(boxes: List<SavingsBox>) = repository.groupByDate(boxes)

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { settings.setThemeMode(mode) }
    }

    fun requestReset() {
        _showResetDialog.value = true
    }

    fun cancelReset() {
        _showResetDialog.value = false
    }

    fun confirmReset() {
        viewModelScope.launch {
            repository.resetChallenge()
            settings.resetStartDate()
            _showResetDialog.value = false
        }
    }
}
