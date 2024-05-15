package com.example.seeweather

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.seeweather.ListUtils.loadImageFromIconName
import com.example.seeweather.R

class HorizontalWeatherAdapter(private val weather: List<HorizontalWeatherData>) : RecyclerView.Adapter<HorizontalWeatherAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_weatherhorizontal, parent, false)
        return ViewHolder(view)
    }


    override fun getItemCount(): Int {
        return weather.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val weather = weather[position]

        holder.time.text = weather.time.take(5)
        holder.temp.text = Math.round(weather.temp).toString() + "\u00B0"

        weather.icon.let {
            loadImageFromIconName(holder.icon.context,
                it, holder.icon)
        }

        Log.d("icon horizontally", "icon: $weather.icon")


    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val time = itemView.findViewById<TextView>(R.id.text_time)
        val icon = itemView.findViewById<ImageView>(R.id.image_weather)
        val temp = itemView.findViewById<TextView>(R.id.text_temperature)

    }

}