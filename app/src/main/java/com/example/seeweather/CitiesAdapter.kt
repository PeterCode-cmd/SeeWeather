package com.example.seeweather

import android.content.Intent
import render.animations.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.seeweather.R
import com.example.seeweather.WeatherActivity
import com.realpacific.clickshrinkeffect.applyClickShrink

class CitiesAdapter(private val cities: MutableList<City>) : RecyclerView.Adapter<CitiesAdapter.ViewHolder>() {

    private val selectedItems: MutableSet<Int> = HashSet()

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {


        val tvCity = itemView.findViewById<TextView>(R.id.tvCity)
        val tvTemp = itemView.findViewById<TextView>(R.id.tvTemp)
        val tvDescription = itemView.findViewById<TextView>(R.id.tvDescription)
        val cardView = itemView.findViewById<androidx.cardview.widget.CardView>(R.id.cardView)
        val checkBox = itemView.findViewById<CheckBox>(R.id.checkBox)
        val layoutTemp = itemView.findViewById<LinearLayout>(R.id.layoutTemp)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_city, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cities.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val city = cities[position]

        holder.tvCity.text = city.name
        holder.tvTemp.text = Math.round(city.temp).toString() + "\u00B0"
        holder.tvDescription.isSelected = true
        holder.tvDescription.text = city.description

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedItems.add(position)
            } else {
                selectedItems.remove(position)
            }
        }

        if(selectedItems.contains(position)){
            holder.checkBox.isChecked = true
        }

        if(selectedItems.count() == cities.count()){

            holder.checkBox.isChecked = true

        }else{

            holder.checkBox.isChecked = false
        }

        if(Constants.isButtonPressed){

            holder.checkBox.visibility = View.VISIBLE
            holder.layoutTemp.visibility = View.GONE
            holder.tvDescription.visibility = View.GONE
            holder.itemView.isClickable = false

        }else{
            holder.checkBox.visibility = View.GONE
            holder.layoutTemp.visibility = View.VISIBLE
            holder.tvDescription.visibility = View.VISIBLE
            holder.itemView.isClickable = true
        }


        val id = city.id
        when(id){
            in 200..232 -> holder.cardView.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_thunder)
            in 300..321 -> holder.cardView.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_drizzle)
            in 500..531 -> holder.cardView.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_thunder)
            in 600..622 -> holder.cardView.setCardBackgroundColor(holder.itemView.context.getColor(R.color.snow))
            in 701..781 -> holder.cardView.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_drizzle)
            800 -> holder.cardView.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_clear)
            801 -> holder.cardView.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_clear)
            802 -> holder.cardView.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_scattered)
            803 -> holder.cardView.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_scattered)
            804 -> holder.cardView.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_thunder)
            else -> holder.cardView.setCardBackgroundColor(holder.itemView.context.getColor(R.color.bgBottom))
        }

        holder.itemView.setOnClickListener {

            val intent = Intent(holder.itemView.context, WeatherActivity::class.java).apply {
                putExtra("name", city.name)
                putExtra("temp", city.temp)
                putExtra("description", city.description)
                putExtra("lat", city.lat)
                putExtra("lon", city.lon)
                putExtra("id", city.id)
            }
            holder.itemView.context.startActivity(intent)
            Animatoo.animateShrink(holder.itemView.context)
        }

    }

    fun deselectAllItems(){

        if(selectedItems.count() == cities.count()){
            selectedItems.clear()
        }
        notifyDataSetChanged()
    }

    fun selectAllItems(){
        cities.forEachIndexed { index, city ->
            selectedItems.add(index)
        }
        notifyDataSetChanged()
    }

    fun removeSelectedItems() {
        selectedItems.forEach { position ->
            cities.removeAt(position)
            notifyItemRemoved(position)
        }
        notifyDataSetChanged()
    }

}