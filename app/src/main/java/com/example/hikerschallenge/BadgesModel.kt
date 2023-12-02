package com.example.hikerschallenge

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import androidx.lifecycle.Observer
import java.io.Serializable
import java.util.Calendar

class BadgesModel(private val context: Context): Serializable {

    private val tag = "BadgesModel"
    var badges = mutableListOf<Badge>()

    init {
        Log.i(tag, "BadgesModel init")
        loadBadges()
        sortBadges()
        Log.i(tag, "BadgesModel created")
    }

    fun addBadge(badge: Badge){
        badges.add(badge)
        Log.i(tag, "'$badge' badge added")
        saveBadges()
        Log.i(tag, badge.toString())
    }

    fun removeBadge(badge: Badge){
        badges.remove(badge)
        Log.i(tag, "'$badge' badge removed")
        saveBadges()
        Log.i(tag, badge.toString())
    }

    fun saveBadges(){
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        val badgeNames = mutableSetOf<String>()
        val badgeLocations = mutableSetOf<String>()
        val badgeDates = mutableSetOf<String>()
        for (badge in badges){
            badgeNames.add(badge.name)
            badgeLocations.add(badge.location)
            badgeDates.add(badge.getDisplayDate(true))
        }
        editor.putStringSet("badgeNames", badgeNames)
        editor.putStringSet("badgeLocations", badgeLocations)
        editor.putStringSet("badgeDates", badgeDates)
        editor.apply()
        Log.i(tag, "Badges saved")
        Log.i(tag, badgeNames.toString())
    }

    private fun loadBadges() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val badgeNames = sharedPreferences.getStringSet("badgeNames", setOf<String>())
        val badgeLocations = sharedPreferences.getStringSet("badgeLocations", setOf<String>())
        val badgeDates = sharedPreferences.getStringSet("badgeDates", setOf<String>())
        Log.i(tag, "Badge names: $badgeNames")
        Log.i(tag, "Badge locations: $badgeLocations")
        Log.i(tag, "Badge dates: $badgeDates")
        if (badgeNames == null || badgeLocations == null) {
            Log.i(tag, "No badges saved")
            return
        } else {
            val iteratorNames = badgeNames.iterator()
            val iteratorLocations = badgeLocations.iterator()
            val iteratorDates = badgeDates?.iterator()

            while (iteratorNames.hasNext() && iteratorLocations.hasNext() && iteratorDates?.hasNext() == true) {
                val badgeName = iteratorNames.next()
                val badgeLocation = iteratorLocations.next()
                val badgeDate = iteratorDates.next()
                // convert date string to Calendar object
                val datetime = badgeDate.split(" ")
                val time = datetime[0]
                val hoursMinutes = time.split(":")
                val date = datetime[1]
                val badgeDateSplit = date.split("/")
                Log.i(tag, badgeDateSplit.toString())
                val badgeDateCalendar = Calendar.getInstance()
                badgeDateCalendar.set(Calendar.HOUR_OF_DAY, hoursMinutes[0].toInt())
                badgeDateCalendar.set(Calendar.MINUTE, hoursMinutes[1].toInt())
                badgeDateCalendar.set(Calendar.DAY_OF_MONTH, badgeDateSplit[0].toInt())
                badgeDateCalendar.set(Calendar.MONTH, badgeDateSplit[1].toInt() - 1)
                badgeDateCalendar.set(Calendar.YEAR, badgeDateSplit[2].toInt())
                badges.add(Badge(badgeName, badgeLocation, badgeDateCalendar))
            }
        }

        Log.i(tag, "Badges loaded")
    }


    override fun toString(): String {
        return "BadgesModel(badges=$badges)"
    }

    fun observe(badgesObserver: Observer<BadgesModel>) {
        badgesObserver.onChanged(this)

    }

    private fun sortBadges(){
        badges.sort()
    }


}