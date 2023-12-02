package com.example.hikerschallenge

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private val badgesViewModel by activityViewModels<BadgesViewModel>()

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
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    if (location != null) {
                        Log.i(tag, "Location found")
                        Log.i(tag, "Location: ${location.latitude}, ${location.longitude}")

                        val myBuilder = CronetEngine.Builder(requireActivity())
                        val cronetEngine = myBuilder.build()
                        val executor = Executors.newSingleThreadExecutor()
                        val requestCallback = URLRequestCallback(this.badgesViewModel)

                        val requestBuilder = cronetEngine.newUrlRequestBuilder(
                            "https://api.openweathermap.org/data/2.5/weather?lat=${location.latitude}&lon=${location.longitude}&appid=732af6bb744b0c60aa2041cd423fbe50&units=metric",
                            requestCallback,
                            executor
                        )
                        val request = requestBuilder.build()
                        request.start()

                    } else {
                        Log.i(tag, "Location not found")
                    }
                }

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
        badgesViewModel.weatherDataLive.observe(viewLifecycleOwner, weatherObserver)

        val badgesRecyclerView = view.findViewById<RecyclerView>(R.id.horizontal_scroller)
        badgesRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val badgesObserver = androidx.lifecycle.Observer<BadgesModel> { badgesModel ->
            badgesModel.let {
                badgesRecyclerView.adapter = BadgesAdapter(badgesModel.badges)
            }
        }

        badgesViewModel.badgesModel?.observe(badgesObserver)


        Log.i(tag, "onCreateView() run")
        return view
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