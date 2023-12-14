package com.example.hikerschallenge

import android.util.Log

class DataBadge(val id: String, val verification: String) {
    // DataBadge, holds the data for a badge

    // Variables for holding the data
    lateinit var name: String
    lateinit var localLocation: String
    lateinit var countryLocation: String
    lateinit var countryflag: String

    init {
        Log.i("DataBadge", "DataBadge with id '$id' created")
    }

    // Set the fields after creation
    fun setFields(name: String, location: String, country: String){
        this.name = name
        this.localLocation = location
        this.countryLocation = country
        this.countryflag = getCountryFlag(country)
    }

    // Equals and hashcode functions, used for sorting
    override fun equals(other: Any?): Boolean {
        // sort by country, then location, then name
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

    // Hashcode function, used for sorting
    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 5 * result + localLocation.hashCode()
        result = 10 * result + countryLocation.hashCode()
        result = 15 * result + verification.hashCode()
        return result
    }

    // Get the country flag emoji
    private fun getCountryFlag(country: String): String{
        if (country.lowercase() == "united kingdom"){
            return "🇬🇧"
        } else if (country.lowercase() == "switzerland"){
            return "🇨🇭"
        }
        return ""
    }

}