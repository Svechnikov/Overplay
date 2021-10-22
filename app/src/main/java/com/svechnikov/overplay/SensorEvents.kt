package com.svechnikov.overplay

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.SensorManager
import com.squareup.seismic.ShakeDetector
import com.svechnikov.overplay.moving.MoveAwayDetector
import com.svechnikov.overplay.rotation.RotationDetector

class SensorEvents(
    context: Context,
    private val rotationDetector: RotationDetector,
    private val moveAwayDetector: MoveAwayDetector,
) : ShakeDetector.Listener {

    private val sensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager

    private val shakeDetector = ShakeDetector(this)

    private lateinit var shakeListener: () -> Unit

    override fun hearShake() = shakeListener()

    fun setOnMovedAwayListener(listener: () -> Unit) = moveAwayDetector.setListener(listener)

    fun setShakeListener(listener: () -> Unit) {
        shakeListener = listener
    }

    fun setRotationListener(listener: (x: Int, y: Int, z: Int) -> Unit) {
        rotationDetector.setListener(listener)
    }

    fun start() {
        shakeDetector.start(sensorManager)
        rotationDetector.start(sensorManager)
        moveAwayDetector.start()
    }

    fun stop() {
        shakeDetector.stop()
        rotationDetector.stop()
        moveAwayDetector.stop()
    }
}