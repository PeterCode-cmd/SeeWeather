package com.example.seeweather

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.example.seeweather.databinding.ActivityWeatherBinding

class WeatherActivity : AppCompatActivity() {

    var weatherForDay = mutableListOf<WeatherDailyData>()
    var weatherForHour = mutableListOf<HorizontalWeatherData>()
    private lateinit var weatherHoursList: MutableList<HorizontalWeatherData>
    private lateinit var recyclerViewHours: RecyclerView
    private lateinit var weatherAdapter: HorizontalWeatherAdapter

    private lateinit var weatherDailyList: MutableList<WeatherDailyData>
    private lateinit var recyclerViewDaily: RecyclerView
    private lateinit var weatherDailyAdapter: WeatherDailyAdapter


    private lateinit var binding: ActivityWeatherBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerViewHours = findViewById(R.id.recyclerViewHours)
        recyclerViewDaily = findViewById(R.id.recyclerViewDays)

        weatherHoursList = mutableListOf()
        weatherAdapter = HorizontalWeatherAdapter(weatherHoursList)
        recyclerViewHours.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewHours.adapter = weatherAdapter

        weatherDailyList = mutableListOf()
        weatherDailyAdapter = WeatherDailyAdapter(weatherDailyList)
        recyclerViewDaily.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerViewDaily.adapter = weatherDailyAdapter
        recyclerViewDaily.isNestedScrollingEnabled = false

        binding.seekBar.isEnabled = false

        val name = intent.getStringExtra("name")
        val lat = intent.getDoubleExtra("lat", 0.0)
        val lon = intent.getDoubleExtra("lon", 0.0)
        val id = intent.getIntExtra("id", 0)
        val temp = intent.getDoubleExtra("temp", 0.0)
        val description = intent.getStringExtra("description")

        Constants.lat = lat
        Constants.lon = lon

        binding.tvTempNow.text = Math.round(temp).toString() + "\u00B0"
        binding.tvDesc.text = description
        binding.tvCityName.text = name

        binding.ivBackHouse.setOnClickListener {
            finish()
        }

        binding.btnWeatherMore.setOnClickListener {

            BottomSheetWeather(lat, lon, id).show(supportFragmentManager,"ShowMore")

        }

        binding.apply {

            when (id) {
                in 200..232 -> {
                    main.background = ContextCompat.getDrawable(this@WeatherActivity, R.drawable.bg_thunder)
                    changeStatusBarColor(R.color.thunderstorm)

                }
                in 300..321 -> {
                    main.background = ContextCompat.getDrawable(this@WeatherActivity, R.drawable.bg_drizzle)
                    changeStatusBarColor(R.color.drizzle)
                }

                in 500..531 -> {
                    main.background = ContextCompat.getDrawable(this@WeatherActivity, R.drawable.bg_thunder)
                    changeStatusBarColor(R.color.thunderstorm)
                }

                in 600..622 -> {
                    main.background = ContextCompat.getDrawable(this@WeatherActivity, R.drawable.bg_thunder)
                    changeStatusBarColor(R.color.thunderstorm)
                }

                in 701..781 -> {
                    main.background = ContextCompat.getDrawable(this@WeatherActivity, R.drawable.bg_drizzle)
                    changeStatusBarColor(R.color.drizzle)
                }

                800 -> {
                    main.background = ContextCompat.getDrawable(this@WeatherActivity, R.drawable.bg_clear)
                    changeStatusBarColor(R.color.clear)
                }

                801 -> {
                    main.background = ContextCompat.getDrawable(this@WeatherActivity, R.drawable.bg_clear)
                    changeStatusBarColor(R.color.clear)
                }

                802 -> {
                    main.background = ContextCompat.getDrawable(this@WeatherActivity, R.drawable.bg_scattered)
                    changeStatusBarColor(R.color.scatteredclouds)
                }

                803 -> {
                    main.background = ContextCompat.getDrawable(this@WeatherActivity, R.drawable.bg_scattered)
                    changeStatusBarColor(R.color.scatteredclouds)
                }

                804 -> {
                    main.background = ContextCompat.getDrawable(this@WeatherActivity, R.drawable.bg_thunder)
                    changeStatusBarColor(R.color.thunderstorm)
                }

                else -> {
                    main.background = ContextCompat.getDrawable(this@WeatherActivity, R.drawable.bg_scattered)
                    changeStatusBarColor(R.color.scatteredclouds)
                }
            }


        }

        val queue = Volley.newRequestQueue(this@WeatherActivity)

        val url = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/${lat},${lon}/next24hours?unitGroup=metric&include=current%2Chours&key=HPK9ZKZYC7G5RKR2YRNFCBP2B&contentType=json"

