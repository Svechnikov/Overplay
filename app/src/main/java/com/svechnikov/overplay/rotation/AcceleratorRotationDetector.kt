package com.svechnikov.overplay.rotation

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.asin
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round

// Based on https://github.com/konstantinvoronov/camerahorizon_overlay
class AcceleratorRotationDetector : RotationDetector, SensorEventListener {

    private var sensorManager: SensorManager? = null

    private val gravity = DoubleArray(3)

    private val force = DoubleArray(3)

    private lateinit var listener: (x: Int, y: Int, z: Int) -> Unit

    override fun onSensorChanged(event: SensorEvent) {
        val alpha = 0.3f
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0]
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1]
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2]

        force[0] = constrainGravity(gravity[0])
        force[1] = constrainGravity(gravity[1])
        force[2] = constrainGravity(gravity[2])

        listener(
            calculateAngle(force[2]),
            calculateAngle(force[1]),
            calculateAngle(force[0]),
        )
    }

    private fun constrainGravity(gravity: Double) = max(min(gravity, GRAVITY), -GRAVITY)

    private fun calculateAngle(force: Double) =
        (round(Math.toDegrees(asin(force / GRAVITY)) * 100) / 100).toInt()

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    override fun setListener(listener: (x: Int, y: Int, z: Int) -> Unit) {
        this.listener = listener
    }

    override fun start(sensorManager: SensorManager) {
        this.sensorManager = sensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST)
    }

    override fun stop() {
        sensorManager?.unregisterListener(this)
    }

    private companion object {
        const val GRAVITY = 9.81
    }
}