package com.example.envirosense.ui.screens.dashboard

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.envirosense.data.repository.EnviroRepository
import com.example.envirosense.domain.model.Measurement
import com.example.envirosense.util.LocationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Stan UI dla Dashboard
 */
data class DashboardUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val measurement: Measurement? = null,
    val error: String? = null,
    val hasLocationPermission: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val saveError: String? = null
)

/**
 * ViewModel dla ekranu Dashboard
 */
class DashboardViewModel(
    private val repository: EnviroRepository,
    private val locationHelper: LocationHelper
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
    
    init {
        checkLocationPermission()
    }
    
    /**
     * Sprawdza uprawnienia do lokalizacji
     */
    fun checkLocationPermission() {
        val hasPermission = locationHelper.hasLocationPermission()
        _uiState.update { it.copy(hasLocationPermission = hasPermission) }
        
        if (hasPermission && _uiState.value.measurement == null) {
            loadEnvironmentData()
        }
    }
    
    /**
     * Ładuje dane środowiskowe
     */
    fun loadEnvironmentData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                // Pobierz lokalizację
                val location = locationHelper.getCurrentLocation()
                
                if (location == null) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "Nie udało się pobrać lokalizacji. Sprawdź czy GPS jest włączony."
                        )
                    }
                    return@launch
                }
                
                // Pobierz dane środowiskowe
                fetchDataForLocation(location)
                
            } catch (e: SecurityException) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        hasLocationPermission = false,
                        error = "Brak uprawnień do lokalizacji"
                    )
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
     * Odświeża dane
     */
    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, error = null) }
            
            try {
                val location = locationHelper.getCurrentLocation()
                
                if (location == null) {
                    _uiState.update { 
                        it.copy(
                            isRefreshing = false,
                            error = "Nie udało się pobrać lokalizacji"
                        )
                    }
                    return@launch
                }
                
                fetchDataForLocation(location, isRefreshing = true)
                
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isRefreshing = false, 
                        error = "Błąd: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * Pobiera dane dla podanej lokalizacji
     */
    private suspend fun fetchDataForLocation(location: Location, isRefreshing: Boolean = false) {
        val result = repository.fetchEnvironmentData(
            latitude = location.latitude,
            longitude = location.longitude
        )
        
        result.fold(
            onSuccess = { measurement ->
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        measurement = measurement,
                        error = null
                    )
                }
            },
            onFailure = { throwable ->
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = "Nie udało się pobrać danych: ${throwable.message}"
                    )
                }
            }
        )
    }
    
    /**
     * Zapisuje aktualny pomiar
     */
    fun saveMeasurement() {
        val measurement = _uiState.value.measurement ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, saveError = null, saveSuccess = false) }
            
            try {
                repository.saveMeasurement(measurement)
                _uiState.update { 
                    it.copy(
                        isSaving = false, 
                        saveSuccess = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isSaving = false, 
                        saveError = "Nie udało się zapisać pomiaru: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * Resetuje flagę sukcesu zapisu
     */
    fun clearSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }
    
    /**
     * Czyści błąd
     */
    fun clearError() {
        _uiState.update { it.copy(error = null, saveError = null) }
    }
    
    /**
     * Factory do tworzenia ViewModelu z parametrami
     */
    class Factory(
        private val repository: EnviroRepository,
        private val locationHelper: LocationHelper
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
                return DashboardViewModel(repository, locationHelper) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
