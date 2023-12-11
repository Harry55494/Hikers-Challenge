package com.example.hikerschallenge

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
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

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "badge_model"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    private val tag = "HomeFragment"
    private val appViewModel by activityViewModels<AppViewModel>()
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            Log.i("PhotoPicker", "Selected URI: $uri")
            appViewModel.homeImage = uri
            updateImage()
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
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.i(tag, "Location permission not granted")
        } else {
            Log.i(tag, "Location permission granted, submitting request for location")

            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

            val mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1 * 1000)
                .setFastestInterval(5 * 1000);

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
                badgesRecyclerView.adapter = BadgesAdapterHorizontal(appViewModel, "user")
            }
        }

        appViewModel.badgesModel?.observe(badgesObserver)

        val badgesRecyclerView2 = view.findViewById<RecyclerView>(R.id.want_to_collect_scroller)
        badgesRecyclerView2.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val badgesObserver2 = androidx.lifecycle.Observer<BadgesModel> { badgesModel ->
            badgesModel.let {
                badgesRecyclerView2.adapter = BadgesAdapterHorizontal(appViewModel, "wanted")
            }
        }

        appViewModel.badgesModel?.observe(badgesObserver2)


        val photoCard = view.findViewById<androidx.cardview.widget.CardView>(R.id.cardview)
        photoCard.setOnLongClickListener { view ->

            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

            true }

        if (appViewModel.homeImage != null){
            updateImage()
        }

        Log.i(tag, "onCreateView() run")
        return view
    }

    private fun updateImage(){
        val imageView = view?.findViewById<ImageView>(R.id.imageView)
        var URI = appViewModel.homeImage ?: return
        URI = Uri.parse(URI.toString())
        if (imageView == null){
            Log.i("PhotoPicker", "imageView is null")
            return
        }
        try {
            val inputStream = requireActivity().contentResolver.openInputStream(URI)
            val drawable = Drawable.createFromStream(inputStream, URI.toString())
            imageView.setImageDrawable(drawable)
            Log.i("PhotoPicker", "Image updated")
        } catch (e: Exception){
            Log.e("PhotoPicker", "Error: $e")
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param badge_model Parameter 1.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(badge_model: BadgesModel) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, badge_model.toString())
                }
            }
    }
}