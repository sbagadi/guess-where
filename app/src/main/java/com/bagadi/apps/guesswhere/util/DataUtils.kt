package com.bagadi.apps.guesswhere.util

import com.bagadi.apps.guesswhere.data.GameData
import com.bagadi.apps.guesswhere.data.LocationData
import com.bagadi.apps.guesswhere.data.RoundData
import com.google.android.gms.maps.model.LatLng
import java.util.*

/**
 * Created by Santosh on 10/9/2016.
 */

object DataUtils {

    fun generateSampleGameData(): GameData {

        val locationsList = ArrayList<LocationData>(4)

        val homeLocationData = LocationData("USA", "California", LatLng(37.3984252, -121.9450896))
        locationsList.add(homeLocationData)

        val sydneyLocationData = LocationData("Australia", "New South Wales", LatLng(-33.87365, 151.20689))
        locationsList.add(sydneyLocationData)

        val tajMahalLocationData = LocationData("India", "Uttar Pradesh", LatLng(27.1722044, 78.0422475))
        locationsList.add(tajMahalLocationData)

        val londonBrideData = LocationData("United Kingdom", "Lonndon", LatLng(51.507834, -0.0877309))
        locationsList.add(londonBrideData)

        val roundDataList = ArrayList<RoundData>(4)

        for (locationData in locationsList) {
            val roundData = RoundData(locationData)
            roundDataList.add(roundData)
        }

        return GameData(4, roundDataList)
    }

    private fun calculateScore(roundData: RoundData) {
        val score = 0

        val actualLatLng = roundData.locationData.latLng
        val selectedLatLng = roundData.selectedLocation



        roundData.score = score
    }
}
