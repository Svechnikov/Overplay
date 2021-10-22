package com.svechnikov.overplay.location

import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager

class BasicLocationProvider(context: Context) : LocationProvider, LocationListener {

    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private lateinit var listener: (Location) -> Unit

    override fun onLocationChanged(location: Location) = listener(location)

    override fun setListener(listener: (Location) -> Unit) {
        this.listener = listener
    }

    override fun start() {
        locationManager.getBestProvider(
            Criteria().apply {
                accuracy = Criteria.ACCURACY_FINE
            },
            true,
        )?.let {
            // todo propagate exception instead of silently consuming it
            try {
                locationManager.requestLocationUpdates(it, 0L, 0f, this)
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    override fun stop() = locationManager.removeUpdates(this)
}