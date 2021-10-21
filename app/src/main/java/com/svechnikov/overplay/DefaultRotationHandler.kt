package com.svechnikov.overplay

import com.google.android.exoplayer2.SimpleExoPlayer
import kotlin.math.abs

class DefaultRotationHandler(private val player: SimpleExoPlayer) : RotationHandler {

    private var lastRotationZEventTime = 0L

    private var lastRotationXEventTime = 0L

    override fun handleRotation(x: Int, y: Int, z: Int) {
        val time = System.currentTimeMillis()

        if (abs(z) > 50 && time - lastRotationZEventTime > 750) {
            if (z > 0) {
                player.seekBack()
            } else {
                player.seekForward()
            }
            lastRotationZEventTime = time
        }

        if (abs(x) > 50 && time - lastRotationXEventTime > 950) {
            if (x > 0) {
                player.decreaseDeviceVolume()
            } else {
                player.increaseDeviceVolume()
            }
            lastRotationXEventTime = time
        }
    }
}