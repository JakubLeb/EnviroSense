package com.example.envirosense.data.remote.dto

import com.example.envirosense.domain.model.AirQuality
import com.google.gson.annotations.SerializedName

/**
 * DTO dla odpowiedzi z OpenWeatherMap Air Pollution API
 */
data class AirQualityResponse(
    @SerializedName("coord")
    val coordinates: AirCoordinates,
    
    @SerializedName("list")
    val list: List<AirQualityData>
) {
    /**
     * Konwertuje DTO do modelu domenowego AirQuality
     */
    fun toAirQuality(): AirQuality {
        val data = list.firstOrNull() ?: return AirQuality(
            aqi = 1,
            pm25 = 0.0,
            pm10 = 0.0,
            no2 = 0.0,
            o3 = 0.0,
            co = 0.0,
            so2 = 0.0,
            nh3 = 0.0
        )
        
        return AirQuality(
            aqi = data.main.aqi,
            pm25 = data.components.pm25,
            pm10 = data.components.pm10,
            no2 = data.components.no2,
            o3 = data.components.o3,
            co = data.components.co,
            so2 = data.components.so2,
            nh3 = data.components.nh3
        )
    }
}

data class AirCoordinates(
    @SerializedName("lat")
    val latitude: Double,
    
    @SerializedName("lon")
    val longitude: Double
)

data class AirQualityData(
    @SerializedName("main")
    val main: AirMain,
    
    @SerializedName("components")
    val components: AirComponents,
    
    @SerializedName("dt")
    val timestamp: Long
)

data class AirMain(
    @SerializedName("aqi")
    val aqi: Int  // 1 = Good, 2 = Fair, 3 = Moderate, 4 = Poor, 5 = Very Poor
)

data class AirComponents(
    @SerializedName("co")
    val co: Double,         // Carbon monoxide, μg/m³
    
    @SerializedName("no")
    val no: Double,         // Nitrogen monoxide, μg/m³
    
    @SerializedName("no2")
    val no2: Double,        // Nitrogen dioxide, μg/m³
    
    @SerializedName("o3")
    val o3: Double,         // Ozone, μg/m³
    
    @SerializedName("so2")
    val so2: Double,        // Sulphur dioxide, μg/m³
    
    @SerializedName("pm2_5")
    val pm25: Double,       // Fine particles matter, μg/m³
    
    @SerializedName("pm10")
    val pm10: Double,       // Coarse particulate matter, μg/m³
    
    @SerializedName("nh3")
    val nh3: Double         // Ammonia, μg/m³
)
