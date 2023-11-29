package com.example.hikerschallenge

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import androidx.lifecycle.Observer
import java.io.Serializable

class BadgesModel(private val context: Context): Serializable {

    private val tag = "BadgesModel"
    var badges = mutableListOf<Badge>()

    init {
        Log.i(tag, "BadgesModel init")
        loadBadges()
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
        for (badge in badges){
            badgeNames.add(badge.name)
            badgeLocations.add(badge.location)
        }
        editor.putStringSet("badgeNames", badgeNames)
        editor.putStringSet("badgeLocations", badgeLocations)
        editor.apply()
        Log.i(tag, "Badges saved")
        Log.i(tag, badgeNames.toString())
    }

    private fun loadBadges() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val badgeNames = sharedPreferences.getStringSet("badgeNames", setOf<String>())
        val badgeLocations = sharedPreferences.getStringSet("badgeLocations", setOf<String>())
        Log.i(tag, "Badge names: $badgeNames")
        Log.i(tag, "Badge locations: $badgeLocations")
        if (badgeNames == null || badgeLocations == null) {
            Log.i(tag, "No badges saved")
            return
        } else {
            val iteratorNames = badgeNames.iterator()
            val iteratorLocations = badgeLocations.iterator()

            while (iteratorNames.hasNext() && iteratorLocations.hasNext()) {
                val badgeName = iteratorNames.next()
                val badgeLocation = iteratorLocations.next()
                badges.add(Badge(badgeName, badgeLocation))
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


}