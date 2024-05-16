package com.example.seeweather

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.seeweather.R
import com.example.seeweather.databinding.BottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.json.JSONArray
import org.json.JSONObject
import android.Manifest
import android.content.Context.LOCATION_SERVICE
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.checkSelfPermission
import com.example.seeweather.ResultsAdapter.OnItemClickListener
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.w3c.dom.Text

class BottomSheetWeather(private val lat: Double, private val lon: Double, private val id: Int): BottomSheetDialogFragment() {

    var weatherForDay = mutableListOf<WeatherDailyData>()


    private lateinit var main: LinearLayout
    private lateinit var weatherDailyList: MutableList<WeatherDailyData>
    private lateinit var recyclerViewDaily: RecyclerView
    private lateinit var weatherDailyAdapter: WeatherDailyAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_weather, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewDaily = view.findViewById(R.id.recyclerViewDays)
        main = view.findViewById(R.id.bottomMain)

        when (id) {
            in 200..232 -> {
                main.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_thunder)
            }
            in 300..321 -> {
                main.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_drizzle)
            }

            in 500..531 -> {
                main.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_thunder)
            }

            in 600..622 -> {
                main.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_thunder)
            }

            in 701..781 -> {
                main.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_drizzle)
            }

            800 -> {
                main.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_clear)
            }

            801 -> {
                main.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_clear)
            }

            802 -> {
                main.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_scattered)
            }

            803 -> {
                main.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_scattered)
            }

            804 -> {
                main.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_thunder)
            }

            else -> {
                main.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_scattered)
            }
        }


        weatherDailyList = mutableListOf()
        weatherDailyAdapter = WeatherDailyAdapter(weatherDailyList)
        recyclerViewDaily.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerViewDaily.adapter = weatherDailyAdapter

        val queue2 = Volley.newRequestQueue(requireContext())

        val url2 = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/${lat},${lon}/next15days?unitGroup=metric&include=days&key=HPK9ZKZYC7G5RKR2YRNFCBP2B&contentType=json"

        val jsonRequest2 = JsonObjectRequest(url2, { response ->

            Log.d("Response", url2)

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


}