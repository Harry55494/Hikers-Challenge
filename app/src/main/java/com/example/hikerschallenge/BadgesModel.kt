package com.example.hikerschallenge

import android.os.Bundle
import android.util.Log
import java.io.Serializable

class BadgesModel(): Serializable {

    private val tag = "BadgesModel"
    var badges = mutableListOf<Badge>()

    init {
        Log.i(tag, "BadgesModel created")
    }

    fun addBadge(badge: Badge){
        badges.add(badge)
        Log.i(tag, "'$badge' badge added")
        Log.i(tag, badge.toString())
    }

    fun saveBadges(bundle: Bundle){
        bundle.putSerializable("badges", this)
        Log.i(tag, "Badges saved")
    }

    fun loadBadges(bundle: Bundle){
        val badgesModel = bundle.getSerializable("badges") as BadgesModel
        Log.i(tag, badgesModel.toString())
        this.badges = badgesModel.badges
        Log.i(tag, "Badges loaded")
    }

    override fun toString(): String {
        return "BadgesModel(badges=$badges)"
    }


}