package com.bagadi.apps.guesswhere.ui;

import android.annotation.SuppressLint;
import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bagadi.apps.guesswhere.R;
import com.bagadi.apps.guesswhere.data.GameData;
import com.bagadi.apps.guesswhere.data.RoundData;
import com.bagadi.apps.guesswhere.util.DataUtils;
import com.bagadi.apps.guesswhere.util.MapUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GameActivity extends AppCompatActivity implements OnStreetViewPanoramaReadyCallback,
        OnMapReadyCallback {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private FloatingActionButton mStreetMapToggleFab;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    private SupportStreetViewPanoramaFragment mStreetViewPanoramaFragment;

    private SupportMapFragment mMapFragment;

    private StreetViewPanorama mStreetViewPanorama;

    private GameData mGameData = DataUtils.generateSampleGameData();

    private int mCurrentRound;

    private boolean mIsSmallMap;

    private LatLng mSelectedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mVisible = true;
        mStreetMapToggleFab = (FloatingActionButton) findViewById(R.id.street_map_toggle_fab);
        mContentView = findViewById(R.id.fullscreen_content);

        mStreetMapToggleFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startNextRound();

                if (mMapFragment.isHidden()) {
                    showMapFragment();
                } else {
                    hideMapFragment();
                }
            }
        });

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
//        mStreetMapToggleFab.setOnTouchListener(mDelayHideTouchListener);

        StreetViewPanoramaOptions panoramaOptions =
                new StreetViewPanoramaOptions().streetNamesEnabled(false);

        mStreetViewPanoramaFragment =
                SupportStreetViewPanoramaFragment.newInstance(panoramaOptions);
        mStreetViewPanoramaFragment.getStreetViewPanoramaAsync(GameActivity.this);
        mStreetViewPanoramaFragment.setRetainInstance(true);

        replaceFragment(mStreetViewPanoramaFragment, R.id.street_view_fragment_container);

        GoogleMapOptions options = new GoogleMapOptions();
        options.rotateGesturesEnabled(false).tiltGesturesEnabled(false);

        mMapFragment = SupportMapFragment.newInstance(options);
//        hideMapFragment();
        mMapFragment.getMapAsync(this);
        mMapFragment.setRetainInstance(true);

        replaceFragment(mMapFragment, R.id.map_fragment_container);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private void replaceFragment(Fragment fragment, int id) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(id, fragment);
        transaction.commit();
    }

    private void showMapFragment() {
        mMapFragment.getMapAsync(this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.show(mMapFragment);
        mStreetMapToggleFab.setImageResource(R.drawable.ic_streetview_white_24px);
        transaction.commit();
    }

    private void hideMapFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.hide(mMapFragment);
        mStreetMapToggleFab.setImageResource(R.drawable.ic_map_white_24dp);
        transaction.commit();
    }

    private Fragment getCurrentFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        return fragmentManager.findFragmentById(R.id.street_view_fragment_container);
    }

    private void startNextRound() {
        if (mCurrentRound < mGameData.getNumberOfRounds()) {
            mCurrentRound++;
            RoundData roundData = mGameData.getRoundsList().get(mCurrentRound - 1);

            if (mStreetViewPanorama != null) {
                mStreetViewPanorama.setPosition(roundData.getLocationData().getLatLng());
            }
        }
    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        mStreetViewPanorama = streetViewPanorama;
        startNextRound();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                LatLng actualPosition = mGameData
                        .getRoundsList().get(mCurrentRound -1).getLocationData().getLatLng();
                mSelectedPosition = latLng;
                float[] result = new float[1];
                Location.distanceBetween(
                        actualPosition.latitude,
                        actualPosition.longitude,
                        mSelectedPosition.latitude,
                        mSelectedPosition.longitude,
                        result);

                MapUtils.getAddressForLatLng(GameActivity.this, latLng);

                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(latLng));
            }
        });
    }
}
