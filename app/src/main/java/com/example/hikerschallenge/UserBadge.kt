package com.example.hikerschallenge

import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.Calendar

class UserBadge(db: DataBadge, private var dateCollected: Calendar? = null): Comparable<UserBadge>  {
    // UserBadge, holds the extra data for a badge in the user's wallet

    // Variables for holding the data
    // Mostly assigned from the DataBadge passed in
    private val tag: String = "UserBadge"
    var dataID: String = db.id
    var name: String = db.name
    var localLocation: String = db.localLocation
    var country: String = db.countryLocation

    init {
        Log.i(tag, "'$name' created")
        Log.i(tag, "dateCollected: $dateCollected")
    }

    override fun toString(): String {
        return "Badge(name='$name', dataID='$dataID', location='$localLocation', dateCollected=${getDisplayDate(true, true)})"
    }

    // Display date function, for displaying the date at various points in the app
    // Has different options for displaying the time
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
    override fun compareTo(other: UserBadge): Int {
        if (country == null || other.country == null){
            return if (localLocation != other.localLocation){
                localLocation.compareTo(other.localLocation)
            } else {
                name.compareTo(other.name)
            }
        }
        return if (country != other.country){
            country.compareTo(other.country)
        } else if (localLocation != other.localLocation){
            localLocation.compareTo(other.localLocation)
        } else {
            name.compareTo(other.name)
        }
    }

    // Share intent
    fun share(context: Context, message: String? = "I've just climbed ${name} and collected the badge in the Hiker's Challenge app!") {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }

        context.startActivity(Intent.createChooser(shareIntent, "Share badge"))
    }


}
