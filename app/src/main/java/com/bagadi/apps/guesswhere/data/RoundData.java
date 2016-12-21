package com.bagadi.apps.guesswhere.data;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Santosh on 10/8/2016.
 */
public class RoundData {

    private LocationData mLocationData;
    private LatLng mSelectedLocation;
    private int mScore;

    public RoundData() {
    }

    public RoundData(LocationData locationData, int points) {
        this.mLocationData = locationData;
        this.mScore = points;
    }

    public LocationData getLocationData() {
        return mLocationData;
    }

    public int getScore() {
        return mScore;
    }

    public void setScore(int score) {
        this.mScore = score;
    }

    public LatLng getSelectedLocation() {
        return mSelectedLocation;
    }

    public void setSelectedLocation(LatLng selectedLocation) {
        this.mSelectedLocation = selectedLocation;
    }
}
