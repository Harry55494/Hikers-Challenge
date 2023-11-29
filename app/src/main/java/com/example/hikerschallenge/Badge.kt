package com.example.hikerschallenge

import android.util.Log
import java.io.Serializable

data class Badge(var name: String, var location: String = "Unknown"): Serializable {

    private val tag = "BadgeClass"

    init {
        Log.i(tag, "'$name' created")
    }

    override fun toString(): String {
        return "Badge(name='$name')"
    }

}
