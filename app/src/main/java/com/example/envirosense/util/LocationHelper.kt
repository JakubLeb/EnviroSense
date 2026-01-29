package com.example.envirosense.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Helper class do obsługi lokalizacji GPS
 */
class LocationHelper(private val context: Context) {
    
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    
    /**
     * Sprawdza czy aplikacja ma uprawnienia do lokalizacji
     */
    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * Pobiera aktualną lokalizację
     * 
     * @return Location lub null jeśli nie można pobrać lokalizacji
     * @throws SecurityException jeśli brak uprawnień
     * @throws Exception w przypadku innych błędów
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {
        if (!hasLocationPermission()) {
            throw SecurityException("Location permission not granted")
        }
        
        return suspendCancellableCoroutine { continuation ->
            val cancellationTokenSource = CancellationTokenSource()
            
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).addOnSuccessListener { location ->
                continuation.resume(location)
            }.addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
            
            continuation.invokeOnCancellation {
                cancellationTokenSource.cancel()
            }
        }
    }
    
    /**
     * Pobiera ostatnią znaną lokalizację (szybsze, ale może być nieaktualna)
     */
    @SuppressLint("MissingPermission")
    suspend fun getLastKnownLocation(): Location? {
        if (!hasLocationPermission()) {
            throw SecurityException("Location permission not granted")
        }
        
        return suspendCancellableCoroutine { continuation ->
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    continuation.resume(location)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }
    
    companion object {
        /**
         * Lista wymaganych uprawnień do lokalizacji
         */
        val LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }
}
