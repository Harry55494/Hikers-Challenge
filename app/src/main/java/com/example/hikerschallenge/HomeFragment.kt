package com.example.hikerschallenge

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
    private val tag = "HomeFragment"
    private val appViewModel by activityViewModels<AppViewModel>()
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            Log.i("PhotoPicker", "Selected URI: $uri")
            view?.let { updateImage(it, uri) }
        } else {
            Log.i("PhotoPicker", "No media selected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i(tag, "onCreate() run")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

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

            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

            val mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1 * 1000)
                .setFastestInterval(5 * 1000)

            fusedLocationClient.requestLocationUpdates(mLocationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val locationList = locationResult.locations
                    if (locationList.isNotEmpty()) {
                        val location = locationList.last()
                        Log.i(tag, "Location found")
                        Log.i(tag, "Location: ${location.latitude}, ${location.longitude}")

                        val myBuilder = CronetEngine.Builder(requireActivity())
                        val cronetEngine = myBuilder.build()
                        val executor = Executors.newSingleThreadExecutor()
                        val requestCallback = URLRequestCallback(appViewModel)

                        val requestBuilder = cronetEngine.newUrlRequestBuilder(
                            // Please don't steal my API key, I am but a lowly student :)
                            "https://api.openweathermap.org/data/2.5/weather?lat=${location.latitude}&lon=${location.longitude}&appid=732af6bb744b0c60aa2041cd423fbe50&units=metric",
                            requestCallback,
                            executor
                        )
                        val request = requestBuilder.build()
                        request.start()
                    }
                }
            }, null)

        }

        val weatherObserver = androidx.lifecycle.Observer<Array<String>> { weatherData ->
            val weatherText = view.findViewById<android.widget.TextView>(R.id.weatherTextView)
            if (!weatherData.contentEquals(arrayOf("", ""))){
                "${weatherData[0]}°C ${weatherData[1]}".also { weatherText.text = it }
            } else {
                "".also { weatherText.text = it }
            }
            Log.i(tag, "weatherObserver triggered")
        }
        appViewModel.weatherDataLive.observe(viewLifecycleOwner, weatherObserver)

        val badgesRecyclerView = view.findViewById<RecyclerView>(R.id.recently_collected_scroller)
        badgesRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val badgesObserver = androidx.lifecycle.Observer<BadgesModel> { badgesModel ->
            badgesModel.let {
                badgesRecyclerView.adapter = BadgesAdapterHorizontal(appViewModel, "user", "reverse")
            }
        }

        appViewModel.badgesModel?.observe(badgesObserver)

        val badgesRecyclerView2 = view.findViewById<RecyclerView>(R.id.want_to_collect_scroller)
        badgesRecyclerView2.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val badgesObserver2 = androidx.lifecycle.Observer<BadgesModel> { badgesModel ->
            badgesModel.let {
                badgesRecyclerView2.adapter = BadgesAdapterHorizontal(appViewModel, "wanted", "normal")
            }
        }

        appViewModel.badgesModel?.observe(badgesObserver2)


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

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val homeImage = sharedPreferences.getString("homeImage", null)
        if (homeImage != null){
            updateImage(view, Uri.parse(homeImage))
        }

        Log.i(tag, "onCreateView() run")
        return view
    }

    private fun updateImage(view: View, uri: Uri? = null){
        val photoCard = view.findViewById<androidx.cardview.widget.CardView>(R.id.cardview)
        val imageView = photoCard.findViewById<ImageView>(R.id.imageView)
        Log.i("PhotoPicker", "Updating image to $uri")

        if (uri == null){
            val image = ResourcesCompat.getDrawable(resources, R.drawable.template_home_image, null)
            imageView.setImageDrawable(image)
            imageView.postInvalidate()
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

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putString("homeImage", uri.toString())
        editor.apply()
        Log.i("PhotoPicker", "Image '${uri.toString()}' saved to shared preferences")

    }

}