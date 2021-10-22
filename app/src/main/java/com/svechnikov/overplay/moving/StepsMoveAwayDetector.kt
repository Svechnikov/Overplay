package com.svechnikov.overplay.moving

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class StepsMoveAwayDetector(context: Context) : MoveAwayDetector, SensorEventListener {

    private val sensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager

    override var distanceMeters: Float = 10f

    private var steps = 0f

    private lateinit var listener: () -> Unit

    override fun onSensorChanged(event: SensorEvent) {
        steps++
        if (stepsToMeters() >= distanceMeters) {
            steps = 0f
            listener()
        }
    }

    // An average step is 1.39 meters
    private fun stepsToMeters(): Float = steps * 1.39f

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    override fun setListener(listener: () -> Unit) {
        this.listener = listener
    }

    override fun start() {
        val stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        sensorManager.registerListener(this, stepDetector, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun stop() = sensorManager.unregisterListener(this)
}