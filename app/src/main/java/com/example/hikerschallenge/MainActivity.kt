package com.example.hikerschallenge

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private var tag = "MainActivity"

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {

        Log.i(tag, "onCreate() run")

        val badgesModel = BadgesModel(this)
        val viewModelFactory = BadgesViewModelFactory(this)
        ViewModelProvider(this, viewModelFactory).get(AppViewModel::class.java)

        val activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                Log.i(tag, "${it.key} = ${it.value}")
            }
        }

        activityResultLauncher.launch(arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_MEDIA_IMAGES))

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        val navController = findNavController(R.id.nav_fragment)
        bottomNavigationView.setupWithNavController(navController)

        Log.i(tag, "Main Activity Loaded")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(tag, "Main Activity Destroyed")
    }

}
