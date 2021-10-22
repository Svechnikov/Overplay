package com.svechnikov.overplay.location

import android.content.Context
import android.location.Location
import mad.location.manager.lib.Interfaces.LocationServiceInterface
import mad.location.manager.lib.Services.ServicesHelper

class KalmanFilterLocationProvider(
    private val context: Context,
) : LocationProvider, LocationServiceInterface {

    private lateinit var listener: (Location) -> Unit

    override fun locationChanged(location: Location) = listener(location)

    override fun setListener(listener: (Location) -> Unit) {
        this.listener = listener
    }

    override fun start() {
        ServicesHelper.addLocationServiceInterface(this)
        ServicesHelper.getLocationService(context) {
            it.start()
        }
    }

    override fun stop() {
        ServicesHelper.removeLocationServiceInterface(this)
        ServicesHelper.getLocationService().stop()
    }
}