package com.phillingham.hikerschallenge

import android.util.Log
import org.chromium.net.CronetException
import org.chromium.net.UrlRequest
import org.chromium.net.UrlResponseInfo
import java.nio.ByteBuffer


class URLRequestCallback(private val viewModel: AppViewModel) : UrlRequest.Callback() {
    // URLRequestCallback, used for getting weather data from the OpenWeather API
    // Some functions are not used, but are required for the UrlRequest.Callback() class

    private val tag = "URLRequestCallback"
    private var weatherData = ArrayList<String>()

    override fun onRedirectReceived(
        request: UrlRequest?,
        info: UrlResponseInfo?,
        newLocationUrl: String?
    ) {
        Log.i(tag, "onRedirectReceived() run")
    }

    override fun onResponseStarted(request: UrlRequest?, info: UrlResponseInfo?) {
        Log.i(tag, "onResponseStarted() run")
        Log.i(tag, info.toString())
        // decode the response body
        val buffer = ByteBuffer.allocateDirect(1024)
        request!!.read(buffer)
    }

    // This function is called when the response body is read
    override fun onReadCompleted(
        request: UrlRequest?,
        info: UrlResponseInfo?,
        byteBuffer: ByteBuffer?
    ) {
        byteBuffer?.clear()
        request!!.read(byteBuffer)
        var StringResponse = String(byteBuffer!!.array())
        StringResponse = StringResponse.replace("?","")
        Log.i(tag, StringResponse)
        // get the temperature and conditions from the response
        val temperature = StringResponse.substringAfter("temp\":").substringBefore(",")
        val conditions = StringResponse.substringAfter("main\":\"").substringBefore("\"")
        // save the data to the temporary array
        weatherData = arrayListOf(temperature, conditions)
        Log.i(tag, temperature)
        Log.i(tag, conditions)
        Log.i(tag, "onReadCompleted() run")
    }

    // This function is called when the request is finished
    // Saves the data to the view model
    override fun onSucceeded(request: UrlRequest?, info: UrlResponseInfo?) {
        Log.i(tag, info.toString())
        val roundedDegrees = weatherData[0].toDouble().toInt()
        // Get the emoji for the weather conditions
        val dataEmoji = when (weatherData[1]) {
            "Clouds" -> "\uD83C\uDF24"
            "Clear" -> "\uD83C\uDF1E"
            "Rain" -> "\uD83C\uDF27"
            "Snow" -> "\uD83C\uDF28"
            else -> "\uD83C\uDF1E"
        }
        viewModel.weatherData = arrayOf(roundedDegrees.toString(), dataEmoji)
        viewModel.weatherDataLive.postValue(viewModel.weatherData)
        Log.i(tag, "onSucceeded() run")
    }

    override fun onFailed(request: UrlRequest?, info: UrlResponseInfo?, error: CronetException?) {
        Log.i(tag, "onFailed() run")
        Log.e(tag, info.toString())
        Log.e(tag, error.toString())
    }

}
