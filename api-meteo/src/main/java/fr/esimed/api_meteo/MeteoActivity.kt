package fr.esimed.api_meteo

import android.app.ActionBar
import android.app.Dialog
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import fr.esimed.api_meteo.data.Location
import fr.esimed.api_meteo.data.Weather

class MeteoActivity : AppCompatActivity()
{
    inner class QueryWeatherTask(private val service:WeatherService, private val layout: LinearLayout): AsyncTask<Location, Void, Weather>()
    {
        private val dlg = Dialog(this@MeteoActivity)

        fun formatDateFr(date: String):String
        {
            val date_tmp = date.split("-")
            return "${date_tmp[2]}/${date_tmp[1]}/${date_tmp[0]}"
        }

        override fun onPreExecute()
        {
            layout.visibility = View.INVISIBLE
            dlg.window?.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
            dlg.setContentView(R.layout.research)
            dlg.show()
        }

        override fun doInBackground(vararg params: Location?): Weather?
        {
            val query = params[0] ?: return null
            return service.getWeather(query)
        }

        override fun onPostExecute(result: Weather?)
        {
            val date = result?.applicable_date
            val dateFr = formatDateFr(date.toString())
            layout.findViewById<ImageView>(R.id.image_weather).setImageBitmap(result?.bitmap)

            layout.findViewById<TextView>(R.id.text_view_weather_today).text = String.format(getString(R.string.weather_of_today), dateFr, result?.title)
            layout.findViewById<TextView>(R.id.text_view_temp_max).text = String.format(getString(R.string.temperature_max), result?.temp_max?.toFloat())
            layout.findViewById<TextView>(R.id.text_view_temp_min).text = String.format(getString(R.string.temperature_min), result?.temp_min?.toFloat())
            layout.findViewById<TextView>(R.id.text_view_wind).text = String.format(getString(R.string.wind_speed), result?.wind_speed?.toFloat())
            layout.findViewById<TextView>(R.id.text_view_humidity).text = String.format(getString(R.string.humidity), result?.humidity?.toFloat()) + "%"
            layout.findViewById<TextView>(R.id.text_view_previsibilite).text = String.format(getString(R.string.previsibilite), result?.previsibilite?.toFloat()) + "%"

            layout.visibility = View.VISIBLE
            dlg.dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meteo)

        val svc = WeatherService()
        val location = intent.getSerializableExtra("location") as Location

        val layout = findViewById<LinearLayout>(R.id.layout_weather)
        QueryWeatherTask(svc, layout).execute(location)
    }
}