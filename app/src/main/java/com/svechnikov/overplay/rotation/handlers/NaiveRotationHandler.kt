package com.svechnikov.overplay.rotation.handlers

import com.google.android.exoplayer2.SimpleExoPlayer
import kotlin.math.abs

class NaiveRotationHandler(private val player: SimpleExoPlayer) : RotationHandler {
    override fun handleRotation(x: Int, y: Int, z: Int) {
        if (abs(z) > 20) {
            if (z > 0) {
                player.seekBack()
            } else {
                player.seekForward()
            }
        }

        if (abs(x) > 20) {
            if (x > 0) {
                player.decreaseDeviceVolume()
            } else {
                player.increaseDeviceVolume()
            }
        }
    }
}