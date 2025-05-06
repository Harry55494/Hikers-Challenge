package com.phillingham.hikerschallenge
import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AppViewModel(@SuppressLint("StaticFieldLeak") private val context: Context) : ViewModel() {
    // Main App View Model, used for storing data that needs to be accessed across fragments
    // Badges model stores all badge data
    var badgesModel: BadgesModel? = BadgesModel(context)
    // Weather data stores the current weather from the API call
    var weatherData: Array<String> = arrayOf("", "")
    // Weather data live is a live data version of weather data for updating without refreshing the fragment
    var weatherDataLive: MutableLiveData<Array<String>> = MutableLiveData(weatherData)
    // QR value stores the value of the QR code scanned
    var qrvalue: MutableLiveData<String> = MutableLiveData("")
    // Home image stores the URI of the image selected for the home screen
    var homeImage: Uri? = null

}
    class BadgesViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    // Factory for creating the AppViewModel
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            return AppViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}