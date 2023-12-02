package com.example.hikerschallenge
import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.io.Serializable

class BadgesViewModel(@SuppressLint("StaticFieldLeak") private val context: Context) : ViewModel(), Serializable {
    var locationPermissionGranted: Boolean = false
    var badgesModel: BadgesModel? = BadgesModel(context)
    var dataModel: DataModel? = DataModel(context)
    var weatherData: Array<String> = arrayOf("", "")
    var weatherDataLive: MutableLiveData<Array<String>> = MutableLiveData(weatherData)
    var qrvalue: MutableLiveData<String> = MutableLiveData("")

}

    class BadgesViewModelFactory(private val badgesModel: BadgesModel, private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BadgesViewModel::class.java)) {
            return BadgesViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}