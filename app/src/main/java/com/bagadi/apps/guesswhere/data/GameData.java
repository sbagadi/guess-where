package com.bagadi.apps.guesswhere.data;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Santosh on 10/8/2016.
 */
public class GameData {

    private int mNumberOfRounds;

    private List<RoundData> mRoundsList;

    public GameData() {
        // Empty constructor needed for Firebase DB.
    }

    public GameData(int mNumberOfRounds, List<RoundData> mRoundsList) {
        this.mNumberOfRounds = mNumberOfRounds;
        this.mRoundsList = mRoundsList;
    }

    public List<RoundData> getRoundsList() {
        return mRoundsList;
    }

    public int getNumberOfRounds() {
        return mNumberOfRounds;
    }
}