package com.phillingham.hikerschallenge

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import org.chromium.net.CronetEngine
import java.util.concurrent.Executors

class HomeFragment : Fragment() {
    // Home fragment, used for the home screen

    // Variables for the fragment
    private val tag = "HomeFragment"
    // Get the view model
    private val appViewModel by activityViewModels<AppViewModel>()
    // Register for activity result
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            Log.i("PhotoPicker", "Selected URI: $uri")
            view?.let { updateImage(it, uri) }
        } else {
            Log.i("PhotoPicker", "No media selected")
        }
    }

    // On create
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(tag, "onCreate() run")
    }

    // On create view
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Check for permissions
        if (ActivityCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.i(tag, "Location permission not granted")
        } else {
            // Log the state of the permissions
            for (permission in arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_MEDIA_IMAGES
            )) {
                if (ContextCompat.checkSelfPermission(
                        requireActivity(),
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Log.i(tag, "Permission $permission not granted")
                } else {
                    Log.i(tag, "Permission $permission granted")
                }
            }

            // Make location request if permissions are granted
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            val mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1 * 1000)
                .setFastestInterval(5 * 1000)

            // Request location updates
            fusedLocationClient.requestLocationUpdates(mLocationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    // Check if location is found
                    val locationList = locationResult.locations
                    if (locationList.isNotEmpty()) {
                        // Get the location
                        val location = locationList.last()
                        // Log the location
                        Log.i(tag, "Location found")
                        Log.i(tag, "Location: ${location.latitude}, ${location.longitude}")

                        // Build a cronet request to get the weather data
                        val myBuilder = CronetEngine.Builder(requireActivity())
                        val cronetEngine = myBuilder.build()
                        val executor = Executors.newSingleThreadExecutor()
                        val requestCallback = URLRequestCallback(appViewModel)

                        val api_key = ""

                        // Build the request
                        val requestBuilder = cronetEngine.newUrlRequestBuilder(
                            // Please don't steal my API key, I am but a lowly student :)

                            //Edit: Can't believe I left it in here thinking this project wouldn't go anywhere... at least its been cancelled now, but leaving the code to demo
                            "https://api.openweathermap.org/data/2.5/weather?lat=${location.latitude}&lon=${location.longitude}&appid=$api_key&units=metric",
                            requestCallback,
                            executor
                        )
                        // Send off the request
                        // It will get the weather data and add it to the view model on its own
                        val request = requestBuilder.build()
                        request.start()
                    }
                }
            }, null)

        }

        // Create observer for the weather data
        val weatherObserver = androidx.lifecycle.Observer<Array<String>> { weatherData ->
            val weatherText = view.findViewById<android.widget.TextView>(R.id.weatherTextView)
            // If the data is not blank, set the text to the data
            if (!weatherData.contentEquals(arrayOf("", ""))){
                "${weatherData[0]}°C ${weatherData[1]}".also { weatherText.text = it }
            } else {
                // Otherwise, set it to "" to hide it
                "".also { weatherText.text = it }
            }
            Log.i(tag, "weatherObserver triggered")
        }
        // Add the observer to the view model
        appViewModel.weatherDataLive.observe(viewLifecycleOwner, weatherObserver)


        // Setup the recycler views for the badges
        // First is the recently claimed badges
        val badgesRecyclerView = view.findViewById<RecyclerView>(R.id.recently_collected_scroller)
        badgesRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val badgesObserver = androidx.lifecycle.Observer<BadgesModel> { badgesModel ->
            badgesModel.let {
                badgesRecyclerView.adapter = BadgesAdapterHorizontal(appViewModel, "user", "reverse")
            }
        }
        appViewModel.badgesModel?.observe(badgesObserver)

        // Second is the badges the user has tracked
        val badgesRecyclerView2 = view.findViewById<RecyclerView>(R.id.want_to_collect_scroller)
        badgesRecyclerView2.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val badgesObserver2 = androidx.lifecycle.Observer<BadgesModel> { badgesModel ->
            badgesModel.let {
                badgesRecyclerView2.adapter = BadgesAdapterHorizontal(appViewModel, "tracked", "normal")
            }
        }
        appViewModel.badgesModel?.observe(badgesObserver2)

        // Setup the photo picker
        val photoCard = view.findViewById<androidx.cardview.widget.CardView>(R.id.cardview)
        photoCard.setOnLongClickListener { view ->

            if (appViewModel.homeImage != null) {
                val alertDialog = AlertDialog(requireContext())
                alertDialog.showAlertOptions("Change Photo", "Do you want to remove the photo, or pick a new one?", "Remove", {appViewModel.homeImage = null; updateImage(view)}, "Pick New") {
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
                updateImage(view)
            } else {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

            true }

        // Load the image from shared preferences
        // Doesnt currently work due to permissions issues :(
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val homeImage = sharedPreferences.getString("homeImage", null)
        if (homeImage != null){
            updateImage(view, Uri.parse(homeImage))
        }

        Log.i(tag, "onCreateView() run")
        return view
    }

    // Function used to update the image
    private fun updateImage(view: View, uri: Uri? = null){
        // Get the image view
        val photoCard = view.findViewById<androidx.cardview.widget.CardView>(R.id.cardview)
        val imageView = photoCard.findViewById<ImageView>(R.id.imageView)
        Log.i("PhotoPicker", "Updating image to $uri")

        // If the uri is null, set the image to the default
        if (uri == null){
            val image = ResourcesCompat.getDrawable(resources, R.drawable.template_home_image, null)
            imageView.setImageDrawable(image)
            imageView.postInvalidate()
        // Otherwise, set the image to the uri
        } else {
            val uri2 = Uri.parse(uri.toString())
            try {
                val inputStream = requireActivity().contentResolver.openInputStream(uri2)
                val drawable = Drawable.createFromStream(inputStream, uri2.toString())
                imageView.setImageDrawable(drawable)
                // post invalidate to redraw the view
                imageView.postInvalidate()
                Log.i("PhotoPicker", "Image updated")
            } catch (e: Exception){
                Log.e("PhotoPicker", "Error: $e")
            }
        }

        // Save the uri to shared preferences after setting the image
        // This bit does technically work, its just the loading that doesnt...
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putString("homeImage", uri.toString())
        editor.apply()
        Log.i("PhotoPicker", "Image '${uri.toString()}' saved to shared preferences")

    }

}