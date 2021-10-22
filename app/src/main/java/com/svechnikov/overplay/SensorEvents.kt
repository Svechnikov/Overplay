package com.svechnikov.overplay

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.SensorManager
import android.location.Location
import com.squareup.seismic.ShakeDetector
import com.svechnikov.overplay.location.LocationProvider
import com.svechnikov.overplay.rotation.RotationDetector

class SensorEvents(
    context: Context,
    private val rotationDetector: RotationDetector,
    private val locationProvider: LocationProvider,
) : ShakeDetector.Listener {

    private val sensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager

    private val shakeDetector = ShakeDetector(this)

    private lateinit var shakeListener: () -> Unit

    override fun hearShake() = shakeListener()

    fun setLocationListener(listener: (Location) -> Unit) = locationProvider.setListener(listener)

    fun setShakeListener(listener: () -> Unit) {
        shakeListener = listener
    }

    fun setRotationListener(listener: (x: Int, y: Int, z: Int) -> Unit) {
        rotationDetector.setListener(listener)
    }

    fun start() {
        shakeDetector.start(sensorManager)
        rotationDetector.start(sensorManager)
        locationProvider.start()
    }

    fun stop() {
        shakeDetector.stop()
        rotationDetector.stop()
        locationProvider.stop()
    }
}