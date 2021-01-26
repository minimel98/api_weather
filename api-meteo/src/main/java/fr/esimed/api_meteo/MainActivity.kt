package fr.esimed.api_meteo

import android.app.ActionBar
import android.app.Dialog
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import fr.esimed.api_meteo.data.Location

class MainActivity : AppCompatActivity()
{
    inner class QueryLocationTask(private val service:WeatherService, private val listView: ListView): AsyncTask<String, Void, List<Location>>()
    {
        private val dlg = Dialog(this@MainActivity)

        override fun onPreExecute()
        {
            listView.visibility = View.INVISIBLE
            dlg.window?.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
            dlg.setContentView(R.layout.research)
            dlg.show()
        }

        override fun doInBackground(vararg params: String?): List<Location>
        {
            val query = params[0] ?: return emptyList()
            return service.getLocations(query)
        }

        override fun onPostExecute(result: List<Location>?)
        {
            listView.adapter = ArrayAdapter<Location>(applicationContext, android.R.layout.simple_list_item_1, android.R.id.text1, result!!)
            listView.visibility = View.VISIBLE
            dlg.dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val list = findViewById<ListView>(R.id.result_search_list)

        val svc = WeatherService()
        findViewById<ImageButton>(R.id.btn_search).setOnClickListener {
            val editQuery = findViewById<EditText>(R.id.edit_text_search).text.toString()
            QueryLocationTask(svc, list).execute(editQuery)
        }

        list.setOnItemClickListener { parent, view, position, id ->
            val location = list.getItemAtPosition(position) as Location
            intent = Intent(this,MeteoActivity::class.java)
            intent.putExtra("location", location)
            this.startActivity(intent)
        }
    }
}