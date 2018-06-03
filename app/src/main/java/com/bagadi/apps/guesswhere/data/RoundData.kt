package com.bagadi.apps.guesswhere.data

import com.google.android.gms.maps.model.LatLng

/**
 * Created by Santosh on 10/8/2016.
 */
data class RoundData(val locationData: LocationData) {
    var selectedLocation: LatLng? = null
    var score: Int = 0
}
