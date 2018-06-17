package com.bagadi.apps.guesswhere.ui

import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.bagadi.apps.guesswhere.R
import com.google.android.gms.maps.model.LatLng

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val fab = findViewById<View>(R.id.fab) as com.google.android.material.floatingactionbutton.FloatingActionButton
        fab.setOnClickListener { startActivity(Intent(this@MainActivity, GameActivity::class.java)) }

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.fragment_container, MainActivityFragment())
        fragmentTransaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)

    }

    companion object {

        // George St, Sydney
        private val SYDNEY = LatLng(-33.87365, 151.20689)
        private val HOME = LatLng(37.3984252, -121.9450896)
    }
}
