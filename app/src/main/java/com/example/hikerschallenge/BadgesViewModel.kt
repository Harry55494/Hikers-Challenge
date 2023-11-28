package com.example.hikerschallenge
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.Serializable

class BadgesViewModel : ViewModel(), Serializable {
    var locationPermissionGranted: Boolean = false
    var badgesModel: BadgesModel? = BadgesModel()
    var weatherData: Array<String> = arrayOf("", "")
    var weatherDataLive: MutableLiveData<Array<String>> = MutableLiveData(weatherData)
}
