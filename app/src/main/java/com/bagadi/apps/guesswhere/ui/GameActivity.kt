package com.bagadi.apps.guesswhere.ui

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import com.bagadi.apps.guesswhere.R
import com.bagadi.apps.guesswhere.util.DataUtils
import com.bagadi.apps.guesswhere.util.MapUtils
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class GameActivity : AppCompatActivity(), OnStreetViewPanoramaReadyCallback, OnMapReadyCallback {
    private val mHideHandler = Handler()
    private var mContentView: View? = null
    private val mHidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        mContentView!!.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }
    private var mStreetMapToggleFab: FloatingActionButton? = null
    private val mShowPart2Runnable = Runnable {
        // Delayed display of UI elements
        val actionBar = supportActionBar
        actionBar?.show()
    }
    private var mVisible: Boolean = false
    private val mHideRunnable = Runnable { hide() }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val mDelayHideTouchListener = View.OnTouchListener { view, motionEvent ->
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS)
        }
        false
    }

    private lateinit var mStreetViewPanoramaFragment: SupportStreetViewPanoramaFragment
    private lateinit var mMapFragment: SupportMapFragment

    private var mStreetViewPanorama: StreetViewPanorama? = null
    private val mGameData = DataUtils.generateSampleGameData()
    private var mCurrentRound: Int = 0
    private val mIsSmallMap: Boolean = false
    private var mSelectedPosition: LatLng? = null

    private val currentFragment: Fragment
        get() {
            val fragmentManager = supportFragmentManager
            return fragmentManager.findFragmentById(R.id.street_view_fragment_container)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_game)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        mVisible = true
        mStreetMapToggleFab = findViewById<View>(R.id.street_map_toggle_fab) as FloatingActionButton
        mContentView = findViewById(R.id.fullscreen_content)

        mStreetMapToggleFab!!.setOnClickListener {
            //                startNextRound();

            if (mMapFragment!!.isHidden) {
                showMapFragment()
            } else {
                hideMapFragment()
            }
        }

        // Set up the user interaction to manually show or hide the system UI.
        mContentView!!.setOnClickListener { toggle() }

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //        mStreetMapToggleFab.setOnTouchListener(mDelayHideTouchListener);

        val panoramaOptions = StreetViewPanoramaOptions().streetNamesEnabled(false)

        mStreetViewPanoramaFragment = SupportStreetViewPanoramaFragment.newInstance(panoramaOptions)
        mStreetViewPanoramaFragment!!.getStreetViewPanoramaAsync(this@GameActivity)
        mStreetViewPanoramaFragment!!.retainInstance = true

        replaceFragment(mStreetViewPanoramaFragment!!, R.id.street_view_fragment_container)

        val options = GoogleMapOptions()
        options.rotateGesturesEnabled(false).tiltGesturesEnabled(false)

        mMapFragment = SupportMapFragment.newInstance(options)
        //        hideMapFragment();
        mMapFragment!!.getMapAsync(this)
        mMapFragment!!.retainInstance = true

        replaceFragment(mMapFragment!!, R.id.map_fragment_container)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun toggle() {
        if (mVisible) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Hide UI first
        val actionBar = supportActionBar
        actionBar?.hide()

        mVisible = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable)
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    @SuppressLint("InlinedApi")
    private fun show() {
        // Show the system bar
        mContentView!!.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        mVisible = true

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable)
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }

    private fun replaceFragment(fragment: Fragment, id: Int) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(id, fragment)
        transaction.commit()
    }

    private fun showMapFragment() {
        mMapFragment!!.getMapAsync(this)
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.show(mMapFragment)
        mStreetMapToggleFab!!.setImageResource(R.drawable.ic_streetview_white_24px)
        transaction.commit()
    }

    private fun hideMapFragment() {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.hide(mMapFragment)
        mStreetMapToggleFab!!.setImageResource(R.drawable.ic_map_white_24dp)
        transaction.commit()
    }

    private fun startNextRound() {
        if (mCurrentRound < mGameData.numberOfRounds) {
            mCurrentRound++
            val roundData = mGameData.roundsList[mCurrentRound - 1]

            if (mStreetViewPanorama != null) {
                mStreetViewPanorama!!.setPosition(roundData.locationData.latLng)
            }
        }
    }

    override fun onStreetViewPanoramaReady(streetViewPanorama: StreetViewPanorama) {
        mStreetViewPanorama = streetViewPanorama
        startNextRound()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.setOnMapClickListener { latLng ->
            val actualPosition = mGameData
                    .roundsList[mCurrentRound - 1].locationData.latLng
            mSelectedPosition = latLng
            val result = FloatArray(1)
            Location.distanceBetween(
                    actualPosition.latitude,
                    actualPosition.longitude,
                    mSelectedPosition!!.latitude,
                    mSelectedPosition!!.longitude,
                    result)

            MapUtils.getAddressForLatLng(this@GameActivity, latLng)

            googleMap.clear()
            googleMap.addMarker(MarkerOptions().position(latLng))
        }
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [.AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private val AUTO_HIDE = true

        /**
         * If [.AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private val UI_ANIMATION_DELAY = 300
    }
}
