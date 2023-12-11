package com.example.hikerschallenge

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment() : Fragment() {

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var barcodeScanner: BarcodeScanner
    private val tag = "CameraFragment"
    private val appViewModel by activityViewModels<AppViewModel>()
    private var threadRunning = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraExecutor = Executors.newSingleThreadExecutor()
        barcodeScanner = BarcodeScanning.getClient()

        startCamera()

    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val previewView =
                view?.findViewById<androidx.camera.view.PreviewView>(R.id.previewView)

            val preview = Preview.Builder().build().also {
                if (previewView != null) {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            val imageAnalysis = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { image ->
                        analyseForQRCode(image)
                    }
                }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this as LifecycleOwner, cameraSelector, preview, imageAnalysis
                )
            } catch (e: Exception) {
                Log.e(tag, "Error binding camera", e)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    @OptIn(ExperimentalGetImage::class) private fun analyseForQRCode(image: ImageProxy) {
        val inputImage = InputImage.fromMediaImage(image.image!!, image.imageInfo.rotationDegrees)

        barcodeScanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    val value = barcode.displayValue
                    if (value != null) {
                        appViewModel.qrvalue.postValue(value)
                    }
                    val l = appViewModel.qrvalue.value
                    Log.i(tag, "QR Code Value: $l")
                }
            }
            .addOnFailureListener { e ->
                Log.i(tag, "Error scanning QR code", e)
            }
            .addOnCompleteListener { barcodes ->
                image.close()
                // pause the camera
                /*
                val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
                cameraProviderFuture.addListener({
                    val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                    cameraProvider.unbindAll()
                }, ContextCompat.getMainExecutor(requireContext()))
                Log.i(tag, "Camera paused")*/
                if (barcodes.result.isEmpty()){
                    // set timeout on another thread coroutine to set the value to "" after 1 second
                    if (!threadRunning){
                        threadRunning = true
                        val timeoutThread = Thread {
                            Thread.sleep(1000)
                            if (barcodes.result.isEmpty()){
                                appViewModel.qrvalue.postValue("")
                            }
                            threadRunning = false
                        }
                        timeoutThread.start()
                    }

                }

            }
    }

    override fun onDestroy() {
        cameraExecutor.shutdown()
        appViewModel.qrvalue.postValue("")
        super.onDestroy()

    }

}
