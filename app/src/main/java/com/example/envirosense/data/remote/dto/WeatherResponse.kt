package com.example.envirosense.data.remote.dto

import com.example.envirosense.domain.model.Location
import com.example.envirosense.domain.model.Weather
import com.google.gson.annotations.SerializedName

/**
 * DTO dla odpowiedzi z OpenWeatherMap Weather API
 */
data class WeatherResponse(
    @SerializedName("coord")
    val coordinates: Coordinates,
    
    @SerializedName("weather")
    val weather: List<WeatherInfo>,
    
    @SerializedName("main")
    val main: MainInfo,
    
    @SerializedName("visibility")
    val visibility: Int,
    
    @SerializedName("wind")
    val wind: WindInfo,
    
    @SerializedName("clouds")
    val clouds: CloudsInfo,
    
    @SerializedName("sys")
    val sys: SysInfo,
    
    @SerializedName("name")
    val cityName: String,
    
    @SerializedName("cod")
    val code: Int
) {
    /**
     * Konwertuje DTO do modelu domenowego Weather
     */
    fun toWeather(): Weather {
        val weatherInfo = weather.firstOrNull()
        return Weather(
            temperature = main.temperature,
            feelsLike = main.feelsLike,
            humidity = main.humidity,
            pressure = main.pressure,
            windSpeed = wind.speed,
            windDirection = wind.direction ?: 0,
            description = weatherInfo?.description?.replaceFirstChar { it.uppercase() } ?: "",
            icon = weatherInfo?.icon ?: "01d",
            clouds = clouds.all,
            visibility = visibility,
            sunrise = sys.sunrise,
            sunset = sys.sunset
        )
    }
    
    /**
     * Konwertuje DTO do modelu Location
     */
    fun toLocation(): Location {
        return Location(
            latitude = coordinates.latitude,
            longitude = coordinates.longitude,
            name = cityName,
            country = sys.country
        )
    }
}

data class Coordinates(
    @SerializedName("lat")
    val latitude: Double,
    
    @SerializedName("lon")
    val longitude: Double
)

data class WeatherInfo(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("main")
    val main: String,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("icon")
    val icon: String
)

data class MainInfo(
    @SerializedName("temp")
    val temperature: Double,
    
    @SerializedName("feels_like")
    val feelsLike: Double,
    
    @SerializedName("pressure")
    val pressure: Int,
    
    @SerializedName("humidity")
    val humidity: Int,
    
    @SerializedName("temp_min")
    val tempMin: Double,
    
    @SerializedName("temp_max")
    val tempMax: Double
)

data class WindInfo(
    @SerializedName("speed")
    val speed: Double,
    
    @SerializedName("deg")
    val direction: Int?
)

data class CloudsInfo(
    @SerializedName("all")
    val all: Int
)

data class SysInfo(
    @SerializedName("country")
    val country: String,
    
    @SerializedName("sunrise")
    val sunrise: Long,
    
    @SerializedName("sunset")
    val sunset: Long
)
