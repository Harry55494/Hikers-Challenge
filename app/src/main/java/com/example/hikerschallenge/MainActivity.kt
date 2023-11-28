package com.example.hikerschallenge

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private var tag = "MainActivity"
    private val modelKey = "Model"
    private var badgesViewModel: BadgesViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        val badgesViewModel = ViewModelProvider(this).get(BadgesViewModel::class.java)

        val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            isGranted: Boolean ->
            if (isGranted) {
                badgesViewModel.locationPermissionGranted = true
                Log.i(tag, "Location permission granted")
            } else {
                Log.i(tag, "Location permission denied")
            }
        }

        requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)

        val activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                Log.i(tag, "${it.key} = ${it.value}")
            }
        }

        activityResultLauncher.launch(arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.CAMERA))

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        val navController = findNavController(R.id.nav_fragment)
        bottomNavigationView.setupWithNavController(navController)

        Log.i(tag, "Main Activity Loaded")
    }

    private fun getModel(): BadgesViewModel {
        if (badgesViewModel == null) {
            badgesViewModel = BadgesViewModel()
        }
        return badgesViewModel as BadgesViewModel
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putSerializable(modelKey, getModel())
        super.onSaveInstanceState(savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        Log.i(tag, "onCreate Running")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(tag, "Main Activity Destroyed")
    }

}
