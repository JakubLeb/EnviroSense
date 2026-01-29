package com.example.envirosense.domain.model

/**
 * Model reprezentujący lokalizację
 */
data class Location(
    val latitude: Double,
    val longitude: Double,
    val name: String? = null,      // Nazwa miejscowości
    val country: String? = null    // Kod kraju
) {
    /**
     * Zwraca sformatowaną nazwę lokalizacji
     */
    fun getDisplayName(): String {
        return when {
            name != null && country != null -> "$name, $country"
            name != null -> name
            else -> "%.4f, %.4f".format(latitude, longitude)
        }
    }
    
    /**
     * Zwraca współrzędne jako String
     */
    fun getCoordinatesString(): String = "%.4f, %.4f".format(latitude, longitude)
}
