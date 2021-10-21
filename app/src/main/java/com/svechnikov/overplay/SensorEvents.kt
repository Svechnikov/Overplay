package com.svechnikov.overplay

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.SensorManager
import android.location.Location
import com.squareup.seismic.ShakeDetector
import com.svechnikov.overplay.rotation.GyroscopeRotationDetector

class SensorEvents(context: Context) : ShakeDetector.Listener {

    private val sensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager

    private val shakeDetector = ShakeDetector(this)

    private val rotationDetector = GyroscopeRotationDetector()

    private lateinit var shakeListener: () -> Unit

    override fun hearShake() = shakeListener()

    fun setLocationListener(listener: (Location) -> Unit) {

    }

    fun setShakeListener(listener: () -> Unit) {
        shakeListener = listener
    }

    fun setRotationListener(listener: (x: Int, y: Int, z: Int) -> Unit) {
        rotationDetector.setListener(listener)
    }

    fun start() {
        shakeDetector.start(sensorManager)
        rotationDetector.start(sensorManager)
    }

    fun stop() {
        shakeDetector.stop()
        rotationDetector.stop()
    }
}