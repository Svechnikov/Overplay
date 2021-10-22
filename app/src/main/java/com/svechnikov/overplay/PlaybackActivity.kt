package com.svechnikov.overplay

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.svechnikov.overplay.rotation.GyroscopeRotationDetector
import com.svechnikov.overplay.rotation.handlers.DefaultRotationHandler

class PlaybackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { granted ->
                if (granted) {
                    startDefaultGame()
                }
            }.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            startDefaultGame()
        }
    }

    private fun startDefaultGame() = createDefaultGame().start(this)

    private fun createDefaultGame(): Game {
        val playerView = StyledPlayerView(this).also(::setContentView)
        val player = SimpleExoPlayer.Builder(this).build()
        val rotationDetector = GyroscopeRotationDetector()
        val sensorEvents = SensorEvents(this, rotationDetector)
        val rotationHandler = DefaultRotationHandler(player)

        return Game.createDefault(player, playerView, sensorEvents, rotationHandler)
    }
}