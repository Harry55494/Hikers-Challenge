package com.example.hikerschallenge

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ScanFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ScanFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val badgesViewModel by activityViewModels<BadgesViewModel>()
    private val tag = "ScanFragment"

    fun badgeScanned(){
        val randomGenerator = java.util.Random()
        val randomName = randomGenerator.nextInt(1000).toString()
        badgesViewModel.badgesModel!!.badges.add(Badge(randomName))
        badgesViewModel.badgesModel!!.saveBadges(Bundle())
        Log.i(tag, "badgeScanned() run")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        Log.i(tag, "onCreate() run")
        Log.i(tag, badgesViewModel.badgesModel.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_scan, container, false)

        val button = view.findViewById<android.widget.Button>(R.id.addBadgeButton)
        button.setOnClickListener {
            badgeScanned()
        }

        // add camera fragment
        val cameraFragment = CameraFragment()
        val transaction = childFragmentManager.beginTransaction()
        transaction.add(R.id.camera_fragment_container, cameraFragment)
        transaction.commit()

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ScanFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}