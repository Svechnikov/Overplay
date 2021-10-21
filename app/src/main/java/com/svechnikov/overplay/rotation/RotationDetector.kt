package com.svechnikov.overplay.rotation

import android.hardware.SensorManager

interface RotationDetector {
    fun setListener(listener: (x: Int, y: Int, z: Int) -> Unit)
    fun start(sensorManager: SensorManager)
    fun stop()
}