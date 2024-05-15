package com.example.seeweather

import android.graphics.Bitmap
import android.media.session.MediaSession.Token
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions

import com.example.seeweather.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior

import org.json.JSONObject

class MainActivity : AppCompatActivity(), ResultsAdapter.OnItemClickListener, MyBottomSheetDialogFragment.OnLocationClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var citiesAdapter: CitiesAdapter
    private var cities = mutableListOf<City>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        }

        cities = CityManager.getCities(this)
        updateWeatherDataForCities()

        citiesAdapter = CitiesAdapter(cities)
        binding.RecyclerViewCities.layoutManager = LinearLayoutManager(this)
        binding.RecyclerViewCities.adapter = citiesAdapter

        /*binding.ivBackButton.setOnClickListener {

        }*/

        binding.topCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {

                citiesAdapter.selectAllItems()
                citiesAdapter.notifyDataSetChanged()

            }else {

                citiesAdapter.deselectAllItems()
                citiesAdapter.notifyDataSetChanged()


            }
        }

        binding.ivCancelButton.setOnClickListener {

            if(Constants.isButtonPressed){

                Constants.isButtonPressed = !Constants.isButtonPressed
                binding.tvZarzadzaj.text = "Dodaj miasto"
                binding.ivCancelButton.visibility = View.GONE
                binding.ivBackButton.visibility = View.VISIBLE
                binding.btnEdit.visibility = View.VISIBLE
                binding.topCheckBox.visibility = View.GONE
                binding.btnAddCity.visibility = View.VISIBLE

                citiesAdapter.notifyDataSetChanged()

            }


        }

        binding.btnEdit.setOnClickListener {

            Constants.isButtonPressed = !Constants.isButtonPressed
            if(Constants.isButtonPressed){
                binding.tvZarzadzaj.text = "Wybierz elementy"
                binding.ivCancelButton.visibility = View.VISIBLE
                binding.ivBackButton.visibility = View.GONE
                binding.btnEdit.visibility = View.GONE
                binding.topCheckBox.visibility = View.VISIBLE
                binding.btnAddCity.visibility = View.GONE


            }else {
                binding.tvZarzadzaj.text = "Dodaj miasto"
                binding.ivCancelButton.visibility = View.GONE
                binding.ivBackButton.visibility = View.VISIBLE
                binding.btnEdit.visibility = View.GONE
                binding.btnAddCity.visibility = View.VISIBLE

            }

            citiesAdapter.notifyDataSetChanged()

        }

        binding.btnDelete.setOnClickListener {
            citiesAdapter.removeSelectedItems()
            citiesAdapter.notifyDataSetChanged()
        }



        binding.btnAddCity.setOnClickListener {
            MyBottomSheetDialogFragment(this,cities, binding.RecyclerViewCities).show(supportFragmentManager, "AddCity")
        }

    }

    private fun parseResponse(jsonObject: JSONObject): WeatherData? {
        try {
            val weatherArray = jsonObject.getJSONArray("weather")
            val weatherObject = weatherArray.getJSONObject(0)
            val description = weatherObject.optString("description", "")
            val weatherId = weatherObject.optInt("id", 0)
            val mainObject = jsonObject.getJSONObject("main")
            val temp = mainObject.optDouble("temp", 0.0)

            return WeatherData(description, temp, weatherId)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun getWeather(name: String, lat: Double, lon: Double) {
        val queue = Volley.newRequestQueue(this)
        val url = "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&appid=${Constants.TOKEN}&units=metric&lang=pl"

        Log.d("RequestURL", "Request URL: $url")

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                val resultCities = parseResponse(response)
                val newCity = City(resultCities!!.id,resultCities!!.temp, resultCities!!.description, name, lat, lon)

                val existingCityIndex = cities.indexOfFirst { it.name == name }
                if (existingCityIndex != -1) {
                    cities[existingCityIndex] = newCity
                } else {
                    cities.add(0, newCity)
                }
                citiesAdapter.notifyDataSetChanged()
            },
            { error ->
                error.printStackTrace()
            }
        )
        queue.add(request)
    }

    private fun updateWeatherDataForCities() {
        for (city in cities) {
            getWeather(city.name, city.lat, city.lon)
        }
    }

    override fun onPause() {
        super.onPause()
        CityManager.saveCities(this, cities.toMutableList())
    }

    override fun onItemClick(name: String, lat: Double, lon: Double) {
        getWeather(name,lat, lon)
    }

    override fun onLocationClick(lat: Double, lon: Double) {

        Log.d("Location", "Latitude: $lat, Longitude: $lon")
        getWeather("", lat, lon)

    }

    override fun onSpecifiedLocationClick(name: String, lat: Double, lon: Double) {

        getWeather(name,lat,lon)

    }
}