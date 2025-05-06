package com.phillingham.hikerschallenge

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import java.io.Serializable


class BadgesModel(private val context: Context): Serializable {
    // Badges Model, holds all the data badges and user badges

    // Variables for holding the data
    private val tag = "BadgesModel"
    private val trackedBadges = mutableListOf<DataBadge>()
    private val allBadges = mutableMapOf<String, DataBadge>()
    var userBadges = mutableListOf<UserBadge>()

    init {
        Log.i(tag, "BadgesModel init")

        // Load databadges from the json file
        val file = "data.json"
        val json = context.assets.open(file).bufferedReader().use { it.readText() }
        val obj = JSONObject(json)
        val badges = obj.getJSONArray("badges")
        for (i in 0 until badges.length()) {
            // Turn each badge into a DataBadge object and add it to the map
            val badgeJSON = badges.getJSONObject(i)
            val id = badgeJSON.getString("id")
            val verification = badgeJSON.getString("verification")
            val db = DataBadge(id, verification)

            // Set the fields
            db.setFields(
                badgeJSON.getString("name"),
                badgeJSON.getString("location"),
                badgeJSON.getString("location_2"),
            )
            allBadges[id] = db
        }

        // Load and sort user badges
        loadModel()
        sortUserBadges()

        Log.i(tag, "BadgesModel created")
    }

    // Getters and setters

    // Get a data badge from the map
    fun getDataBadge(id: String): DataBadge {
        return allBadges[id]!!
    }

    // Get all the data badges
    fun getAllBadges(): List<DataBadge> {
        return allBadges.values.toList()
    }

    // Check if a badge has been collected
    fun isBadgeCollected(id: String): Boolean {
        return userBadges.any { it.dataID == id}
    }

    // Get all the badges a user is currently tracking
    fun getTracked(): List<DataBadge> {
        return trackedBadges
    }

    // Add a badge to the tracked list
    fun addTrackedBadge(id: String) {
        val badge = getDataBadge(id)
        trackedBadges.add(badge)
        saveModel()
    }

    // Remove a badge from the tracked list
    // Works even if the badge isn't actually tracked
    fun removeTrackedBadge(id: String) {
        // Find the badge and remove it
        for (badge in trackedBadges) {
            if (badge.id == id) {
                trackedBadges.remove(badge)
                Log.i(tag, "'$badge' badge removed from wantToCollect")
                break
            }
        }
        saveModel()
    }

    // Claim a badge
    fun claimUserBadge(id: String, verification: String): UserBadge {
        val dateCollected = java.util.Calendar.getInstance()
        val dataBadge = getDataBadge(id)
        // Check if the verification code is correct
        if (dataBadge.verification != verification){
            throw Exception("Verification code incorrect")
        }

        // Create a new user badge and remove the tracked badge
        val newBadge = UserBadge(dataBadge, dateCollected)
        removeTrackedBadge(id)

        // Add the badge to the user badges list and save the model
        userBadges.add(newBadge)
        Log.i(tag, "'$newBadge' badge added")
        saveModel()
        Log.i(tag, newBadge.toString())

        return newBadge
    }

    // Remove a user badge
    fun removeUserBadge(id: String){
        val userBadge = userBadges.find { it.dataID == id }!!
        userBadges.remove(userBadge)
        Log.i(tag, "'$userBadge' badge removed")
        saveModel()
        Log.i(tag, userBadge.toString())
    }

    // Save the model using shared preferences
    fun saveModel(){
        // Get the shared preferences and editor
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()

        // Convert the badges to JSON and save them
        val badgesJSON = Gson().toJson(userBadges)
        editor.putString("badgesJSON", badgesJSON)

        // Convert the tracked badges to their IDs
        val wantToCollectIDs = mutableListOf<String>()
        for (badge in trackedBadges){
            wantToCollectIDs.add(badge.id)
        }

        // Convert the tracked list to JSON and save it
        val wantToCollectJSON = Gson().toJson(wantToCollectIDs)
        editor.putString("wantToCollectJSON", wantToCollectJSON)

        // Apply the changes
        editor.apply()
        Log.i(tag, "Model saved, $badgesJSON")
    }

    // Load the model from shared preferences
    private fun loadModel() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val badgesJson = sharedPreferences.getString("badgesJSON", null)

        // Load the badges from JSON
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

        // Load the tracked badges from JSON
        val wantToCollectJson = sharedPreferences.getString("wantToCollectJSON", null)
        if (wantToCollectJson != null) {
            for (id in Gson().fromJson(wantToCollectJson, Array<String>::class.java)){
                trackedBadges.add(getDataBadge(id))
            }
        }

        Log.i(tag, "Model loaded")
    }


    // toString function
    override fun toString(): String {
        return "BadgesModel(badges=$userBadges)"
    }

    // Observer function
    fun observe(badgesObserver: Observer<BadgesModel>) {
        badgesObserver.onChanged(this)

    }

    // Sort the user badges
    fun sortUserBadges(){
        Log.i(tag, "Sorting badges")
        //Log.i(tag, userBadges.toString())
        userBadges.sort()
        //Log.i(tag, userBadges.toString())
    }


}