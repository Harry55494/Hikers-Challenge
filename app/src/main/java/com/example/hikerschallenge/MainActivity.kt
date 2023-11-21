package com.example.hikerschallenge

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private var tag = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {


        val badgesViewModel = ViewModelProvider(this)[BadgesViewModel::class.java]
        badgesViewModel.badgesModel = BadgesModel()
        for (i in 1..15){
            badgesViewModel.badgesModel!!.addBadge(Badge("First Badge"))
        }


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        val navController = findNavController(R.id.nav_fragment)
        bottomNavigationView.setupWithNavController(navController)

        Log.i(tag, "Main Activity Loaded")
    }
}
