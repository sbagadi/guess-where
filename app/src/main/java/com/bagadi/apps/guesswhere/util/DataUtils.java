package com.bagadi.apps.guesswhere.util;

import com.bagadi.apps.guesswhere.data.GameData;
import com.bagadi.apps.guesswhere.data.LocationData;
import com.bagadi.apps.guesswhere.data.RoundData;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Santosh on 10/9/2016.
 */

public class DataUtils {

    public static GameData generateSampleGameData() {

        List<LocationData> locationsList = new ArrayList<>(4);

        LocationData homeLocationData =
                new LocationData("USA", "California", new LatLng(37.3984252,-121.9450896));
        locationsList.add(homeLocationData);

        LocationData sydneyLocationData =
                new LocationData("Australia", "New South Wales", new LatLng(-33.87365, 151.20689));
        locationsList.add(sydneyLocationData);

        LocationData tajMahalLocationData =
                new LocationData("India", "Uttar Pradesh", new LatLng(27.1722044,78.0422475));
        locationsList.add(tajMahalLocationData);

        LocationData londonBrideData =
                new LocationData("United Kingdom", "Lonndon", new LatLng(51.507834,-0.0877309));
        locationsList.add(londonBrideData);

        List<RoundData> roundDataList = new ArrayList<>(4);

        for (LocationData locationData : locationsList) {
            RoundData roundData = new RoundData(locationData, 0);
            roundDataList.add(roundData);
        }

        return new GameData(4, roundDataList);
    }

    private static void calculateScore(RoundData roundData) {
        int score = 0;

        LatLng actualLatLng = roundData.getLocationData().getLatLng();
        LatLng selectedLatLng = roundData.getSelectedLocation();



        roundData.setScore(score);
    }
}
