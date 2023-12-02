package com.example.hikerschallenge

import android.content.Context
import android.util.Log
import org.json.JSONObject

class DataModel(context: Context) {

    private val tag = "DataModel"
    private val allBadges = mutableListOf<Badge>()

    init {
        // json file data.json

        // for each badge in json file
        // allBadges.add(Badge(name, location, date))

        val file = "data.json"
        val json = context.assets.open(file).bufferedReader().use { it.readText() }

        val obj = JSONObject(json)
        val badges = obj.getJSONArray("badges")
        for (i in 0 until badges.length()) {
            val badge = badges.getJSONObject(i)
            val name = badge.getString("name")
            val location = badge.getString("location")
            allBadges.add(Badge(name, location))
        }

        Log.i(tag, allBadges.toString())

        Log.i(tag, "DataModel init")
    }

    fun getBadgeInfo(id: Int): Badge {
        return allBadges[id]
    }

}