        val jsonRequest = JsonObjectRequest(url, { response ->

            Log.d("Response", url)

            weatherForHour = mutableListOf()

            val currentConditions = response.getJSONObject("currentConditions")

            val currentHour = currentConditions.getString("datetime")
            val uvIndex = currentConditions.getInt("uvindex")
            val feelsLike = currentConditions.getDouble("feelslike")
            val windSpeed = currentConditions.getDouble("windspeed")
            val humidity = currentConditions.getInt("humidity")
            val visibility = currentConditions.getDouble("visibility")
            val pressure = currentConditions.getInt("pressure")
            val sunrise = currentConditions.getString("sunrise")
            val sunset = currentConditions.getString("sunset")

            binding.tvUvIndex.text = uvIndex.toString()
            binding.tvFeelsLike.text = Math.round(feelsLike).toString() + "\u00B0"
            binding.tvWindSpeed.text = Math.round(windSpeed).toString() + " km/h"
            binding.tvHumidity.text = humidity.toString() + " %"
            binding.tvVisibility.text = visibility.toString() + " km"
            binding.tvPressure.text = pressure.toString() + " hPa"
            binding.tvSunset.text = sunset.take(5)
            binding.tvSunrise.text = sunrise.take(5)


            val sunsetInt = sunset.replace(":", "")
            val sunriseInt = sunrise.replace(":", "").substring(1,6)

            val currentHourInt: Int

            if(currentHour[0] == '0'){
                currentHourInt = currentHour.replace(":", "").substring(1,6).toInt()
            }else{
                currentHourInt = currentHour.replace(":", "").toInt()
            }

            binding.seekBar.min = sunriseInt.toInt()
            binding.seekBar.max = sunsetInt.toInt()

            binding.seekBar.progress = currentHourInt


            val hour = currentHour.take(2)

            val days = response.getJSONArray("days")
            val day = days.getJSONObject(0)
            val tempMax = day.getDouble("tempmax")
            val tempMin = day.getDouble("tempmin")

            binding.tvDesc.text = description + " " + Math.round(tempMin).toString() + "\u00B0" + " / " + Math.round(tempMax).toString() + "\u00B0"

            val day2 = days.getJSONObject(1)

            val hours = day.getJSONArray("hours")
            val hours2 = day2.getJSONArray("hours")

            for (i in hour.toInt() + 1 until hours.length()) {
                val hourWeather = hours.getJSONObject(i)

                val hourIcon = hourWeather.getString("icon")

                val hourIconCut = hourIcon.replace("-", "")

                val weather = HorizontalWeatherData(
                    hourWeather.getString("datetime"),
                    hourIconCut,
                    hourWeather.getDouble("temp")
                )

                weatherForHour.add(weather)


            }

            for (i in 0 until hour.toInt() + 1) {
                val hourWeather = hours2.getJSONObject(i)

                val hourIcon = hourWeather.getString("icon")

                val hourIconCut = hourIcon.replace("-", "")

                val weather = HorizontalWeatherData(
                    hourWeather.getString("datetime"),
                    hourIconCut,
                    hourWeather.getDouble("temp")
                )

                weatherForHour.add(weather)

            }

            weatherHoursList.clear()
            weatherHoursList.addAll(weatherForHour)
            weatherAdapter.notifyDataSetChanged()


        }, { error ->

            error.printStackTrace()

        })

        queue.add(jsonRequest)

        val queue2 = Volley.newRequestQueue(this@WeatherActivity)

        val url2 = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/${lat},${lon}/next7days?unitGroup=metric&include=days&key=HPK9ZKZYC7G5RKR2YRNFCBP2B&contentType=json"

        val jsonRequest2 = JsonObjectRequest(url2, { response ->

            weatherForDay = mutableListOf()

            val days = response.getJSONArray("days")
            for(i in 0 until days.length() - 1){

                val day = days.getJSONObject(i)
                val tempMax = day.getDouble("tempmax")
                val tempMin = day.getDouble("tempmin")
                val date = day.getString("datetime")
                val icon = day.getString("icon")

                val weatherIcon = icon.replace("-", "")

                val weather = WeatherDailyData(
                    date,
                    weatherIcon,
                    tempMax,
                    tempMin
                )

                weatherForDay.add(weather)

            }

            weatherDailyList.clear()
            weatherDailyList.addAll(weatherForDay)
            weatherDailyAdapter.notifyDataSetChanged()

        }, { error ->

                error.printStackTrace()

            })

        queue2.add(jsonRequest2)

    }

    private fun changeStatusBarColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, color)
        }
    }


}