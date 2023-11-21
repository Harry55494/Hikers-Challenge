package com.example.hikerschallenge

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

    override fun toString(): String {
        return "BadgesModel(badges=$badges)"
    }


}