package com.example.hikerschallenge

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class ScanFragment : Fragment() {
    private val appViewModel by activityViewModels<AppViewModel>()
    private val tag = "ScanFragment"

    private fun badgeScanned(){
        val scannedQR = appViewModel.qrvalue.value!!
        Log.i(tag, "QR code scanned: $scannedQR")

        if (!isQRValid(scannedQR)){
            Log.i(tag, "Invalid QR code")
            val alertDialog = AlertDialog(this.requireContext())
            alertDialog.showAlert(
                "Invalid QR code",
                "The QR code you scanned is invalid. Please try again.")
            return
        }

        val id = scannedQR.split(",")[0]
        val verification = scannedQR.split(",")[1]
        if (appViewModel.badgesModel!!.userBadges.any { it.dataID == id }){
            Log.i(tag, "Badge already in wallet")
            val badgeName = appViewModel.badgesModel!!.getDataBadge(id).name
            val alertDialog = AlertDialog(this.requireContext())
            alertDialog.showAlert(
                "Badge already in wallet",
                "You already have the $badgeName badge in your wallet!")
            return
        }
        val newBadge = appViewModel.badgesModel!!.claimUserBadge(id, verification)
        appViewModel.badgesModel!!.sortUserBadges()
        appViewModel.badgesModel!!.saveModel()
        val alertDialog = AlertDialog(this.requireContext())
        alertDialog.showAlert(
            "Badge collected!",
            "You have collected the ${newBadge.name} badge! You can view it in your wallet.")
        Log.i(tag, "badgeScanned() run")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(tag, "onCreate() run")
        Log.i(tag, appViewModel.badgesModel.toString())
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
        val scanButtonHint = view.findViewById<android.widget.TextView>(R.id.scanButtonHint)

        val permissionCheck = context?.checkCallingOrSelfPermission(android.Manifest.permission.CAMERA)!!
        val response = permissionCheck == 0
        if (!response){
            Log.i(tag, "Camera permission not granted")
            scanButtonHint.text = getString(R.string.please_enable_camera)

        }

        // add camera fragment
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