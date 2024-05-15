package com.example.seeweather

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley


class FragmentDetails(private val date: String, private val lat: Double, private val lon: Double) : DialogFragment() {

    var weatherForHour = mutableListOf<HorizontalWeatherData>()
    private lateinit var weatherHoursList: MutableList<HorizontalWeatherData>
    private lateinit var recyclerViewHours: RecyclerView
    private lateinit var weatherAdapter: HorizontalWeatherAdapter


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

        val queue = Volley.newRequestQueue(requireContext())

        val url = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/${lat},${lon}/$date/$date?unitGroup=metric&key=HPK9ZKZYC7G5RKR2YRNFCBP2B&contentType=json"

        val jsonRequest = JsonObjectRequest(url, { response ->

            Log.d("Response", url)

            weatherForHour = mutableListOf()

           /* val currentConditions = response.getJSONObject("currentConditions")

            val currentHour = currentConditions.getString("datetime")
            val uvIndex = currentConditions.getInt("uvindex")
            val feelsLike = currentConditions.getDouble("feelslike")
            val windSpeed = currentConditions.getDouble("windspeed")
            val humidity = currentConditions.getInt("humidity")
            val visibility = currentConditions.getDouble("visibility")
            val pressure = currentConditions.getInt("pressure")
            val sunrise = currentConditions.getString("sunrise")
            val sunset = currentConditions.getString("sunset")*/

            val days = response.getJSONArray("days")
            val day = days.getJSONObject(0)

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