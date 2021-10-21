package com.svechnikov.overplay.rotation

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class GyroscopeRotationDetector : RotationDetector, SensorEventListener {

    private var sensorManager: SensorManager? = null

    private val queue = SampleQueue()

    private lateinit var listener: (x: Int, y: Int, z: Int) -> Unit

    private var lastCalculationTime = 0L

    override fun onSensorChanged(event: SensorEvent) {
        val values = event.values
        queue.add(values[0], values[1], values[2], event.timestamp)

        val time = System.currentTimeMillis()
        when {
            lastCalculationTime == 0L -> {
                lastCalculationTime = time
            }
            time - lastCalculationTime >= CALCULATION_INTERVAL -> {
                val meanVelocity = queue.calculateMeanVelocity()

                listener(
                    Math.toDegrees(meanVelocity[0].toDouble()).toInt(),
                    Math.toDegrees(meanVelocity[1].toDouble()).toInt(),
                    Math.toDegrees(meanVelocity[2].toDouble()).toInt(),
                )

                lastCalculationTime = time
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    override fun setListener(listener: (x: Int, y: Int, z: Int) -> Unit) {
        this.listener = listener
    }

    override fun start(sensorManager: SensorManager) {
        this.sensorManager = sensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun stop() {
        sensorManager?.unregisterListener(this)
        queue.clear()
    }

    private data class Sample(
        var x: Float = 0f,
        var y: Float = 0f,
        var z: Float = 0f,
        var time: Long = 0L,
        var next: Sample? = null,
    )

    private class SampleQueue {

        private val pool = SamplePool()

        private var oldest: Sample? = null

        private var newest: Sample? = null

        private var sampleCount = 0

        private val meanVelocity = FloatArray(3)

        private val velocitySum = FloatArray(3)

        fun add(x: Float, y: Float, z: Float, time: Long) {
            purge(time - MAX_WINDOW_SIZE)

            val sample = pool.acquire().also {
                it.x = x
                it.y = y
                it.z = z
                it.time = time
            }
            newest?.next = sample
            newest = sample
            if (oldest == null) {
                oldest = sample
            }
            sampleCount++
        }

        fun calculateMeanVelocity(): FloatArray {
            val oldest = oldest
            val newest = newest

            if (oldest == null || newest == null || newest.time - oldest.time < MIN_WINDOW_SIZE) {
                meanVelocity[0] = 0f
                meanVelocity[1] = 0f
                meanVelocity[2] = 0f
                return meanVelocity
            }

            velocitySum[0] = 0f
            velocitySum[1] = 0f
            velocitySum[2] = 0f

            var sample = oldest
            while (sample != null) {
                velocitySum[0] += sample.x
                velocitySum[1] += sample.y
                velocitySum[2] += sample.z
                sample = sample.next
            }

            meanVelocity[0] = velocitySum[0] / sampleCount
            meanVelocity[1] = velocitySum[1] / sampleCount
            meanVelocity[2] = velocitySum[2] / sampleCount

            return meanVelocity
        }

        fun clear() {
            var oldest = oldest
            while (oldest != null) {
                val removed = oldest
                oldest = oldest.next
                pool.release(removed)
            }
            this.oldest = null
            this.newest = null
            sampleCount = 0
        }

        private fun purge(cutoff: Long) {
            var oldest = oldest

            while (sampleCount >= MIN_QUEUE_SIZE && oldest != null && oldest.time < cutoff) {
                val removed = oldest
                sampleCount--
                oldest = removed.next
                this.oldest = oldest
                pool.release(removed)
            }
        }

        companion object {
            const val MAX_WINDOW_SIZE: Long = 500000000
            const val MIN_WINDOW_SIZE = 250000000
            const val MIN_QUEUE_SIZE = 4
        }
    }

    private class SamplePool {
        private var head: Sample? = null

        fun acquire(): Sample {
            var sample = head
            if (sample == null) {
                sample = Sample()
            } else {
                head = sample.next
            }
            return sample
        }

        fun release(sample: Sample) {
            sample.next = head
            head = sample
        }
    }

    private companion object {
        const val CALCULATION_INTERVAL = 100L
    }
}