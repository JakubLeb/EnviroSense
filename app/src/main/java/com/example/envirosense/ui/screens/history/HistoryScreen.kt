package com.example.envirosense.ui.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.envirosense.data.repository.EnviroRepository
import com.example.envirosense.ui.components.EmptyContent
import com.example.envirosense.ui.components.LoadingContent
import com.example.envirosense.ui.components.MeasurementItem

/**
 * Ekran historii pomiarów
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    repository: EnviroRepository,
    onMeasurementClick: (Long) -> Unit
) {
    val viewModel: HistoryViewModel = viewModel(
        factory = HistoryViewModel.Factory(repository)
    )
    
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Obsługa snackbar
    LaunchedEffect(uiState.deleteSuccess, uiState.error) {
        when {
            uiState.deleteSuccess -> {
                snackbarHostState.showSnackbar("Wszystkie pomiary usunięte")
                viewModel.clearDeleteSuccess()
            }
            uiState.error != null -> {
                snackbarHostState.showSnackbar(uiState.error!!)
                viewModel.clearError()
            }
        }
    }
    
    // Dialog potwierdzenia usunięcia wszystkich
    if (uiState.showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideDeleteAllDialog() },
            title = { Text("Usuń wszystkie pomiary") },
            text = { Text("Czy na pewno chcesz usunąć wszystkie zapisane pomiary? Tej operacji nie można cofnąć.") },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.deleteAllMeasurements() }
                ) {
                    Text(
                        text = "Usuń",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.hideDeleteAllDialog() }
                ) {
                    Text("Anuluj")
                }
            }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Historia pomiarów",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    if (uiState.measurements.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.showDeleteAllDialog() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Usuń wszystkie",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
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
                // Ładowanie
                uiState.isLoading -> {
                    LoadingContent(message = "Ładowanie historii...")
                }
                
                // Pusta lista
                uiState.measurements.isEmpty() -> {
                    EmptyContent(
                        message = "Brak zapisanych pomiarów.\nZapisz pierwszy pomiar z ekranu Dashboard!"
                    )
                }
                
                // Lista pomiarów
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = uiState.measurements,
                            key = { it.id }
                        ) { measurement ->
                            MeasurementItem(
                                measurement = measurement,
                                onClick = { onMeasurementClick(measurement.id) },
                                onDelete = { viewModel.deleteMeasurement(measurement) }
                            )
                        }
                    }
                }
            }
        }
    }
}
