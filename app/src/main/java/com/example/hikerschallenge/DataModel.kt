package com.example.hikerschallenge

import android.content.Context
import android.util.Log
import org.json.JSONObject

class DataModel(context: Context) {

    private val tag = "DataModel"
    private val allBadges = mutableMapOf<String, Badge>()

    init {

        val file = "data.json"
        val json = context.assets.open(file).bufferedReader().use { it.readText() }

        val obj = JSONObject(json)
        val badges = obj.getJSONArray("badges")
        for (i in 0 until badges.length()) {
            val badge = badges.getJSONObject(i)
            val id = badge.getString("id")
            val verification = badge.getString("verification")
            val name = badge.getString("name")
            val location = badge.getString("location")
            allBadges["$id,$verification"] = Badge(name, location, null, false)
        }
        Log.i(tag, "DataModel created")
    }

    fun getBadgeInfo(idVerification: String): Badge {
        return allBadges[idVerification]!!
    }

}
