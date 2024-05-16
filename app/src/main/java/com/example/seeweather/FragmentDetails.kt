package com.example.seeweather

import android.media.Image
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ContentInfoCompat.Flags
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class FragmentDetails(private val date: String, private val lat: Double, private val lon: Double, private val icon: String) : BottomSheetDialogFragment() {

    var weatherForHour = mutableListOf<HorizontalWeatherData>()
    private lateinit var weatherHoursList: MutableList<HorizontalWeatherData>
    private lateinit var recyclerViewHours: RecyclerView
    private lateinit var tvTempDay: TextView
    private lateinit var weatherAdapter: HorizontalWeatherAdapter
    private lateinit var ivWeatherIcon: ImageView
    private lateinit var main: LinearLayout
    private lateinit var tvHumidity: TextView
    private lateinit var tvPressure: TextView
    private lateinit var tvWind: TextView
    private lateinit var tvFeelsLike: TextView
    private lateinit var tvVisibility: TextView
    private lateinit var tvUvIndex: TextView
    private lateinit var tvSelectedDate: TextView
    private lateinit var tvDew: TextView
    private lateinit var tvClouds: TextView
    private lateinit var tvPrecipitation: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        recyclerViewHours = view.findViewById(R.id.recyclerViewHours)
        weatherHoursList = mutableListOf()
        weatherAdapter = HorizontalWeatherAdapter(weatherHoursList)
        recyclerViewHours.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerViewHours.adapter = weatherAdapter
        main = view.findViewById(R.id.main)
        tvWind = view.findViewById(R.id.tvWindSpeed)
        tvPressure = view.findViewById(R.id.tvPressure)
        tvHumidity = view.findViewById(R.id.tvHumidity)
        tvFeelsLike = view.findViewById(R.id.tvFeelsLike)
        tvTempDay = view.findViewById(R.id.tvTempMinMax)
        ivWeatherIcon = view.findViewById(R.id.ivWeatherIcon)
        tvUvIndex = view.findViewById(R.id.tvUvIndex)
        tvVisibility = view.findViewById(R.id.tvVisibility)
        tvSelectedDate = view.findViewById(R.id.tvSelectedDate)
        tvClouds = view.findViewById(R.id.tvCloudCover)
        tvDew = view.findViewById(R.id.tvDewPoint)
        tvPrecipitation = view.findViewById(R.id.tvPrecip)

        tvSelectedDate.text = "Szczegóły dla dnia " + ListUtils.formatDate(date)

        when(icon){
            "clearday" -> main.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_clear)
            "clearnight" -> main.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_clear)
            "rain" -> main.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_drizzle)
            "snow" -> main.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_drizzle)
            "sleet" -> main.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_drizzle)
            "wind" -> main.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_drizzle)
            "fog" -> main.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_drizzle)
            "cloudy" -> main.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_thunder)
            "partlycloudyday" -> main.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_scattered)
            "partlycloudynight" -> main.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_scattered)
        }

        val queue = Volley.newRequestQueue(requireContext())

        val url = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/${lat},${lon}/$date/$date?unitGroup=metric&key=HPK9ZKZYC7G5RKR2YRNFCBP2B&contentType=json"

        val jsonRequest = JsonObjectRequest(url, { response ->

            Log.d("Response", url)

            weatherForHour = mutableListOf()

            val days = response.getJSONArray("days")
            val day = days.getJSONObject(0)

            val temp = day.getDouble("temp")
            val tempMinDay = day.getDouble("tempmin")
            val tempMaxDay = day.getDouble("tempmax")
            val weatherIcon = day.getString("icon")
            val humidity = day.getDouble("humidity")
            val pressure = day.getDouble("pressure")
            val windSpeed = day.getDouble("windspeed")
            val feelsLike = day.getDouble("feelslike")
            val uvindex = day.getDouble("uvindex")
            val visibility = day.getDouble("visibility")
            val precip = day.getDouble("precip")
            val dew = day.getDouble("dew")
            val cloudCover = day.getDouble("cloudcover")

            tvHumidity.text = Math.round(humidity).toString() + "%"
            tvPressure.text = Math.round(pressure).toString() + "hPa"
            tvWind.text = Math.round(windSpeed).toString() + "km/h"
            tvFeelsLike.text = Math.round(feelsLike).toString() + "\u00B0"
            tvUvIndex.text = Math.round(uvindex).toString()
            tvVisibility.text = Math.round(visibility).toString() + "km"
            tvClouds.text = Math.round(cloudCover).toString() + "%"
            tvDew.text = Math.round(dew).toString() + "\u00B0"
            tvPrecipitation.text = Math.round(precip).toString() + "mm"

            ListUtils.loadImageFromIconName(requireContext(),weatherIcon, ivWeatherIcon)

            tvTempDay.text = Math.round(temp).toString() + "\u00B0"

            val hours = day.getJSONArray("hours")

            for (i in 0 until hours.length()) {
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

            weatherHoursList.clear()
            weatherHoursList.addAll(weatherForHour)
            weatherAdapter.notifyDataSetChanged()


        }, { error ->

            error.printStackTrace()

        })

        queue.add(jsonRequest)


    }
}