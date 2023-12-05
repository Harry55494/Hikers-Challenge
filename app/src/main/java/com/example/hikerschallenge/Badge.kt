package com.example.hikerschallenge

import android.content.Context
import android.content.Intent
import android.util.Log
import java.io.Serializable
import java.util.Calendar

data class Badge(var name: String, var location: String = "Unknown", var location_2: String = "Unknown", var dateCollected: Calendar? = null, var announceCreation: Boolean = true): Serializable, Comparable<Badge> {

    private val tag = "BadgeClass"

    init {
        if (announceCreation) {
            Log.i(tag, "'$name' created")
            Log.i(tag, "dateCollected: $dateCollected")
        }
    }

    override fun toString(): String {
        return "Badge(name='$name', location='$location', dateCollected=${getDisplayDate(true, true)})"
    }

    fun getDisplayDate(includeTime: Boolean = false, fullTime: Boolean = false): String {
        return if (dateCollected == null){
            "Unknown"
        } else {
            if (includeTime){
                val minute = dateCollected!!.get(Calendar.MINUTE)
                var stringMinute = minute.toString()
                if (minute < 10){
                    stringMinute = minute.toString().padStart(2, '0')
                }
                if (fullTime){
                    "${dateCollected!!.get(Calendar.HOUR_OF_DAY)}:${stringMinute}:${dateCollected!!.get(Calendar.SECOND)}:${dateCollected!!.get(Calendar.MILLISECOND)} ${dateCollected!!.get(Calendar.DAY_OF_MONTH)}/${dateCollected!!.get(Calendar.MONTH) + 1}/${dateCollected!!.get(Calendar.YEAR)}"
                } else {
                    "${dateCollected!!.get(Calendar.HOUR_OF_DAY)}:${stringMinute} ${dateCollected!!.get(Calendar.DAY_OF_MONTH)}/${dateCollected!!.get(Calendar.MONTH) + 1}/${dateCollected!!.get(Calendar.YEAR)}"
                }

            } else {
                "${dateCollected!!.get(Calendar.DAY_OF_MONTH)}/${dateCollected!!.get(Calendar.MONTH) + 1}/${
                    dateCollected!!.get(
                        Calendar.YEAR
                    )
                }"
            }
        }
    }

    // override sorting
    override fun compareTo(other: Badge): Int {
        return if (dateCollected == null || other.dateCollected == null){
            0
        } else {
            dateCollected!!.compareTo(other.dateCollected!!)
        }
    }

    fun share(context: Context, message: String? = "I've just climbed ${name} and collected the badge in the Hiker's Challenge app!") {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }

        context.startActivity(Intent.createChooser(shareIntent, "Share badge"))
    }


}
