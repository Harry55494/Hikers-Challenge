package com.example.hikerschallenge

import android.util.Log

class DataBadge(val id: String, val verification: String) {

    lateinit var name: String
    lateinit var localLocation: String
    lateinit var countryLocation: String
    lateinit var countryflag: String

    init {
        Log.i("DataBadge", "DataBadge with id '$id' created")

    }

    fun setFields(name: String, location: String, country: String){
        this.name = name
        this.localLocation = location
        this.countryLocation = country
        this.countryflag = getCountryFlag(country)
    }

    override fun equals(other: Any?): Boolean {
        // sort by location2, then name
        if (other is DataBadge){
            return if (countryLocation == null || other.countryLocation == null){
                if (localLocation != other.localLocation){
                    localLocation == other.localLocation
                } else {
                    name == other.name
                }
            } else {
                if (countryLocation != other.countryLocation){
                    countryLocation == other.countryLocation
                } else if (localLocation != other.localLocation){
                    localLocation == other.localLocation
                } else {
                    name == other.name
                }
            }
        }

        return false

    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + localLocation.hashCode()
        result = 31 * result + (countryLocation?.hashCode() ?: 0)
        result = 31 * result + verification.hashCode()
        return result
    }

    fun getCountryFlag(country: String): String{
        if (country.lowercase() == "united kingdom"){
            return "🇬🇧"
        } else if (country.lowercase() == "switzerland"){
            return "🇨🇭"
        }
        return ""
    }

}