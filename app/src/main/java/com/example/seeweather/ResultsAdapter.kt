package com.example.seeweather

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.seeweather.R

class ResultsAdapter(private val cities: List<ResultCity>, private val listener: OnItemClickListener) : RecyclerView.Adapter<ResultsAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(name: String, lat: Double, lon: Double)
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val tvCity = itemView.findViewById<TextView>(R.id.tvCityText)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_result_city, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cities.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val city = cities[position]

        holder.tvCity.text = city.name + ", " + city.state + ", " + city.country

        holder.itemView.setOnClickListener {

            listener.onItemClick(city.name, city.lat, city.lon)

        }

    }
}