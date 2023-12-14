package com.example.hikerschallenge

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class ScanFragment : Fragment() {
    // Scan fragment for scanning QR codes

    // Variables for view model and logging
    private val appViewModel by activityViewModels<AppViewModel>()
    private val tag = "ScanFragment"

    // Function for when a QR code is scanned
    private fun badgeScanned(){
        val scannedQR = appViewModel.qrvalue.value!!
        Log.i(tag, "QR code scanned: $scannedQR")

        // First check if the QR code is valid
        if (!isQRValid(scannedQR)){
            Log.i(tag, "Invalid QR code")
            // Show a dialog box if not
            val alertDialog = AlertDialog(this.requireContext())
            alertDialog.showAlert(
                "Invalid QR code",
                "The QR code you scanned is invalid. Please try again.")
            return
        }

        // Otherwise, split the badge QR code into the ID and verification
        val id = scannedQR.split(",")[0]
        val verification = scannedQR.split(",")[1]
        // Check if the badge is already in the wallet
        if (appViewModel.badgesModel!!.userBadges.any { it.dataID == id }){
            Log.i(tag, "Badge already in wallet")
            // Show a dialog box if it is
            val badgeName = appViewModel.badgesModel!!.getDataBadge(id).name
            val alertDialog = AlertDialog(this.requireContext())
            alertDialog.showAlert(
                "Badge already in wallet",
                "You already have the $badgeName badge in your wallet!")
            return
        }
        // Otherwise, claim the badge
        val newBadge = appViewModel.badgesModel!!.claimUserBadge(id, verification)
        appViewModel.badgesModel!!.sortUserBadges()
        appViewModel.badgesModel!!.saveModel()
        // Show a dialog box
        val alertDialog = AlertDialog(this.requireContext())
        alertDialog.showAlert(
            "Badge collected!",
            "You have collected the ${newBadge.name} badge! You can view it in your wallet.")
        Log.i(tag, "badgeScanned() run")
    }

    // On create
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(tag, "onCreate() run")
        Log.i(tag, appViewModel.badgesModel.toString())
    }

    // On create view, inflate the layout and add the camera fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_scan, container, false)

        // Add listener to add badge button
        val button = view.findViewById<android.widget.Button>(R.id.claimBadgeButton)
        button.setOnClickListener {
            badgeScanned()
        }
        val scanButtonHint = view.findViewById<android.widget.TextView>(R.id.scanButtonHint)

        // Check if camera permission is granted
        // If not, ask the user to enable it
        val permissionCheck = context?.checkCallingOrSelfPermission(android.Manifest.permission.CAMERA)!!
        val response = permissionCheck == 0
        if (!response){
            Log.i(tag, "Camera permission not granted")
            scanButtonHint.text = getString(R.string.please_enable_camera)

        }

        // add camera fragment to the fragment container
        val cameraFragment = CameraFragment()
        val transaction = childFragmentManager.beginTransaction()
        transaction.add(R.id.camera_fragment_container, cameraFragment)
        transaction.commit()


        // create listener to show add badge button when QR code is scanned
        appViewModel.qrvalue.observe(viewLifecycleOwner) { newValue ->
            Log.i(tag, "Making button visible")
            Log.i(tag, "qrvalue observer triggered")
            if (newValue != "") {
                button.visibility = View.VISIBLE
                scanButtonHint.visibility = View.GONE
            } else {
                button.visibility = View.GONE
            }
        }


        return view
    }

    // Function to check if a QR code is valid
    // First tries to split the QR code into two parts, then checks if the ID is valid
    private fun isQRValid(qr: String): Boolean{
        if (qr.split(",").size != 2){
            return false
        }
        val id = qr.split(",")[0]
        for (badge in appViewModel.badgesModel!!.getAllBadges()){
            if (badge.id == id){
                return true
            }
        }
        return false

    }

}