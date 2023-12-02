package com.example.hikerschallenge

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import androidx.lifecycle.Observer
import java.io.Serializable
import java.util.Calendar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


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

        val badgesJSON = Gson().toJson(badges)
        editor.putString("badgesJSON", badgesJSON)

        editor.apply()
        Log.i(tag, "Badges saved, $badgesJSON")
    }

    private fun loadBadges() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val badgesJson = sharedPreferences.getString("badgesJSON", null)

        if (badgesJson != null) {
            val badgesListType = object : TypeToken<List<Badge>>() {}.type
            badges = Gson().fromJson(badgesJson, badgesListType)
        }
        Log.i(tag, "Badges loaded")
    }


    override fun toString(): String {
        return "BadgesModel(badges=$badges)"
    }

    fun observe(badgesObserver: Observer<BadgesModel>) {
        badgesObserver.onChanged(this)

    }

    fun sortBadges(){
        Log.i(tag, "Sorting badges")
        Log.i(tag, badges.toString())
        badges.sort()
        Log.i(tag, badges.toString())
    }


}