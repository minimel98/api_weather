package fr.esimed.api_meteo

import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.JsonReader
import android.view.View
import android.widget.ListView
import fr.esimed.api_meteo.data.Location
import fr.esimed.api_meteo.data.Weather
import java.io.IOException
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection

class WeatherService()
{
    private val apiUrl = "https://www.metaweather.com"
    private val queryUrl = "$apiUrl/api/location/search/?query=%s"

    fun getLocations(query: String): List<Location>
    {
        val url = URL(String.format(queryUrl, query))
        var connection: HttpsURLConnection? = null

        try
        {
            connection = url.openConnection() as HttpsURLConnection
            connection.connect()

            val code = connection.responseCode

            if (code != HttpsURLConnection.HTTP_OK)
            {
                return emptyList()
            }
            val inputStream = connection.inputStream ?: return emptyList()
            val reader = JsonReader(inputStream.bufferedReader())
            val listLocation = mutableListOf<Location>()

            reader.beginArray()
            while (reader.hasNext())
            {
                val location = Location()
                reader.beginObject()
                while (reader.hasNext())
                {
                    when(reader.nextName())
                    {
                        "title" -> location.title = reader.nextString()
                        "woeid" -> location.woeid = reader.nextInt()
                        else -> reader.skipValue()
                    }
                }
                reader.endObject()
                listLocation.add(location)
            }
            reader.endArray()
            return listLocation
        }
        catch (e : IOException)
        {
            return emptyList()
        }
        finally
        {
            connection?.disconnect()
        }
    }

    fun getWeather(location: Location): Weather?
    {
        val queryWoeid = location.woeid
        val queryWeatherUrl = "$apiUrl/api/location/$queryWoeid/"

        val urlWeather = URL(String.format(queryWeatherUrl))
        var connect: HttpsURLConnection? = null

        try
        {
            connect = urlWeather.openConnection() as HttpsURLConnection
            connect.connect()
            val code = connect.responseCode

            if (code != HttpsURLConnection.HTTP_OK)
            {
                return null
            }
            val inputStream = connect.inputStream ?: return null
            val reader = JsonReader(inputStream.bufferedReader())
            val list = mutableListOf<Weather>()

            reader.beginObject()
            while (reader.hasNext())
            {
                if (reader.nextName() == "consolidated_weather")
                {
                    reader.beginArray()
                    while (reader.hasNext())
                    {
                        val weather = Weather(title = location.title)
                        reader.beginObject()
                        while (reader.hasNext())
                        {
                            when(reader.nextName())
                            {
                                "applicable_date" -> weather.applicable_date = reader.nextString().toString()
                                "weather_state_abbr" -> weather.weather_state_abbr = reader.nextString()
                                "max_temp" -> weather.temp_max = reader.nextDouble()
                                "min_temp" -> weather.temp_min = reader.nextDouble()
                                "wind_speed" -> weather.wind_speed = reader.nextDouble()
                                "humidity" -> weather.humidity = reader.nextDouble()
                                "predictability" -> weather.previsibilite = reader.nextInt()
                                else -> reader.skipValue()
                            }
                        }
                        reader.endObject()
                        weather.bitmap = BitmapFactory.decodeStream(URL("https://www.metaweather.com/static/img/weather/png/64/${weather.weather_state_abbr}.png").openConnection().getInputStream())
                        list.add(weather)
                    }
                    reader.endArray()
                }
                else
                {
                    reader.skipValue()
                }
            }
            reader.endObject()
            val weatherTomorrow = list[1]
            return  weatherTomorrow
        }
        catch (e : IOException)
        {
            return null
        }
        finally
        {
            connect?.disconnect()
        }
    }
}