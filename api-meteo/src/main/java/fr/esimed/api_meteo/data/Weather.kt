package fr.esimed.api_meteo.data

import android.graphics.Bitmap
import java.util.*

data class Weather(var title: String = "",
                   var applicable_date: String = "",
                   var weather_state_abbr: String = "",
                   var temp_max: Double = 0.0,
                   var temp_min: Double = 0.0,
                   var wind_speed: Double = 0.0,
                   var humidity: Double = 0.0,
                   var previsibilite: Int = 0,
                   var bitmap: Bitmap ?= null)
{
    override fun toString(): String
    {
        return String.format("%s", weather_state_abbr, applicable_date)
    }
}