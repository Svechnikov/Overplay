package com.svechnikov.overplay

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.svechnikov.overplay.location.KalmanFilterLocationProvider
import com.svechnikov.overplay.moving.LocationMoveAwayDetector
import com.svechnikov.overplay.rotation.GyroscopeRotationDetector
import com.svechnikov.overplay.rotation.handlers.DefaultRotationHandler

class PlaybackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT > 28) {
            checkPermissions()
        } else {
            checkPermissionsV28()
        }
    }

    private fun checkPermissionsV28() {
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            startDefaultGame()
        }.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun checkPermissions() {
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            startDefaultGame()
        }.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACTIVITY_RECOGNITION,
            )
        )
    }

    private fun startDefaultGame() = createDefaultGame().start(this)

    private fun createDefaultGame(): Game {
        val playerView = StyledPlayerView(this).also(::setContentView)
        val player = SimpleExoPlayer.Builder(this).build()
        val rotationDetector = GyroscopeRotationDetector()
        val locationProvider = KalmanFilterLocationProvider(this)
        val moveAwayDetector = LocationMoveAwayDetector(locationProvider).apply {
            distanceMeters = 10f
        }
        val sensorEvents = SensorEvents(this, rotationDetector, moveAwayDetector)
        val rotationHandler = DefaultRotationHandler(player)

        return Game.createDefault(player, playerView, sensorEvents, rotationHandler)
    }
}