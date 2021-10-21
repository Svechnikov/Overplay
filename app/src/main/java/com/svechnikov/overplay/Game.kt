package com.svechnikov.overplay

import androidx.lifecycle.LifecycleOwner
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.StyledPlayerView

interface Game {
    fun start(lifecycleOwner: LifecycleOwner)

    companion object {
        fun createDefault(
            player: SimpleExoPlayer,
            playerView: StyledPlayerView,
            sensorEvents: SensorEvents,
            rotationHandler: RotationHandler,
        ): Game = DefaultGame(player, playerView, sensorEvents, rotationHandler)
    }
}