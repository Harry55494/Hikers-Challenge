package com.example.hikerschallenge

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import java.io.Serializable


class BadgesModel(private val context: Context): Serializable {

    private val tag = "BadgesModel"
    private val trackedBadges = mutableListOf<DataBadge>()
    private val allBadges = mutableMapOf<String, DataBadge>()
    var userBadges = mutableListOf<UserBadge>()

    init {
        Log.i(tag, "BadgesModel init")

        // Load User Badges

        val file = "data.json"
        val json = context.assets.open(file).bufferedReader().use { it.readText() }
        val obj = JSONObject(json)
        val badges = obj.getJSONArray("badges")
        for (i in 0 until badges.length()) {
            val badgeJSON = badges.getJSONObject(i)
            val id = badgeJSON.getString("id")
            val verification = badgeJSON.getString("verification")
            val db = DataBadge(id, verification)

            db.setFields(
                badgeJSON.getString("name"),
                badgeJSON.getString("location"),
                badgeJSON.getString("location_2"),
            )
            allBadges[id] = db
        }


        loadModel()
        sortUserBadges()

        Log.i(tag, "BadgesModel created")
    }

    fun getDataBadge(id: String): DataBadge {
        return allBadges[id]!!
    }

    fun getAllBadges(): List<DataBadge> {
        return allBadges.values.toList()
    }

    fun isBadgeCollected(id: String): Boolean {
        return userBadges.any { it.dataID == id}
    }

    fun getTracked(): List<DataBadge> {
        return trackedBadges
    }

    fun addTrackedBadge(id: String) {
        val badge = getDataBadge(id)
        trackedBadges.add(badge)
        saveModel()
    }

    fun removeTrackedBadge(id: String) {
        for (badge in trackedBadges) {
            if (badge.id == id) {
                trackedBadges.remove(badge)
                Log.i(tag, "'$badge' badge removed from wantToCollect")
                break
            }
        }
        saveModel()
    }

    fun claimUserBadge(id: String, verification: String): UserBadge {
        val dateCollected = java.util.Calendar.getInstance()
        val dataBadge = getDataBadge(id)
        if (dataBadge.verification != verification){
            throw Exception("Verification code incorrect")
        }
        val newBadge = UserBadge(dataBadge, dateCollected)

        removeTrackedBadge(id)

        userBadges.add(newBadge)
        Log.i(tag, "'$newBadge' badge added")
        saveModel()
        Log.i(tag, newBadge.toString())

        return newBadge
    }

    fun removeUserBadge(id: String){
        val userBadge = userBadges.find { it.dataID == id }!!
        userBadges.remove(userBadge)
        Log.i(tag, "'$userBadge' badge removed")
        saveModel()
        Log.i(tag, userBadge.toString())
    }

    fun saveModel(){
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()

        val badgesJSON = Gson().toJson(userBadges)
        editor.putString("badgesJSON", badgesJSON)

        val wantToCollectIDs = mutableListOf<String>()
        for (badge in trackedBadges){
            wantToCollectIDs.add(badge.id)
        }
        val wantToCollectJSON = Gson().toJson(wantToCollectIDs)
        editor.putString("wantToCollectJSON", wantToCollectJSON)

        editor.apply()
        Log.i(tag, "Model saved, $badgesJSON")
    }

    private fun loadModel() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val badgesJson = sharedPreferences.getString("badgesJSON", null)

        if (badgesJson != null) {
            val badgesListType = object : TypeToken<List<UserBadge>>() {}.type
            userBadges = Gson().fromJson(badgesJson, badgesListType)
        }

        // Add any missing data
        for (userBadge in userBadges){
            val dataBadge = getDataBadge(userBadge.dataID)
            userBadge.name = dataBadge.name
            userBadge.localLocation = dataBadge.localLocation
            userBadge.country = dataBadge.countryLocation
        }

        val wantToCollectJson = sharedPreferences.getString("wantToCollectJSON", null)
        if (wantToCollectJson != null) {
            for (id in Gson().fromJson(wantToCollectJson, Array<String>::class.java)){
                trackedBadges.add(getDataBadge(id))
            }
        }

        Log.i(tag, "Model loaded")
    }


    override fun toString(): String {
        return "BadgesModel(badges=$userBadges)"
    }

    fun observe(badgesObserver: Observer<BadgesModel>) {
        badgesObserver.onChanged(this)

    }

    fun sortUserBadges(){
        Log.i(tag, "Sorting badges")
        //Log.i(tag, userBadges.toString())
        userBadges.sort()
        //Log.i(tag, userBadges.toString())
    }


}