package com.example.envirosense.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.envirosense.data.repository.EnviroRepository
import com.example.envirosense.domain.model.Measurement
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Stan UI dla ekranu History
 */
data class HistoryUiState(
    val isLoading: Boolean = true,
    val measurements: List<Measurement> = emptyList(),
    val error: String? = null,
    val showDeleteAllDialog: Boolean = false,
    val deleteSuccess: Boolean = false
)

/**
 * ViewModel dla ekranu History
 */
class HistoryViewModel(
    private val repository: EnviroRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()
    
    init {
        loadMeasurements()
    }
    
    /**
     * Ładuje wszystkie pomiary
     */
    private fun loadMeasurements() {
        viewModelScope.launch {
            repository.getAllMeasurements().collect { measurements ->
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        measurements = measurements
                    )
                }
            }
        }
    }
    
    /**
     * Usuwa pojedynczy pomiar
     */
    fun deleteMeasurement(measurement: Measurement) {
        viewModelScope.launch {
            try {
                repository.deleteMeasurement(measurement)
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = "Nie udało się usunąć pomiaru: ${e.message}")
                }
            }
        }
    }
    
    /**
     * Pokazuje dialog potwierdzenia usunięcia wszystkich
     */
    fun showDeleteAllDialog() {
        _uiState.update { it.copy(showDeleteAllDialog = true) }
    }
    
    /**
     * Ukrywa dialog potwierdzenia
     */
    fun hideDeleteAllDialog() {
        _uiState.update { it.copy(showDeleteAllDialog = false) }
    }
    
    /**
     * Usuwa wszystkie pomiary
     */
    fun deleteAllMeasurements() {
        viewModelScope.launch {
            try {
                repository.deleteAllMeasurements()
                _uiState.update { 
                    it.copy(
                        showDeleteAllDialog = false,
                        deleteSuccess = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        showDeleteAllDialog = false,
                        error = "Nie udało się usunąć pomiarów: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * Resetuje flagę sukcesu
     */
    fun clearDeleteSuccess() {
        _uiState.update { it.copy(deleteSuccess = false) }
    }
    
    /**
     * Czyści błąd
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    /**
     * Factory do tworzenia ViewModelu
     */
    class Factory(
        private val repository: EnviroRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
                return HistoryViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
