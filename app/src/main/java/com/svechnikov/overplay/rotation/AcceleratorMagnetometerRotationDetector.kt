package com.svechnikov.overplay.rotation

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class AcceleratorMagnetometerRotationDetector : RotationDetector, SensorEventListener {

    private lateinit var listener: (x: Int, y: Int, z: Int) -> Unit

    private var sensorManager: SensorManager? = null

    private var accelerometerData: FloatArray? = null

    private var magnetometerData: FloatArray? = null

    private val R = FloatArray(9)

    private val R2 = FloatArray(9)

    private val orientation = FloatArray(3)

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                accelerometerData = event.values
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                magnetometerData = event.values
            }
        }

        val accelerometerData = accelerometerData
        val magnetometerData = magnetometerData

        if (
            accelerometerData != null &&
            magnetometerData != null &&
            SensorManager.getRotationMatrix(R, null, accelerometerData, magnetometerData)
        ) {
            SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_X,SensorManager.AXIS_Z, R2)
            SensorManager.getOrientation(R2, orientation)

            listener(
                toDegrees(orientation[1]),
                toDegrees(orientation[0]),
                toDegrees(orientation[2]),
            )
            this.accelerometerData = null
            this.magnetometerData = null
        }
    }

    private fun toDegrees(value: Float): Int = Math.toDegrees(value.toDouble()).toInt()

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    override fun setListener(listener: (x: Int, y: Int, z: Int) -> Unit) {
        this.listener = listener
    }

    override fun start(sensorManager: SensorManager) {
        this.sensorManager = sensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun stop() {
        sensorManager?.unregisterListener(this)
    }
}