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
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.checkSelfPermission
import com.example.seeweather.ResultsAdapter.OnItemClickListener
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MyBottomSheetDialogFragment(private val listener: OnLocationClickListener, private val cities: MutableList<City>, private val recyclerView: RecyclerView): BottomSheetDialogFragment(), View.OnClickListener {

    interface OnLocationClickListener {
        fun onLocationClick(lat: Double, lon: Double)
        fun onSpecifiedLocationClick(name: String,lat: Double, lon: Double)
    }

    private val buttonIds = arrayOf(
        R.id.warszawa, R.id.krakow, R.id.wroclaw, R.id.gdansk, R.id.lublin, R.id.newyork, R.id.paryz, R.id.londyn, R.id.pekin, R.id.tokio, R.id.rzym, R.id.moskwa, R.id.singapur, R.id.dubaj, R.id.sydney, R.id.ateny
    )
    private lateinit var layoutPopularCities: LinearLayout
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var resultsAdapter: ResultsAdapter
    private var resultsList = mutableListOf<ResultCity>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        layoutPopularCities = view.findViewById(R.id.layoutPopularCities)

        buttonIds.forEach { id ->
            view.findViewById<Button>(id).setOnClickListener(this)
        }

        val recyclerViewResults = view.findViewById<RecyclerView>(R.id.RecyclerViewResults)
        resultsList = mutableListOf()
        resultsAdapter = ResultsAdapter(resultsList, context as ResultsAdapter.OnItemClickListener)
        recyclerViewResults.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewResults.adapter = resultsAdapter

        val etsearch = view.findViewById<EditText>(R.id.etSearch)
        val btnAnulate = view.findViewById<TextView>(R.id.btnAnulate)
        val btnCurrentLocation = view.findViewById<Button>(R.id.btnCurrentLocation)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        btnCurrentLocation.setOnClickListener {

            btnCurrentLocation.setOnClickListener {
                if (requireContext().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getLastLocation()
                } else {
                    requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
                }
            }
        }

        btnAnulate.setOnClickListener {
            dismiss()
        }

        etsearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (etsearch.text.toString().isNotEmpty()) {
                    layoutPopularCities.visibility = View.GONE
                    recyclerViewResults.visibility = View.VISIBLE
                }else{
                    layoutPopularCities.visibility = View.VISIBLE
                    recyclerViewResults.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        etsearch.setOnKeyListener{v, keyCode, event ->
            when{
                ((keyCode == KeyEvent.KEYCODE_ENTER) && (event.action == KeyEvent.ACTION_DOWN)) ->
                {
                    layoutPopularCities.visibility = View.GONE
                    recyclerViewResults.visibility = View.VISIBLE
                    val city = etsearch.text.toString()
                    requestToServer(city)

                    return@setOnKeyListener true
                }
                else -> false
            }
        }
    }

    private fun requestToServer(city: String) {

        val queue = Volley.newRequestQueue(requireContext())

        val url = "https://api.openweathermap.org/geo/1.0/direct?q=" + city + "&limit=10&appid=${Constants.TOKEN}"

        Log.d("RequestURL", "Request URL: $url")

        val request = JsonArrayRequest(Request.Method.GET, url, null,
            { response ->

                Log.d("Results", url)

                val resultCities = parseJsonResponse(response)

                resultsList.clear()
                resultsList.addAll(resultCities)
                resultsAdapter.notifyDataSetChanged()
            },
            { error ->
                error.printStackTrace()
            }
        )
        queue.add(request)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLastLocation()
                } else {
                }
                return
            }
        }
    }

    private fun getLastLocation() {
        if (requireContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        listener.onLocationClick(latitude, longitude)
                    } else {
                        Toast.makeText(requireContext(), "Nie udało się odnaleźć Twojej lokalizacji", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Nie udało się odnaleźć Twojej lokalizacji", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun parseJsonResponse(jsonArray: JSONArray): List<ResultCity> {
        val resultCities = mutableListOf<ResultCity>()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val name = if (jsonObject.has("local_names")) {
                val localNames = jsonObject.getJSONObject("local_names")
                localNames.optString("pl", jsonObject.optString("name", ""))
            } else {
                jsonObject.optString("name", "")
            }
            val country = jsonObject.optString("country", "")
            val state = jsonObject.optString("state", "")
            val lat = jsonObject.optDouble("lat", 0.0)
            val lon = jsonObject.optDouble("lon", 0.0)
            val resultCity = ResultCity(country, name, state, lat, lon)
            resultCities.add(resultCity)
        }
        return resultCities
    }

    private suspend fun dismissDialog() {
        dismiss()
        delay(250)
        recyclerView.smoothScrollToPosition(0)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.warszawa -> {
                GlobalScope.launch { dismissDialog()
                delay(300)
                    listener.onSpecifiedLocationClick("Warszawa", 52.2296756, 21.0122287)
                }
            }
            R.id.krakow -> {
                listener.onSpecifiedLocationClick("Kraków" ,50.0646507, 19.9449747)
                GlobalScope.launch { dismissDialog() }
            }
            R.id.gdansk -> {
                listener.onSpecifiedLocationClick("Gdańsk", 54.3520515, 18.6466059)
                GlobalScope.launch { dismissDialog() }
            }
            R.id.wroclaw -> {
                listener.onSpecifiedLocationClick("Wrocław", 51.1072002, 17.0385408)
                GlobalScope.launch { dismissDialog() }
            }
            R.id.dubaj -> {
                listener.onSpecifiedLocationClick("Dubaj", 50.5077909, 18.1526107)
                GlobalScope.launch { dismissDialog() }
            }
            R.id.ateny -> {
                listener.onSpecifiedLocationClick("Ateny", 52.0833333, 20.9166667)
                GlobalScope.launch { dismissDialog() }
            }
            R.id.sydney -> {
                listener.onSpecifiedLocationClick("Sydney", -33.8688197, 151.2092955)
                GlobalScope.launch { dismissDialog() }
            }
            R.id.rzym -> {
                listener.onSpecifiedLocationClick("Rzym", 41.9027835, 12.4963655)
                GlobalScope.launch { dismissDialog() }
            }
            R.id.singapur -> {
                listener.onSpecifiedLocationClick("Singapur", 1.352083, 103.819836)
                GlobalScope.launch { dismissDialog() }
            }
            R.id.moskwa -> {
                listener.onSpecifiedLocationClick("Moskwa", 55.755826, 37.6172999)
                GlobalScope.launch { dismissDialog() }
            }
            R.id.tokio -> {
                listener.onSpecifiedLocationClick("Tokio", 35.689487, 139.691706)
                GlobalScope.launch { dismissDialog() }
            }
            R.id.londyn -> {
                listener.onSpecifiedLocationClick("Londyn", 51.5073219, -0.1276474)
                GlobalScope.launch { dismissDialog() }
            }
            R.id.lublin -> {
                listener.onSpecifiedLocationClick("Lublin", 51.25, 22.566667)
                GlobalScope.launch { dismissDialog() }
            }
            R.id.newyork -> {
                listener.onSpecifiedLocationClick("Nowy Jork", 40.712776, -74.005974)
                GlobalScope.launch { dismissDialog() }
            }
            R.id.paryz -> {
                listener.onSpecifiedLocationClick("Paryż", 51.233334, 19.466667)
                GlobalScope.launch { dismissDialog() }
            }
            R.id.pekin -> {
                listener.onSpecifiedLocationClick("Pekin", 39.916668, 116.383331)
                GlobalScope.launch { dismissDialog() }
            }
        }
    }


}