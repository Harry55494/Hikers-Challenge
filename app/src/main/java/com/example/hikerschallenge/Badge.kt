package com.example.hikerschallenge

import android.util.Log
import java.io.Serializable
import java.util.Calendar

data class Badge(var name: String, var location: String = "Unknown", var dateCollected: Calendar? = null, var announceCreation: Boolean = true): Serializable, Comparable<Badge> {

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
                if (fullTime){
                    "${dateCollected!!.get(Calendar.HOUR_OF_DAY)}:${dateCollected!!.get(Calendar.MINUTE)}:${dateCollected!!.get(Calendar.SECOND)}:${dateCollected!!.get(Calendar.MILLISECOND)} ${dateCollected!!.get(Calendar.DAY_OF_MONTH)}/${dateCollected!!.get(Calendar.MONTH) + 1}/${dateCollected!!.get(Calendar.YEAR)}"
                } else {
                    "${dateCollected!!.get(Calendar.HOUR_OF_DAY)}:${dateCollected!!.get(Calendar.MINUTE)} ${dateCollected!!.get(Calendar.DAY_OF_MONTH)}/${dateCollected!!.get(Calendar.MONTH) + 1}/${dateCollected!!.get(Calendar.YEAR)}"
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


}
