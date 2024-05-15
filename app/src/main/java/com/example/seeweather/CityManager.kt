package com.example.seeweather

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object CityManager {
    private const val PREFS_NAME = "CityPrefs"
    private const val KEY_CITIES = "savedCities"

    fun saveCities(context: Context, cities: List<City>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val gson = Gson()
        val json = gson.toJson(cities)
        editor.putString(KEY_CITIES, json)
        editor.apply()
    }

    fun getCities(context: Context): MutableList<City> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString(KEY_CITIES, null)
        return if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<List<City>>() {}.type
            gson.fromJson(json, type) ?: mutableListOf()
        } else {
            mutableListOf()
        }
    }
}

