package com.bagadi.apps.guesswhere.data;

import android.location.Address;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Santosh on 10/8/2016.
 */

public class LocationData {

    private String mCountry;
    private String mState;
    private LatLng mLatLng;

    public LocationData() {
        // Empty constructor needed for Firebase DB.
    }

    public LocationData(String country, String state, LatLng location) {
        this.mCountry = country;
        this.mState = state;
        this.mLatLng = location;
    }

    public LatLng getLatLng() {
        return mLatLng;
    }
}
