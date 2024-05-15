package com.example.seeweather

import android.content.Context
import android.os.Build
import android.widget.CheckBox
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import java.text.SimpleDateFormat
import java.util.Calendar
import android.view.Window
import android.view.WindowManager
import java.util.Date
import java.util.Locale

object ListUtils {

    fun loadImageFromIconName(context: Context, iconName: String, imageView: ImageView) {
        val resourceId = context.resources.getIdentifier(iconName, "drawable", context.packageName)
        if (resourceId != 0) {
            Glide.with(context)
                .load(resourceId)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView)
        } else {
            Glide.with(context).load(R.drawable.search).into(imageView)
        }
    }

    fun getDayOfWeekPolish(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val currentDate = Calendar.getInstance()
        val date = inputFormat.parse(dateString)

        if (date != null) {
            val parsedDate = Calendar.getInstance()
            parsedDate.time = date

            if (currentDate.get(Calendar.YEAR) == parsedDate.get(Calendar.YEAR) &&
                currentDate.get(Calendar.MONTH) == parsedDate.get(Calendar.MONTH) &&
                currentDate.get(Calendar.DAY_OF_MONTH) == parsedDate.get(Calendar.DAY_OF_MONTH)) {
                return "Dzisiaj"
            } else {
                currentDate.add(Calendar.DAY_OF_YEAR, 1)
                if (currentDate.get(Calendar.YEAR) == parsedDate.get(Calendar.YEAR) &&
                    currentDate.get(Calendar.MONTH) == parsedDate.get(Calendar.MONTH) &&
                    currentDate.get(Calendar.DAY_OF_MONTH) == parsedDate.get(Calendar.DAY_OF_MONTH)) {
                    return "Jutro"
                }
            }
        }

        val outputFormat = SimpleDateFormat("EEEE", Locale("pl", "PL"))
        val dateObj = inputFormat.parse(dateString)
        val dayOfWeek = outputFormat.format(dateObj ?: Date())

        return when (dayOfWeek) {
            "poniedziałek" -> "Pn"
            "wtorek" -> "Wt"
            "środa" -> "Śr"
            "czwartek" -> "Czw"
            "piątek" -> "Pt"
            "sobota" -> "Sb"
            "niedziela" -> "Nd"
            else -> dayOfWeek
        }

    }

    fun formatDate(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("M/d", Locale.getDefault())

        val date = inputFormat.parse(dateString)

        return outputFormat.format(date ?: Date())
    }


}