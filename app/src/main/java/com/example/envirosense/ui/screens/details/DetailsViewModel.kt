package com.example.envirosense.ui.screens.details

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
 * Stan UI dla ekranu Details
 */
data class DetailsUiState(
    val isLoading: Boolean = true,
    val measurement: Measurement? = null,
    val error: String? = null,
    val showDeleteDialog: Boolean = false,
    val isDeleted: Boolean = false
)

/**
 * ViewModel dla ekranu Details
 */
class DetailsViewModel(
    private val measurementId: Long,
    private val repository: EnviroRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DetailsUiState())
    val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()
    
    init {
        loadMeasurement()
    }
    
    /**
     * Ładuje szczegóły pomiaru
     */
    private fun loadMeasurement() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val measurement = repository.getMeasurementById(measurementId)
                
                if (measurement != null) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            measurement = measurement
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "Nie znaleziono pomiaru"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Błąd: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * Pokazuje dialog potwierdzenia usunięcia
     */
    fun showDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = true) }
    }
    
    /**
     * Ukrywa dialog
     */
    fun hideDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = false) }
    }
    
    /**
     * Usuwa pomiar
     */
    fun deleteMeasurement() {
        viewModelScope.launch {
            try {
                repository.deleteMeasurementById(measurementId)
                _uiState.update { 
                    it.copy(
                        showDeleteDialog = false,
                        isDeleted = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        showDeleteDialog = false,
                        error = "Nie udało się usunąć pomiaru: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * Generuje tekst do udostępnienia
     */
    fun getShareText(): String? {
        return _uiState.value.measurement?.toShareText()
    }
    
    /**
     * Factory do tworzenia ViewModelu
     */
    class Factory(
        private val measurementId: Long,
        private val repository: EnviroRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
                return DetailsViewModel(measurementId, repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
