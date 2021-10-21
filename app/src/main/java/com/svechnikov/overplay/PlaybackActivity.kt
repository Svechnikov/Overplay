package com.svechnikov.overplay

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.svechnikov.overplay.rotation.GyroscopeRotationDetector
import com.svechnikov.overplay.rotation.handlers.DefaultRotationHandler

class PlaybackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createDefaultGame().start(this)
    }

    private fun createDefaultGame(): Game {
        val playerView = StyledPlayerView(this).also(::setContentView)
        val player = SimpleExoPlayer.Builder(this).build()
        val rotationDetector = GyroscopeRotationDetector()
        val sensorEvents = SensorEvents(this, rotationDetector)
        val rotationHandler = DefaultRotationHandler(player)

        return Game.createDefault(player, playerView, sensorEvents, rotationHandler)
    }
}