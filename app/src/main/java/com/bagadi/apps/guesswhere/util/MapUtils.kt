package com.bagadi.apps.guesswhere.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import java.io.IOException
import java.util.*

/**
 * Created by Santosh on 10/20/2016.
 */

object MapUtils {

    fun getAddressForLatLng(context: Context, latLng: LatLng): Address? {
        var errorMessage = ""
        var addresses: List<Address>? = null

        val geocoder = Geocoder(context, Locale.getDefault())

        try {
            addresses = geocoder.getFromLocation(
                    latLng.latitude,
                    latLng.longitude,
                    // In this sample, get just a single address.
                    1)
        } catch (ioException: IOException) {
            // Catch network or other I/O problems.
            errorMessage = "Network I/O exception."
        } catch (illegalArgumentException: IllegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = (errorMessage + ". Latitude = " + latLng.latitude + ", Longitude = "
                    + latLng.longitude)
        }

        // Handle case where no address was found.
        val address: Address?
        if (addresses == null || addresses.size == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = "Address is empty"
                Log.e(Constants.TAG, errorMessage)
            }

            address = null
        } else {
            address = addresses[0]
            val addressFragments = ArrayList<String>()



            errorMessage = address.countryName

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            //            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
            //                addressFragments.add(address.getAddressLine(i));
            //            }
            //            errorMessage = TextUtils.join(System.getProperty("line.separator"), addressFragments);
        }

        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()

        return address
    }
}
