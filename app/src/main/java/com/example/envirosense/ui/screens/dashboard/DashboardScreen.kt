package com.example.envirosense.ui.screens.dashboard

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.envirosense.data.repository.EnviroRepository
import com.example.envirosense.ui.components.AirQualityCard
import com.example.envirosense.ui.components.ErrorContent
import com.example.envirosense.ui.components.LoadingContent
import com.example.envirosense.ui.components.LocationPermissionContent
import com.example.envirosense.ui.components.WeatherCard
import com.example.envirosense.util.LocationHelper

/**
 * Ekran główny - Dashboard z aktualnymi danymi środowiskowymi
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    repository: EnviroRepository,
    locationHelper: LocationHelper
) {
    val viewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModel.Factory(repository, locationHelper)
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.any { it }
        if (granted) {
            viewModel.checkLocationPermission()
        }
    }

    // Obsługa snackbar dla sukcesu/błędu zapisu
    LaunchedEffect(uiState.saveSuccess, uiState.saveError) {
        when {
            uiState.saveSuccess -> {
                snackbarHostState.showSnackbar("Pomiar zapisany!")
                viewModel.clearSaveSuccess()
            }
            uiState.saveError != null -> {
                snackbarHostState.showSnackbar(uiState.saveError!!)
                viewModel.clearError()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🌿",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "EnviroSense",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    if (uiState.measurement != null) {
                        IconButton(
                            onClick = { viewModel.refresh() },
                            enabled = !uiState.isRefreshing
                        ) {
                            if (uiState.isRefreshing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Odśwież"
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                // Brak uprawnień
                !uiState.hasLocationPermission -> {
                    LocationPermissionContent(
                        onRequestPermission = {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    )
                }

                // Ładowanie
                uiState.isLoading -> {
                    LoadingContent(message = "Pobieranie danych środowiskowych...")
                }

                // Błąd
                uiState.error != null && uiState.measurement == null -> {
                    ErrorContent(
                        message = uiState.error!!,
                        onRetry = { viewModel.loadEnvironmentData() }
                    )
                }

                // Dane pobrane
                uiState.measurement != null -> {
                    DashboardContent(
                        uiState = uiState,
                        onSaveMeasurement = { viewModel.saveMeasurement() }
                    )
                }
            }
        }
    }
}

@Composable
private fun DashboardContent(
    uiState: DashboardUiState,
    onSaveMeasurement: () -> Unit
) {
    val measurement = uiState.measurement ?: return
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Lokalizacja
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = measurement.location.getDisplayName(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Karta pogody
        WeatherCard(weather = measurement.weather)

        // Karta jakości powietrza
        AirQualityCard(airQuality = measurement.airQuality)

        // Przycisk zapisu
        Button(
            onClick = onSaveMeasurement,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !uiState.isSaving
        ) {
            if (uiState.isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            } else {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = if (uiState.isSaving) "Zapisywanie..." else "Zapisz pomiar",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }

        // Informacja o czasie aktualizacji
        Text(
            text = "Ostatnia aktualizacja: ${measurement.getFormattedTime()}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}