package com.svechnikov.overplay.moving

import android.location.Location
import com.svechnikov.overplay.location.LocationProvider

class LocationMoveAwayDetector(
    private val locationProvider: LocationProvider,
) : MoveAwayDetector {

    private lateinit var listener: () -> Unit

    private var prevLocation: Location? = null

    override var distanceMeters: Float = 10f

    init {
        locationProvider.setListener(::onLocationChanged)
    }

    private fun onLocationChanged(location: Location) {
        prevLocation?.distanceTo(location)?.let {
            if (it >= distanceMeters) {
                listener()
                prevLocation = location
            }
        }
        if (prevLocation == null) {
            prevLocation = location
        }
    }

    override fun setListener(listener: () -> Unit) {
        this.listener = listener
    }

    override fun start() = locationProvider.start()

    override fun stop() = locationProvider.stop()
}