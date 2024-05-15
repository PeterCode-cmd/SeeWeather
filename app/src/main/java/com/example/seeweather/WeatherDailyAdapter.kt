package com.example.seeweather

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.seeweather.ListUtils.formatDate
import com.example.seeweather.ListUtils.getDayOfWeekPolish
import com.example.seeweather.ListUtils.loadImageFromIconName

class WeatherDailyAdapter(private val weather: List<WeatherDailyData>) : RecyclerView.Adapter<WeatherDailyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_weather_daily2, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return weather.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val weather = weather[position]

        holder.date.text = formatDate(weather.date)
        holder.dateName.text = getDayOfWeekPolish(weather.date)
        holder.temp.text = Math.round(weather.temperatureLow).toString() + "\u00B0" + " / " + Math.round(weather.temperatureHigh).toString() + "\u00B0"

        weather.icon.let {
            loadImageFromIconName(holder.icon.context,
                it, holder.icon)
        }

        holder.itemView.setOnClickListener {

            FragmentDetails(weather.date, Constants.lat, Constants.lon).show((holder.itemView.context as AppCompatActivity).supportFragmentManager, "details")

        }

    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val date = itemView.findViewById<TextView>(R.id.tvDate)
        val temp = itemView.findViewById<TextView>(R.id.tvTemp)
        val icon = itemView.findViewById<ImageView>(R.id.ivIcon)
        val dateName = itemView.findViewById<TextView>(R.id.tvDateName)


    }


}